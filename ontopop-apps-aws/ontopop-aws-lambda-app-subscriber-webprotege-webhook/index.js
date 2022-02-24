/**
 * WebProtege Webhook Subscriber
 *
 * An API Gateway HTTP-triggered (POST) AWS lambda application written in 
 * Node.js that will be requested by the WebProtege webhook (POST). This AWS
 * lambda function simply sends the API Gateway Proxy Request Event object
 * to the WebProtege Exporter Service (which is another AWS lambda application), 
 * or persists the payload to a RDBMS using the sequelize library. 
 * If it invokes the WebProtege Exporter Service AWS lambda application, it
 * does NOT wait for a response.
 *
 * @author jillurquddus
 * @since  2.0.0
 */

const AWS = require('aws-sdk');
const { Sequelize, Model, DataTypes } = require('sequelize');
exports.handler = async (event) => {
    
    // Log the HTTP request headers for debugging purposes
    console.log("New HTTP POST request: WebProtege project update webhook.");
    console.log("WebProtege project update webhook event: " + JSON.stringify(event));
    
    // Get the query parameters as a query object
    // Reference: https://docs.aws.amazon.com/lambda/latest/dg/services-apigateway.html#apigateway-example-event
    let publishingProtocol = event.queryStringParameters.protocol.toUpperCase();
    
    /**************************************************************************
    * AWS LAMBDA INVOCATION
    **************************************************************************/
    
    if ( publishingProtocol == "LAMBDA" ) {
        
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
        console.log("Invoking AWS Lambda: " + functionName);
        let lambda = new AWS.Lambda();
        await lambda.invoke(params).promise();
        
    }
    
    /**************************************************************************
    * Publish event to a RDBMS
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
            console.log('RDBMS connection has been established successfully.');
            
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
            const requestBody = JSON.parse(event.body);
            const record = await WebProtegeWebhook.create({ 
                project_id: requestBody.projectId, 
                user_id: requestBody.userId, 
                revision_number: requestBody.revisionNumber, 
                timestamp: requestBody.timestamp
            });
            console.log("Successfully created a new WebProtegeWebhook RDBMS record: " + record.toJSON());
            
        } catch (err) {
            console.log("Error encountered when creating a new WebProtegeWebhook RDBMS record: " + err);
        } finally {
            sequelize.close();
        }
        
    }
    
};
