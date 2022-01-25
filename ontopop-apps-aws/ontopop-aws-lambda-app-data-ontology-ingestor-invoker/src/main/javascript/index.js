const AWS = require('aws-sdk');
exports.handler = async (event) => {
    
    // Define the ontology ingestor function name as an environmental variable
    let functionName = process.env.FUNCTION_NAME_ONTOLOGY_INGESTOR;
    
    // InvocationType of Event invokes the function asynchronously
    const params = {
        FunctionName: functionName,
        InvocationType: 'Event',
        Payload: JSON.stringify(event)
    };
    
    // Invoke the lambda but do NOT wait for a response
    // This is necessary as GitHub Webhook requests timeout after 10 seconds
    // so an immediate response is required
    let lambda = new AWS.Lambda();
    await lambda.invoke(params).promise();
    
};
