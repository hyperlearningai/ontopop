/**
 * WebProtege Project Update Notification Subscriber
 *
 * A simple HTTP server application written in Node.js that will be 
 * requested by the WebProtege project update webhook (POST). 
 * This Azure Function simply sends the original request body payload to the 
 * OntoPop WebProtege Git Connector URL as a HTTP POST request using the Axios 
 * library. The Axios libary returns a promise which we do NOT handle so
 * that the HTTP response back to WebProtege is immediate.
 *
 * @author jillurquddus
 * @since  2.0.0
 */

const axios = require('axios');
module.exports = async function (context, req) {

    // Log the HTTP request headers for debugging purposes
    context.log("New HTTP POST request: WebProtege project update webhook.");
    context.log("WebProtege project update webhook headers: " + JSON.stringify(req.headers));

    // Log the HTTP request body payload for debugging purposes
    context.log("WebProtege project update webhook payload: " + JSON.stringify(req.body));

    // Define the WebProtege Git Connector URL as an environmental variable
    let webProtegeGitConnectorUrl = process.env.WEBPROTEGE_GIT_CONNECTOR_URL;

    // Send the HTTP POST request using Axios (which returns a promise).
    context.log("Invoking the WebProtege Git Connector service at: " + webProtegeGitConnectorUrl);
    try {
        axios.post(webProtegeGitConnectorUrl, req.body);
    } catch (err) {
        context.log("Error encountered when invoking the WebProtege Git Connector service: " + err);
    }

}