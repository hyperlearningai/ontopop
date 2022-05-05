<br/>
<p align="center">
  <a href="https://ontopop.com" target="_blank">
    <img src="static/assets/images/logos/ontopop-logo-small.png" alt="OntoPop" width="500" height="150">
  </a>
  <p align="center">
    OntoPop is an open-source software project that enables the visualisation, search, exploration and management of version-controlled ontologies, delivered through a collection of event-driven microservices and APIs.
    <br/>
    <br/>
    <a href="https://ontopop.com" target="_blank"">Website</a> · <a href="https://docs.ontopop.com" target="_blank"">Documentation</a>
  </p>
</p>
<br/>

|![OntoPop](static/assets/images/screenshots/ontopop-screenshot.png "OntoPop")|
|:---:|
|Screenshot of OntoPop's native UI|
<br/>

## Table of Contents  
[1. Introduction](#introduction)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[1.1. Vision](#vision)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[1.2. Project Sponsors](#sponsors)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[1.3. Open Frameworks](#frameworks)<br/>
[2. Design](#design)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[2.1. Microservices](#microservices)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[2.2. Design Principles](#principles)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[2.3. Logical System Architecture](#architecture)<br/>
[3. Getting Started](#getting-started)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[3.1. Build from Source](#build)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[3.2. Deployment](#deployment)<br/>
[4. License](#license)<br/>
[5. Acknowledgements](#acknowledgements)<br/>
[6. Contact and Further Information](#contact)<br/>
<br/>

## <a name="introduction"></a>1. Introduction

OntoPop is an open-source software project that enables the visualisation, search, exploration and management of version-controlled ontologies, delivered through a collection of event-driven microservices and APIs.
<br/>

### <a name="vision"></a>1.1. Vision

The goal of OntoPop is to make ontologies easier to understand and more accessible to a broader range of users beyond information and data architects. OntoPop achieves this by providing a collection of open-source software services that together enable downstream applications (including OntoPop's native UI app) to visualise, search, explore and manage version-controlled ontologies, thereby making the creation, manipulation, querying and management of ontologies significantly easier than when compared to current tools available on the market. OntoPop was built and is actively maintained by [HyperLearning AI](https://hyperlearning.ai/).
<br/>

### <a name="sponsors"></a>1.2. Project Sponsors

The OntoPop project is sponsored by [National Highways](https://nationalhighways.co.uk/) and [HyperLearning AI](https://hyperlearning.ai/). OntoPop is deployed at National Highways where it is being used to visualise and manage their ontology-based conceptual data model. Users, IT systems and suppliers alike at National Highways all use OntoPop to better understand what data exists across National Highways' entire data management landscape, where it is, who owns it, how to access it and how it can be linked together, thereby significantly improving data visibility, accessibility and governance.
<br/>

### <a name="frameworks"></a>1.3. Open Frameworks

OntoPop is primarily written in [Java 11](https://jdk.java.net/java-se-ri/11) and utilizes the following core open-source software frameworks and services:

* [OpenJDK 11](https://openjdk.java.net/projects/jdk/11/)
* [Spring Framework](https://spring.io/), including the following Spring projects:
  - [Spring Boot](https://spring.io/projects/spring-boot)
  - [Spring Data](https://spring.io/projects/spring-data)
  - [Spring Cloud](https://spring.io/projects/spring-cloud)
    - [Spring Cloud Function](https://spring.io/projects/spring-cloud-function)
    - [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream)
    - [Spring Cloud Vault](https://spring.io/projects/spring-cloud-vault)
    - [Spring Cloud AWS](https://spring.io/projects/spring-cloud-aws)
    - [Spring Cloud Azure](https://spring.io/projects/spring-cloud-azure)
    - [Spring Cloud GCP](https://spring.io/projects/spring-cloud-gcp)
  - [Spring Security](https://spring.io/projects/spring-security)
  - [Spring Vault](https://spring.io/projects/spring-vault)
* [Apache Maven](https://maven.apache.org/)
* [Apache Commons](https://commons.apache.org/)
* [Apache Jena](https://jena.apache.org/)
* [Apache TinkerPop](https://tinkerpop.apache.org/) (including [Gremlin](https://tinkerpop.apache.org/gremlin.html))
* [JanusGraph](https://janusgraph.org/)
* [Elasticsearch](https://www.elastic.co/elasticsearch/)
* [RabbitMQ](https://www.rabbitmq.com/)
* [HashiCorp Vault](https://www.vaultproject.io/)
* [Java OWL API](http://owlcs.github.io/owlapi/)
* [Google Guava](https://github.com/google/guava)
* [Git](https://git-scm.com/)
* [Node.js](https://nodejs.org/en/)
<br/>

## <a name="design"></a>2. Design

### <a name="microservices"></a>2.1. Microservices

OntoPop is an open-source collection of event-driven microservices that enable downstream applications to visualise, search, explore and manage version-controlled ontologies. The following diagram illustrates the event-driven microservices provided by OntoPop.
<br/><br/>

|![OntoPop Microservices](https://3137012541-files.gitbook.io/~/files/v0/b/gitbook-x-prod.appspot.com/o/spaces%2Fco1NKEPHZffaCClDsEJG%2Fuploads%2FjLIVWabsj01i69Qm2sF4%2FOntoPop%20-%20High%20Level%20Solution%20Design%20-%20Event%20Driven%20Micro%20Services.jpg?alt=media&token=072a3095-38f2-4f0e-b09c-1f1dce2ccf5e "OntoPop Microservices")|
|:---:|
|OntoPop's event-driven microservices|
<br/>

Below is a brief description of the functional purpose of each of these microservices, with links to their respective Maven module.

#### Ontology Services

##### [Ontology Ingestion Service](ontopop-data-ontology-ingestor)

The ontology ingestion service ingests a W3C Web Ontology Language (OWL) ontology from a Git-based version control repository and copies it to persistent object storage.

##### [Ontology Validation Service](ontopop-data-ontology-validator)

The ontology validation service ingests a W3C Web Ontology Language (OWL) ontology and validates it using semantic reasoners, returning true or false.

##### [Ontology Triplestore Loading Service](ontopop-data-ontology-loader-triplestore)

The ontology triplestore loading service ingests a W3C Web Ontology Language (OWL) ontology and loads it into a physical RDF triplestore.

##### [Ontology Triplestore Query Service](ontopop-api-ontology-triplestore)

The ontology triplestore query service is used to query the RDF triplestore using SPARQL queries and returns the relevant triples.

##### [Ontology Parsing Service](ontopop-data-ontology-parser)

The ontology parsing service ingests a W3C Web Ontology Language (OWL) ontology and parses it into its constituent objects, including annotation properties, object properties, classes and class relationships.

#### Property Graph Services

##### [Property Graph Modelling Service](ontopop-data-ontology-modeller-graph)

The property graph modelling service ingests the parsed objects from a W3C Web Ontology Language (OWL) ontology and models them as directed property graph objects, specifically vertices, edges, vertex properties and edge properties.

##### [Property Graph Loading Service](ontopop-data-ontology-loader-graph)

The property graph loading service ingests the directed property graph objects and loads them into a physical graph database.

##### [Property Graph Indexing Service](ontopop-data-ontology-indexer-graph)

The property graph indexing service ingests the directed property graph objects and indexes them into a physical search index.

##### [Property Graph Query Service](ontopop-api-ontology-graph)

The property graph query service is used to query the graph database and search index using Gremlin graph and free-text search queries, and returns the relevant result sets.

### <a name="principles"></a>2.2. Design Principles

#### Interoperability

Every OntoPop service is developed using open standards and open frameworks and, as such, is agnostic of the underlying deployment environment meaning that OntoPop supports deployment to on-premise, public cloud (including the AWS, Microsoft Azure and GCP cloud computing platforms), private cloud, multi-cloud and/or hybrid environments, and supports integration with industry-standard middleware and 3rd party open-source services.

#### Reusability

Given that OntoPop is a collection of event-driven microservices, each service may be reused beyond the specific purposes of ontology visualisation, search, exploration and management. For example, the OntoPop property graph services may be reused to load, manage and query graph databases for a wide range of alternative use cases including fraud detection, digital twins, disease modelling, behavioural analysis, recommendation systems and natural language processing.

### <a name="architecture"></a>2.3. Logical System Architecture

The following diagram describes the high-level logical system architecture of OntoPop when deployed as a single integrated logical service.

|![OntoPop Logical System Architecture](https://3137012541-files.gitbook.io/~/files/v0/b/gitbook-x-prod.appspot.com/o/spaces%2Fco1NKEPHZffaCClDsEJG%2Fuploads%2FrIjmSR4mwHBEisDJJQQA%2FOntoPop%20-%20High%20Level%20Solution%20Design%20-%20Logical%20System%20Architecture.jpg?alt=media&token=5ff7929f-fea8-412a-bd1d-855652bafae4 "OntoPop Logical System Architecture")|
|:---:|
|OntoPop Logical System Architecture|
<br/>

For further information regarding the technical, integration and security architecture of the OntoPop project, please visit the OntoPop documentation at https://docs.ontopop.com.
<br/><br/>

## <a name="getting-started"></a>3. Getting Started

The following instructions describe how to clone the OntoPop source code repository into your development or deployment environment, and then how to compile, build and package the respective OntoPop software services and applications ready for deployment.

### <a name="build"></a>3.1. Build from Source

#### Build Tools

Please ensure that the following prerequisite build tools are installed in your development or deployment environment.

* **[OpenJDK 11](https://openjdk.java.net/projects/jdk/11/)** - open source reference implementation of Java 11.
* **[Apache Maven](https://maven.apache.org/)** - open source build automation tool for Java.
* **[Git](https://git-scm.com/)** - open source distributed version control system.
* **[Node.js](https://nodejs.org/)** - open source backend JavaScript runtime environment.
* **[npm](https://www.npmjs.com/)** - Package manager for the Node.js JavaScript runtime environment.

#### Clone the Source Code

The open-source source code for the OntoPop project may be found on GitHub at https://github.com/hyperlearningai/ontopop. To clone the OntoPop source code repository into your development or deployment environment, please run the following Git command via your command line (or via your preferred Git GUI tool). The location of the cloned OntoPop source code project folder will hereafter be referred to as `$ONTOPOP_BASE`.

```
# Clone the OntoPop GitHub public repository
$ git clone https://github.com/hyperlearningai/ontopop.git

# Navigate into the OntoPop project folder
# This location will hereafter be referred to as $ONTOPOP_BASE
$ cd ontopop
```

#### Maven Profiles

##### Parent Profiles

The following table describes the Maven profiles defined in `$ONTOPOP_BASE/pom.xml`.

Profile Name | Default | Description
:--- | :--- | --- 
`apps` | Yes | Manages the lifecycle of all OntoPop's core services as well as all OntoPop Spring Boot applications (i.e. serverless function apps and API applications) across all cloud vendors.

##### Application Profiles

The following table describes the Maven profiles defined in `$ONTOPOP_BASE/ontopop-apps/pom.xml`.

Profile Name | Default | Description
:--- | :--- | --- 
`apps-multicloud` | Yes | Manages the lifecycle of all OntoPop Spring Boot applications (i.e. serverless function apps and API applications) across all cloud vendors.
`apps-spring` | No | Manages the lifecycle of Spring Boot applications designed for development and testing purposes, or for deployment to self-managed or Spring Cloud environments.
`apps-aws` | No | Manages the lifecycle of AWS Spring Boot applications designed for deployment to AWS Lambda and AWS Beanstalk apps respectively.
`apps-azure` | No | Manages the lifecycle of Microsoft Azure Spring Boot applications designed for deployment to Azure Function apps and Azure Web Apps respectively.

#### Compile and Build

Please ensure that you have entered the correct configuration into the Spring bootstrap and application property files respectively, appropriate to your target deployment environment, prior to packaging OntoPop. For further information regarding configuring OntoPop, please refer to the OntoPop Spring [bootstrap context](https://docs.ontopop.com/deployment-guides/configuration/bootstrap-context) and [application context](https://docs.ontopop.com/deployment-guides/configuration/application-context) documentation pages respectively. To compile and build OntoPop backend services and applications from source, please run the following commands via your command line.

```
# Navigate to $ONTOPOP_BASE
$ cd $ONTOPOP_BASE

# Clean the project working directory.
$ mvn clean

# Compile, build and package OntoPop.
# By default this will build all services and applications.
$ mvn package

# If you wish to build OntoPop's core services only, 
# then disable the "apps" Maven profile as follows.
$ mvn package -P \!apps

# If you wish to build OntoPop's core services but only the
# apps intended for deployment to a self-managed environment
# or to Spring Cloud, then enable the "apps-spring" Maven 
# profile as follows.
$ mvn package -P apps-spring

# If you wish to build OntoPop's core services but only the
# apps intended for deployment to AWS, then enable the 
# "apps-aws" Maven profile as follows.
$ mvn package -P apps-aws

# If you wish to build OntoPop's core services but only the
# apps intended for deployment to Azure, then enable the 
# "apps-azure" Maven profile as follows.
$ mvn package -P apps-azure
```

If you are running `mvn package` for the first time, it will take approximately 5 - 10 minutes to complete the build (dependent on the speed of your internet connection) as Maven will download all the required Java dependencies for the first time. Subsequent executions of mvn package should take between 2 - 3 minutes to complete.

### <a name="deployment"></a>3.2. Deployment

Assuming that `mvn package` completes successfully, you are now ready to deploy OntoPop. Please follow the links below for deployment instructions specific to your target deployment environment.<br/>

* **[Self Managed](https://docs.ontopop.com/deployment-guides/self-managed)** - deploy OntoPop to a self-managed on-premise, public/private cloud or hybrid environment, integrated with entirely open-source self-managed software services such as HashiCorp Vault, RabbitMQ, MySQL, Apache Jena Fuseki, JanusGraph and Elasticsearch.<br/><br/>
* **[Amazon Web Services](https://docs.ontopop.com/deployment-guides/amazon-web-services)** - deploy OntoPop to the Amazon Web Services (AWS) cloud computing platform, integrated with AWS managed services including AWS Secrets Manager, Amazon S3, Amazon MQ, Amazon RDS, AWS Lambda and AWS Elastic Beanstalk.<br/><br/>
* **[Microsoft Azure](https://docs.ontopop.com/deployment-guides/microsoft-azure)** - deploy OntoPop to the Microsoft Azure cloud computing platform, integrated with Azure managed services including Azure Key Vault, Azure Blob Storage, Azure Service Bus, Azure Functions and Azure Web Apps.
<br/>

## <a name="license"></a>4. License

OntoPop is an open-source software project available under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html) (GNU GPLv3) software license.
<br/><br/>

## <a name="acknowledgements"></a>5. Acknowledgements

Provided below is the list of organisations and individuals who sponsor, and contribute towards, the development of the OntoPop project.

### Organisations

* **[National Highways](https://nationalhighways.co.uk/)** - Project sponsor
* **[HyperLearning AI](https://hyperlearning.ai/)** - Project sponsorship, management and maintenance

### Individuals

* **[Jillur Quddus](https://hyperlearning.ai/team/jillurquddus)** - Project Lead, Lead Technical Architect & Lead Java Engineer
* **[Christian Carestia](https://www.linkedin.com/in/christiancarestia)** - Lead Front End Engineer
<br/>

## <a name="contact"></a>6. Contact and Further Information

For further information, please visit the OntoPop documentation website at https://docs.ontopop.com or contact HyperLearning AI using the details below.

* **[Jillur Quddus](https://hyperlearning.ai/team/jillurquddus)**<br/>Chief Data Scientist and Principal Polyglot Software Engineer<br/>ontopop@hyperlearning.ai
