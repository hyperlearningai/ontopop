/**
 * WebProtege Project Update Notification Subscriber
 *
 * A simple HTTP server application written in Node.js that will be 
 * requested by the WebProtege project update webhook (POST). 
 * This Azure Function simply sends the original request body payload to the 
 * WebProtege to Git exporter URL as a HTTP POST request using the Axios 
 * library, or to an Azure Service Bus topic using the azure/service-bus library. 
 * The Axios libary returns a promise which we do NOT handle so
 * that the HTTP response back to WebProtege is immediate.
 *
 * @author jillurquddus
 * @since  2.0.0
 */

const axios = require('axios');
const url = require('url');
const { ServiceBusClient } = require("@azure/service-bus");

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
            
            // Send the message to the topic
            context.log("Sending message to the Azure Service Bus topic: " + azureServiceBusTopic);
            await azureServiceBusSender.sendMessages(JSON.stringify(req.body));
            
            // Close the sender
            await sender.close();
            
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
            context.done();
            
        }
        
    }

}
