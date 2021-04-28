<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://ontopop.com" target="_blank">
    <img src="../static/assets/images/logos/ontopop-logo-small-150x500.png" alt="OntoPop" width="500" height="150">
  </a>
  <h3 align="center">OntoPop</h3>
  <p align="center">
    Open source framework enabling the visualisation, search, exploration and management of ontologies.
    <br/>
    <a href="https://ontopop.com" target="_blank"><strong>OntoPop Website</strong></a>
    <br/>
    <br/>
    <a href="https://ontopop.com" target="_blank"">Website</a>
    ·
    <a href="https://hyperlearningai.atlassian.net/wiki/spaces/OF" target="_blank"">Documentation</a>
  </p>
</p>
<br/>

# OntoPop Services
Java-based backend OntoPop services including the ontology and graph data pipelines, graph computing services and APIs.
<br/><br/>

## Table of Contents  
[1. Introduction](#introduction)<br/>
[2. Getting Started](#getting-started)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[2.1. Requirements](#requirements)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[2.2. Cloning](#cloning)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[2.3. Importing](#importing)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[2.4. Running APIs Locally](#running)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[2.5. Deploying APIs using Maven](#deploying)<br/>
[3. License](#license)<br/>
[4. Contact and Further Information](#contact)<br/>
<br/>

## <a name="introduction"></a>1. Introduction

OntoPop Java-based backend services include:

* Data Pipeline - ontology ingestion, semantic validation, parsing, modelling and loading modules
* Graph Computing Services - graph-based modelling, interfaces and abstract classes using the TinkerPop framework
* APIs - ontology, graph and vocabulary Spring-based APIs

## <a name="getting-started"></a>2. Getting Started

The following subsections describe the steps required to clone, develop and build the OntoPop backend services locally.

### <a name="requirements"></a>2.1. Requirements

Please ensure that the following prerequisite software services are installed locally:

* [OpenJDK 11](https://openjdk.java.net/projects/jdk/11/)
* [Apache Maven](https://maven.apache.org/)

### <a name="cloning"></a>2.2. Cloning

Please clone this Git repository onto your local development machine by executing the following commands via the Git command line (or similar). The directory into which you clone this Git repository will hereafter be referred to as ```ONTOPOP_BASE```.

```
> git clone https://github.com/hyperlearningai/ontopop
> cd ontopop
```

### <a name="importing"></a>2.3. Importing

OntoPop services employs a Maven parent-child module hierarchy. Therefore in order to import the OntoPop services parent Maven project into your chosen IDE (for example Eclipse or VSCode), simply import ```ONTOPOP_BASE/ontology-services```. Thereafter the following Maven child modules will be available:

* [ontology-api-all](ontology-api-all) - All APIs consolidated into a single module designed for deployment to a single API app
* [ontology-api-auth](ontology-api-auth) - Authentication APIs including login
* [ontology-api-collaboration](ontology-api-collaboration) - Collaboration APIs including note management
* [ontology-api-common](ontology-api-common) - Common API classes and helper functions
* [ontology-api-graph](ontology-api-graph) - Graph APIs such as requesting vertices and edges
* [ontology-api-ontology](ontology-api-ontology) - Ontology APIs such as requesting classes and semantic triples
* [ontology-api-ui](ontology-api-ui) - UI APIs including UI styling management
* [ontology-common](ontology-common) - Common static resources used across the project
* [ontology-etl](ontology-etl) - ETL pipelines including ontology and graph processing
* [ontology-graphdb](ontology-graphdb) - TinkerPop-compliant graph database services
* [ontology-jpa](ontology-jpa) - Java Persistence API
* [ontology-model](ontology-model) - Foundational entities and ontology-specific models
* [ontology-owlapi](ontology-owlapi) - OWL processing including semantic validation and parsing
* [ontology-search](ontology-search) - Search indexing services
* [ontology-security](ontology-security) - Authentication and authorisation framework
* [ontology-ui](ontology-ui) - Custom response models for UI visualisation libraries
* [ontology-utils](ontology-utils) - Common utility functions

### <a name="running"></a>2.4. Running APIs Locally

To run a specific API locally for development and testing purposes, please navigate into the relevant Maven child module and run the ```ai.hyperlearning.ontology.services.api.<name>.apps.StartAPIApplication``` Spring Boot application. For example to run the Ontology Authentication API locally, navigate into the ```ontology-api-auth``` Maven child module and run ```ai.hyperlearning.ontology.services.api.auth.apps.StartAPIApplication```.

HTTP port numbers for each of the API services are defined in the ```ontology-common``` Maven child module and in ```src/main/java/resources/conf/environment/<environment>/ontology-framework.properties```.

### <a name="deploying"></a>2.5. Deploying APIs using Maven

The online OntoPop service is currently deployed to the Microsoft Azure cloud computing platform. In order to deploy specific APIs to Azure App Service instances using Maven directly, please update the ```pom.xml``` file in the relevant Maven child module and specifically the Azure deployment properties for the relevant target deployment environment profile. For example:

```
<!-- Production Environment -->
<profile>
    <id>production</id>
    <properties>
        <deployment.azure.subscriptionId>AZURE SUBSCRIPTION ID</deployment.azure.subscriptionId>
        <deployment.azure.resourceGroup>AZURE RESOURCE GROUP NAME</deployment.azure.resourceGroup>
        <deployment.azure.appName>AZURE APP NAME</deployment.azure.appName>
        <deployment.azure.pricingTier>AZURE APP SERVICE PRICING TIER</deployment.azure.pricingTier>
        <deployment.azure.region>AZURE APP SERVICE REGION</deployment.azure.region>
        <deployment.azure.appServicePlanName>AZURE APP SERVICE PLAN NAME</deployment.azure.appServicePlanName>
        <deployment.azure.appServicePlanResourceGroup>AZURE APP SERVICE PLAN RESOURCE GROUP</deployment.azure.appServicePlanResourceGroup>
        <deployment.azure.runtime.os>AZURE APP SERVICE OS</deployment.azure.runtime.os>
        <deployment.azure.runtime.javaVersion>Java 11</deployment.azure.runtime.javaVersion>
        <deployment.azure.runtime.webContainer>Java SE</deployment.azure.runtime.webContainer>
        <spring.profiles.active>production</spring.profiles.active>
    </properties>
</profile>
```

To then deploy the API to an Azure App Service instance using Maven directly, please execute the following command line statements:

```
# Build the project and all inter-dependencies
> cd ONTOPOP_BASE/ontology-services
> mvn clean install -P [Target Environment Maven Profile Name]

# Login to Azure using the Azure CLI
> az login

# Deploy a specific API application to Azure
> cd [Name of Ontology API Maven Child Module e.g. ontology-api-auth]
> mvn package azure-webapp:deploy -P [Target Environment Maven Profile Name]
```

## <a name="license"></a>3. License

Distributed under the [Creative Commons Attribution-ShareAlike 4.0](https://creativecommons.org/licenses/by-sa/4.0/) license. Please see [LICENSE](../LICENSE) for more information.
<br/><br/>

## <a name="contact"></a>4. Contact and Further Information

For further information and guidance, please contact:

* **Jillur Quddus**<br/>Chief Data Scientist and Principal Polyglot Software Engineer<br/>contactus@hyperlearning.ai
