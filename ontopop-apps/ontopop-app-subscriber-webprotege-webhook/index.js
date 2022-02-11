/**
 * WebProtege Project Update Notification Subscriber
 *
 * A simple HTTP server application written in Node.js that will be 
 * requested by the WebProtege project update webhook (POST). 
 * This application simply sends the original request body payload to the 
 * OntoPop WebProtege Git Connector URL as a HTTP POST request using the Axios 
 * library, or to a RabbitMQ messaging queue using the amqplib library. 
 * The Axios libary returns a promise which we do NOT handle so
 * that the HTTP response back to WebProtege is immediate.
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
    console.log("New HTTP POST request: WebProtege project update webhook.");
    console.log("WebProtege project update webhook headers: " + JSON.stringify(req.headers));

    // Log the HTTP request body payload for debugging purposes
    let requestBody = '';
    req.on('data', chunk => {
        requestBody += chunk.toString();
    });
    req.on('end', () => {
        console.log("WebProtege project update webhook payload: " + requestBody);
    });
    
    // Get the query parameters as a query object
    let queryObject = url.parse(req.url, true).query;
    let publishingProtocol = queryObject.protocol.toUpperCase();
    
    /**************************************************************************
    * HTTP POST request to the WebProtege to Git exporter URL
    **************************************************************************/
    
    if ( publishingProtocol == "HTTP" ) {
        
        // Get the WebProtege to Git exporter URL from the environment variable
        let webProtegeGitExporterUrl = process.env.WEBPROTEGE_GIT_EXPORTER_URL;

        // Send the HTTP POST request using Axios (which returns a promise).
        console.log("Invoking the WebProtege Git Connector service at: " + webProtegeGitConnectorUrl);
        try {
            axios.post(webProtegeGitConnectorUrl, req.body);
        } catch (err) {
            console.log("Error encountered when invoking the WebProtege Git Connector service: " + err);
        }
        
    }
    
    /**************************************************************************
    * Publish message to a RabbitMQ virtual topic
    **************************************************************************/
    
    else if ( publishingProtocol == "AMQP" ) {
        
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

                channel.sendToQueue(amqpQueue, Buffer.from(requestBody));
                console.log("Sent message: '%s'", requestBody);
                
            });
            
            setTimeout(function() {
                connection.close();
            }, 500);
            
        });
        
    }

}

// Start a local HTTP server listening on the specified port
const server = http.createServer(requestListener);
server.listen(8080);
