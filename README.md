<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://ontopop.com" target="_blank">
    <img src="static/assets/images/logos/ontopop-logo-small.png" alt="OntoPop" width="500" height="150">
  </a>
  <p align="center">
    OntoPop is an open-source collection of event-driven microservices and APIs that enable consumer applications to visualize, search, explore and manage version-controlled ontologies.
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

OntoPop is an open-source collection of event-driven microservices and APIs that enable consumer applications to visualise, search, explore and manage version-controlled ontologies.
<br/>

### <a name="vision"></a>1.1. Vision

The goal of OntoPop is to make ontologies easier to understand and more accessible to a broader range of users beyond information and data architects. OntoPop achieves this by providing a collection of open-source software services that together enable downstream applications (including OntoPop's native UI app) to visualise, search, explore and manage version-controlled ontologies, thereby making the creation, manipulation, querying and management of ontologies significantly easier than when compared to current tools available on the market. OntoPop was built and is actively maintained by [HyperLearning AI](https://hyperlearning.ai/).
<br/>

### <a name="sponsors"></a>1.2. Project Sponsors

[National Highways](https://nationalhighways.co.uk/) (formerly Highways England) are the current primary sponsor of the OntoPop project. OntoPop is deployed at National Highways where it is being used to visualise and manage their ontology-based conceptual data model. Users, IT systems and suppliers alike at National Highways all use OntoPop to better understand what data exists across National Highways' entire data management landscape, where it is, who owns it, how to access it and how it can be linked together, thereby significantly improving data visibility, accessibility and governance.
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
