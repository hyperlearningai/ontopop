/**
 * GitHub Webhook Subscriber
 *
 * A HTTP-triggered (POST) Azure Function written in Node.js that will be 
 * requested by the Git webhook (POST). This Azure Function simply sends the 
 * filtered headers along with the original request body payload to the 
 * Ontology Ingestion Service URL as a HTTP POST request using the Axios 
 * library, or to an Azure Service Bus topic using the azure/service-bus library. 
 * The Axios libary returns a promise which we do NOT handle - this is 
 * necessary as GitHub Webhook requests timeout after 10 seconds, after which
 * the HTTP connection is destroyed and the webhook payload lost, so an
 * immediate response is required.
 *
 * @author jillurquddus
 * @since  2.0.0
 */

const axios = require('axios');
const url = require('url');
const { ServiceBusClient } = require("@azure/service-bus");

module.exports = async function (context, req) {

    // Log the HTTP request headers for debugging purposes
    context.log("New HTTP POST request: GitHub webhook.");
    context.log("GitHub webhook headers: " + JSON.stringify(req.headers));

    // Log the HTTP request body payload for debugging purposes
    context.log("GitHub webhook payload: " + JSON.stringify(req.body));
    
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
    context.log("Filtered request headers: " + JSON.stringify(filteredRequestHeaders));
    
    /**************************************************************************
    * HTTP POST request to the WebProtege to Git exporter URL
    **************************************************************************/
    
    if ( publishingProtocol == "HTTP" ) {
        
        // Define the ontology ingestor URL as an environmental variable
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
        context.log("Invoking Ontology Ingestion Service at: " + ontologyIngestorUrl);
        try {
            axios.post(ontologyIngestorUrl, req.body, options);
        } catch (err) {
            context.log("Error encountered when invoking the Ontology Ingestion Service: " + err);
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
                "body": Object.assign(req.body, {headers: filteredRequestHeaders }), 
                "contentType": "application/json"
            };
            context.log("Constructed message: " + JSON.stringify(message));
            context.log("Sending message to the Azure Service Bus topic: " + azureServiceBusTopic);
            await azureServiceBusSender.sendMessages(message);
            
            // Close the sender
            await azureServiceBusSender.close();
            
        } catch (err) {
            
            context.log("Error encountered when sending message to the Azure Service Bus topic: " + azureServiceBusTopic);
            context.log(err);
            
        } finally {
            
            // Close the client and send a response back to GitHub
            await azureServiceBusClient.close();
            context.res = {
                status: 200,
                body: "Successfully processed the GitHub webhook."
            };
            
        }   
    
    }

}