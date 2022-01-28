/**
 * Ontology Ingestion Service Invoker
 *
 * A HTTP-triggered (POST) application written in Node.js that will be 
 * requested by the Git webhook (POST). This application simply sends the 
 * filtered headers along with the original request body payload to the 
 * Ontology Ingestion Service URL as a HTTP POST request using the Axios 
 * library. The Axios libary returns a promise which we do NOT handle - this is 
 * necessary as GitHub Webhook requests timeout after 10 seconds, after which
 * the HTTP connection is destroyed and the webhook payload lost, so an
 * immediate response is required.
 *
 * @author jillurquddus
 * @since  2.0.0
 */

const http = require('http');
const axios = require('axios');
const requestListener = async function (req, res) {

    // Log the HTTP request headers for debugging purposes
    context.log("New HTTP POST request: Ontology ingestion webhook.");
    context.log("Ontology ingestion webhook headers: " + JSON.stringify(req.headers));

    // Log the HTTP request body payload for debugging purposes
    context.log("Ontology ingestion webhook payload: " + JSON.stringify(req.body));

    // Define the ontology ingestor URL as an environmental variable
    let ontologyIngestorUrl = process.env.ONTOLOGY_INGESTOR_URL;

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

    // Build an object containing the filtered headers
    context.log("Filtered request headers: " + JSON.stringify(filteredRequestHeaders));
    const options = {
        headers: filteredRequestHeaders
    };

    // Send the HTTP POST request using Axios (which returns a promise).
    // Do NOT handle the promise and return an immediate response.
    // This is necessary as GitHub Webhook requests timeout after 10 seconds, 
    // after which the HTTP connection is destroyed and the webhook payload 
    // lost, so an immediate response is required. 
    context.log("Invoking Ontology Ingestor Service at: " + ontologyIngestorUrl);
    try {
        axios.post(ontologyIngestorUrl, req.body, options);
    } catch (err) {
        context.log("Error encountered when invoking the Ontology Ingestor Service: " + err);
    }

}

// Start a local HTTP server listening on the specified port
const server = http.createServer(requestListener);
server.listen(8080);
