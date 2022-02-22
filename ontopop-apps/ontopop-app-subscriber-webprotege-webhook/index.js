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
const { Sequelize, Model, DataTypes } = require('sequelize');

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
        console.log("Requesting the WebProtege to Git exporter service at: " + webProtegeGitExporterUrl);
        try {
            axios.post(webProtegeGitExporterUrl, req.body);
        } catch (err) {
            console.log("Error encountered when requesting the WebProtege to Git exporter service: " + err);
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
                res.writeHead(200);
                res.end("Successfully processed the WebProtege webhook.");
            }, 500);
            
        });
        
    }
    
    /**************************************************************************
    * Publish message to a RDBMS
    **************************************************************************/
    
    else if ( publishingProtocol == "SQL" ) {
        
        // Get the RDBMS hostname and credentials from the environment variables
        let rdbmsHostname = process.env.RDBMS_HOSTNAME;
        let rdbmsPort = process.env.RDBMS_PORT;
        let rdbmsDatabase = process.env.RDBMS_DATABASE;
        let rdbmsUsername = process.env.RDBMS_USERNAME;
        let rdbmsPassword = process.env.RDBMS_PASSWORD;
        let rdbmsDialect = process.env.RDBMS_DIALECT;
        
        // Build the connection object
        const sequelize = new Sequelize(rdbmsDatabase, rdbmsUsername, rdbmsPassword, {
            host: rdbmsHostname,
            port: rdbmsPort, 
            dialect: rdbmsDialect
        });
        
        try {
            
            // Connect to the database
            await sequelize.authenticate();
            console.log('Connection has been established successfully.');
            
            // Define the WebProtegeWebhook model
            const WebProtegeWebhook = sequelize.define('WebProtegeWebhook', {
                wpwebhook_id: {
                    type: DataTypes.BIGINT,
                    autoIncrement: true,
                    primaryKey: true
                }, 
                project_id: {
                    type: DataTypes.STRING,
                    allowNull: false
                },
                user_id: {
                    type: DataTypes.STRING, 
                    allowNull: false
                }, 
                revision_number: {
                    type: DataTypes.INTEGER, 
                    allowNull: false
                }, 
                timestamp: {
                    type: DataTypes.BIGINT, 
                    allowNull: false
                }, 
                ontology_id: {
                    type: DataTypes.INTEGER, 
                    allowNull: true
                }
            }, {
                tableName: 'wpwebhooks', 
                timestamps: false
            });
            
            // Check the current state of the table in the database and sync
            await WebProtegeWebhook.sync();
            
            // Create and insert a new WebProtegeWebhook record
            const reqBody = JSON.parse(requestBody)
            const record = await WebProtegeWebhook.create({ 
                project_id: reqBody.projectId, 
                user_id: reqBody.userId, 
                revision_number: reqBody.revisionNumber, 
                timestamp: reqBody.timestamp
            });
            console.log("Successfully created a new WebProtegeWebhook RDBMS record: " + record.toJSON());
            
        } catch (err) {
            console.log("Error encountered when creating a new WebProtegeWebhook RDBMS record: " + err);
        } finally {
            sequelize.close();
        }
    
    }

}

// Start a local HTTP server listening on the specified port
const server = http.createServer(requestListener);
server.listen(8080);
