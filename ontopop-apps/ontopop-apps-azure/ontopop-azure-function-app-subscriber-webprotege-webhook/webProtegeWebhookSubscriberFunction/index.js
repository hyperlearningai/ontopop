/**
 * WebProtege Project Update Notification Subscriber
 *
 * A simple HTTP server application written in Node.js that will be 
 * requested by the WebProtege project update webhook (POST). 
 * This Azure Function simply sends the original request body payload to the 
 * WebProtege to Git exporter URL as a HTTP POST request using the Axios 
 * library, or to an Azure Service Bus topic using the azure/service-bus library, 
 * or persists the payload to a RDBMS using the sequelize library. 
 * The Axios libary returns a promise which we do NOT handle so
 * that the HTTP response back to WebProtege is immediate.
 *
 * @author jillurquddus
 * @since  2.0.0
 */

const axios = require('axios');
const url = require('url');
const { ServiceBusClient } = require("@azure/service-bus");
const { Sequelize, Model, DataTypes } = require('sequelize');

module.exports = async function (context, req) {

    // Log the HTTP request headers for debugging purposes
    context.log("New HTTP POST request: WebProtege project update webhook.");
    context.log("WebProtege project update webhook headers: " + JSON.stringify(req.headers));

    // Log the HTTP request body payload for debugging purposes
    context.log("WebProtege project update webhook payload: " + JSON.stringify(req.body));

    // Get the query parameters as a query object
    let queryObject = url.parse(req.url, true).query;
    let publishingProtocol = queryObject.protocol.toUpperCase();
    
    /**************************************************************************
    * HTTP POST request to the WebProtege to Git exporter URL
    **************************************************************************/
    
    if ( publishingProtocol == "HTTP" ) {
        
        // Define the WebProtege Git Connector URL as an environmental variable
        let webProtegeGitExporterUrl = process.env.WEBPROTEGE_GIT_EXPORTER_URL;

        // Send the HTTP POST request using Axios (which returns a promise).
        context.log("Requesting the WebProtege to Git exporter service at: " + webProtegeGitExporterUrl);
        try {
            axios.post(webProtegeGitExporterUrl, req.body);
        } catch (err) {
            context.log("Error encountered when requesting the WebProtege to Git exporter service: " + err);
        }
        
    }

    /**************************************************************************
    * Publish message to an Azure Service Bus topic
    **************************************************************************/

    else if ( publishingProtocol == "AZURE-AMQP" ) {
        
        // Get the Azure Service Bus connection string and topic
        let azureServiceBusConnectionString = process.env.AZURE_SERVICE_BUS_CONNECTION_STRING;
        let azureServiceBusTopic = process.env.AZURE_SERVICE_BUS_TOPIC;
        
        // Create an Azure Service Bus client and sender
        const azureServiceBusClient = new ServiceBusClient(azureServiceBusConnectionString);
        const azureServiceBusSender = azureServiceBusClient.createSender(azureServiceBusTopic);
        
        try {
            
            // Construct and send the message to the topic
            let message = {
                "body": req.body, 
                "contentType": "application/json"
            };
            context.log("Sending message to the Azure Service Bus topic: " + azureServiceBusTopic);
            await azureServiceBusSender.sendMessages(message);
            
            // Close the sender
            await azureServiceBusSender.close();
            
        } catch (err) {
            
            context.log("Error encountered when sending message to the Azure Service Bus topic: " + azureServiceBusTopic);
            context.log(err);
            
        } finally {
            
            // Close the client and send a response back to WebProtege
            await azureServiceBusClient.close();
            context.res = {
                status: 200,
                body: "Successfully processed the WebProtege webhook."
            };
            
        }
        
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
            context.log('Connection has been established successfully.');
            
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
            const record = await WebProtegeWebhook.create({ 
                project_id: req.body.projectId, 
                user_id: req.body.userId, 
                revision_number: req.body.revisionNumber, 
                timestamp: req.body.timestamp
            });
            context.log("Successfully created a new WebProtegeWebhook RDBMS record: " + record.toJSON());
            
        } catch (err) {
            context.log("Error encountered when creating a new WebProtegeWebhook RDBMS record: " + err);
        } finally {
            sequelize.close();
        }
        
    }

}
