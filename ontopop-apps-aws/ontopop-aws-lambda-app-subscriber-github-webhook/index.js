/**
 * GitHub Webhook Subscriber
 *
 * An API Gateway HTTP-triggered (POST) AWS lambda application written in 
 * Node.js that will be requested by the Git webhook (POST). This AWS
 * lambda function simply sends the API Gateway Proxy Request Event object
 * to the Ontology Ingestion Service (which is another AWS lambda application). 
 * It invokes the Ontology Ingestion Service AWS lambda application, but it
 * does NOT wait for a response - this is necessary as GitHub Webhook requests 
 * timeout after 10 seconds, after which the HTTP connection is destroyed and 
 * the webhook payload lost, so an immediate response is required.
 *
 * @author jillurquddus
 * @since  2.0.0
 */

const AWS = require('aws-sdk');
exports.handler = async (event) => {
    
    // Define the ontology ingestor function name as an environmental variable.
    // Note that this lambda (invoker) will require permissions to invoke
    // the lambda (invokee) below via the lambda:InvokeFunction action.
    // Reference: https://docs.aws.amazon.com/lambda/latest/dg/API_Invoke.html
    let functionName = process.env.ONTOPOP_ONTOLOGY_INGESTOR_FUNCTION_NAME;
    
    // InvocationType of Event invokes the function asynchronously.
    const params = {
        FunctionName: functionName,
        InvocationType: 'Event',
        Payload: JSON.stringify(event)
    };
    
    // Invoke the lambda but do NOT wait for a response.
    // This is necessary as GitHub Webhook requests timeout after 10 seconds, 
    // after which the HTTP connection is destroyed and the webhook payload 
    // lost, so an immediate response is required.
    let lambda = new AWS.Lambda();
    await lambda.invoke(params).promise();
    
};
