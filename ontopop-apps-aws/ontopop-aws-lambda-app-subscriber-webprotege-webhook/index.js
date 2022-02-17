/**
 * WebProtege Webhook Subscriber
 *
 * An API Gateway HTTP-triggered (POST) AWS lambda application written in 
 * Node.js that will be requested by the WebProtege webhook (POST). This AWS
 * lambda function simply sends the API Gateway Proxy Request Event object
 * to the WebProtege Exporter Service (which is another AWS lambda application). 
 * It invokes the WebProtege Exporter Service AWS lambda application, but it
 * does NOT wait for a response.
 *
 * @author jillurquddus
 * @since  2.0.0
 */

const AWS = require('aws-sdk');
exports.handler = async (event) => {
    
    // Get the WebProtege exporter function name from an environmental variable.
    // Note that this lambda (invoker) will require permissions to invoke
    // the lambda (invokee) below via the lambda:InvokeFunction action.
    // Reference: https://docs.aws.amazon.com/lambda/latest/dg/API_Invoke.html
    let functionName = process.env.FUNCTION_NAME_WEBPROTEGE_EXPORTER;
    
    // InvocationType of Event invokes the function asynchronously.
    const params = {
        FunctionName: functionName,
        InvocationType: 'Event',
        Payload: JSON.stringify(event)
    };
    
    // Invoke the lambda but do NOT wait for a response.
    let lambda = new AWS.Lambda();
    await lambda.invoke(params).promise();
    
};
