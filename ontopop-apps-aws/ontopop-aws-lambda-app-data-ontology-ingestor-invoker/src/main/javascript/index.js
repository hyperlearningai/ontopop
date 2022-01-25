const AWS = require('aws-sdk');
exports.handler = async (event) => {
    
    // Define the ontology ingestor function name as an environmental variable.
    // Note that this lambda (invoker) will require permissions to invoke
    // the lambda (invokee) below via the lambda:InvokeFunction action.
    // Reference: https://docs.aws.amazon.com/lambda/latest/dg/API_Invoke.html
    let functionName = process.env.FUNCTION_NAME_ONTOLOGY_INGESTOR;
    
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
