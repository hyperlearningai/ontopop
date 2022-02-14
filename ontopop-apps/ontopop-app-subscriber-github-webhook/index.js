/**
 * GitHub Webhook Subscriber
 *
 * A simple HTTP server application written in Node.js that will be 
 * requested by the Git webhook (POST). This application simply sends the 
 * filtered headers along with the original request body payload to the 
 * Ontology Ingestion Service URL as a HTTP POST request using the Axios 
 * library, or to a RabbitMQ messaging queue using the amqplib library.
 * The Axios libary returns a promise which we do NOT handle - this is 
 * necessary as GitHub Webhook requests timeout after 10 seconds, after which
 * the HTTP connection is destroyed and the webhook payload lost, so an
 * immediate response is required.
 *
 * @author jillurquddus
 * @since  2.0.0
 */

const amqp = require('amqplib/callback_api');
const axios = require('axios');
const http = require('http');
const url = require('url');

const requestListener = async function (req, res) {

    // Log the HTTP request headers for debugging purposes
    console.log("New HTTP POST request: GitHub webhook.");
    console.log("GitHub webhook headers: " + JSON.stringify(req.headers));

    // Log the HTTP request body payload for debugging purposes
    let requestBody = '';
    req.on('data', chunk => {
        requestBody += chunk.toString();
    });
    await req.on('end', () => {
        console.log("GitHub webhook payload: " + requestBody);
    });
    
    // Get the query parameters as a query object
    let queryObject = url.parse(req.url, true).query;
    let publishingProtocol = queryObject.protocol.toUpperCase();
    
    // Filter the original request headers to keep only relevant headers.
    // If we pass all the original headers from the webhook, then a
    // HTTP 404 error will be returned when making the HTTP POST request below.
    const headerFilterPatterns = ['x-github', 'x-hub'];
    var filteredRequestHeaders = {};
    for (var key in req.headers) {
        for (var i = 0; i < headerFilterPatterns.length; i++) {
            if (key.startsWith(headerFilterPatterns[i])) {
                filteredRequestHeaders[key] = req.headers[key];
                break;
            }
        }
    }
    console.log("Filtered request headers: " + JSON.stringify(filteredRequestHeaders));
    
    /**************************************************************************
    * HTTP POST request to the Ontology ingestion service URL
    **************************************************************************/
    
    if ( publishingProtocol == "HTTP" ) {
    
        // Get the ontology ingestor URL from an environmental variable
        let ontologyIngestorUrl = process.env.ONTOLOGY_INGESTOR_URL;
        
        // Build an object containing the filtered headers
        const options = {
            headers: filteredRequestHeaders
        };
        
        // Send the HTTP POST request using Axios (which returns a promise).
        // Do NOT handle the promise and return an immediate response.
        // This is necessary as GitHub Webhook requests timeout after 10 seconds, 
        // after which the HTTP connection is destroyed and the webhook payload 
        // lost, so an immediate response is required. 
        console.log("Invoking the Ontology Ingestion Service at: " + ontologyIngestorUrl);
        try {
            axios.post(ontologyIngestorUrl, req.body, options);
        } catch (err) {
            console.log("Error encountered when invoking the Ontology Ingestion Service: " + err);
        }
        
    }
    
    /**************************************************************************
    * Publish message to a RabbitMQ virtual topic
    **************************************************************************/
    
    else if ( publishingProtocol == "AMQP" ) {
        
        // Create the message object to send
        let message = JSON.stringify(Object.assign(JSON.parse(requestBody), {
            headers: filteredRequestHeaders }));
        
        // Get the RabbitMQ hostname and credentials from the environment variables
        let amqpHostname = process.env.AMQP_HOSTNAME;
        let amqpUsername = process.env.AMQP_USERNAME;
        let amqpPassword = process.env.AMQP_PASSWORD;
        let amqpQueue = process.env.AMQP_QUEUE;
        let amqpOptions = { credentials: require('amqplib').credentials.plain(amqpUsername, amqpPassword) };
        
        // Connect to the message broker
        amqp.connect('amqp://' + amqpHostname, amqpOptions, (error, connection) => {
            
            if (error) {
                throw error;
            }
            
            // Send the message to the queue
            connection.createChannel(function(err1, channel) {
                
                if (err1) {
                    throw err1;
                }

                // Assert a queue into existence. If the queue does not already
                // exist then it will be created.
                channel.assertQueue(amqpQueue, {
                    durable: true
                });

                channel.sendToQueue(amqpQueue, Buffer.from(message));
                console.log("Sent message: '%s'", message);
                
            });
            
            setTimeout(function() {
                connection.close();
                res.writeHead(200);
                res.end("Successfully processed the GitHub webhook.");
            }, 500);
            
        });
        
    }

}

// Start a local HTTP server listening on the specified port
const server = http.createServer(requestListener);
server.listen(8080);
