<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://ontopop.com" target="_blank">
    <img src="static/assets/images/logos/ontopop-logo-small-150x500.png" alt="OntoPop" width="500" height="150">
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
    <a href="https://docs.ontopop.com" target="_blank"">Documentation</a>
  </p>
</p>
<br/>

|![OntoPop](static/assets/images/screenshots/ontopop-v0-7-0-screenshot.png "OntoPop")|
|:---:|
|Screenshot of OntoPop https://ontopop.com|
<br/>

## Table of Contents  
[1. Introduction](#introduction)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[1.1. Vision](#vision)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[1.2. Project Sponsors](#sponsors)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[1.3. Frameworks](#frameworks)<br/>
[2. Getting Started](#getting-started)<br/>
[3. Roadmap](#roadmap)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[3.1. Minimum Viable Product](#mvp)<br/>
[4. Contributing](#contributing)<br/>
[5. License](#license)<br/>
[6. Contact and Further Information](#contact)<br/>
[7. Acknowledgements](#acknowledgements)<br/>
<br/>

## <a name="introduction"></a>1. Introduction

OntoPop is an open source framework enabling the visualisation, search, exploration and management of ontologies.
<br/>

### <a name="vision"></a>1.1. Vision

The aim of OntoPop (formerly referred to as the Ontology Framework) is to design and develop a framework consisting of a series of open software services that together deliver the ability to visualise, search, explore and manage ontologies, thereby making ontologies accessible to a broader range of users beyond information and data architects.
<br/>

### <a name="sponsors"></a>1.2. Project Sponsors

[Highways England](https://highwaysengland.co.uk/) are the current primary sponsor of the OntoPop framework. The first use case of OntoPop at Highways England is to use the ontology visualisation to make their data landscape more accessible and visible to consumers of Highways England data, built upon the pre-existing and publicly available [Highways England ontology](https://webprotege.stanford.edu/#projects/0b3be685-73bd-4d5a-b866-e70d0ac7169b/edit/Classes).
<br/>

### <a name="frameworks"></a>1.3. Frameworks

OntoPop is built using the following primary open source frameworks:

Service | Description | Primary Frameworks
:--- | :--- | --- 
Data Pipeline | Ontology ingestion, semantic validation, parsing, modelling and loading | <ul><li>[OpenJDK 11](https://openjdk.java.net/projects/jdk/11/)</li><li>[OWL API](https://github.com/owlcs/owlapi/wiki)</li></ul>
Graph Computing Services | Graph-based modelling and interfaces | <ul><li>[OpenJDK 11](https://openjdk.java.net/projects/jdk/11/)</li><li>[Apache TinkerPop](https://tinkerpop.apache.org/)</li></ul>
APIs | Ontology, graph and vocabulary APIs | <ul><li>[OpenJDK 11](https://openjdk.java.net/projects/jdk/11/)</li><li>[Spring Framework](https://spring.io/)</li></ul>
UI Web App | User-facing ontology visualisation, search and management web application | <ul><li>[React](https://reactjs.org/)</li><li>[NextJs](https://nextjs.org/)</li><li>[Redux-zero](https://github.com/redux-zero/redux-zero)</li><li>[vis.js](https://visjs.org/)</li></ul>
<br/>

## <a name="getting-started"></a>2. Getting Started

Service | Description | Getting Started
:--- | :--- | :---
Backend Services | Java-based backend services including the ontology and graph data pipelines, graph computing services and APIs | [Link](ontology-services)
UI Web App | Javascript-based user-facing web application | [Link](https://github.com/hyperlearningai/ontology-visualisation)
<br/>

## <a name="roadmap"></a>3. Roadmap

Phase | Primary Sponsor | Deliverables | Timelines
:--- | :--- | :--- | :---
Minimum Viable Product (MVP) | Highways England | Series of open and scalable web services and APIs, and a user-facing web application, to enable the visualisation, search, exploration, navigation and management of ontologies, pre-loaded with the Highways England ontology in the first instance. | December 2020 - April 2021
<br/>

### <a name="mvp"></a>3.1. Minimum Viable Product

The first phase of the OntoPop framework is the development of a minimum viable product (MVP) that delivers a series of open and scalable web services and APIs, and a user-facing web application, to enable the visualisation, search, exploration, navigation and management of ontologies, pre-loaded with the Highways England ontology in the first instance.
<br/><br/>

## <a name="contributing"></a>4. Contributing

OntoPop is a community-driven open source framework released under the [Creative Commons Attribution-ShareAlike 4.0](https://creativecommons.org/licenses/by-sa/4.0/) license. The best way to get started is by visiting the [OntoPop Wiki](https://docs.ontopop.com).
<br/><br/>

## <a name="license"></a>5. License

OntoPop is distributed under the [Creative Commons Attribution-ShareAlike 4.0](https://creativecommons.org/licenses/by-sa/4.0/) license. Please see [LICENSE](LICENSE) for more information.
<br/><br/>

## <a name="contact"></a>6. Contact and Further Information

For further information and guidance, please contact:

* **Highways England**<br/>Primary Project Sponsor<br/>contactus@ontopop.com<br/><br/>
* **Jillur Quddus**<br/>Chief Data Scientist and Principal Polyglot Software Engineer<br/>contactus@hyperlearning.ai<br/><br/>


## <a name="acknowledgements"></a>7. Acknowledgements

**Organisations**

* [Highways England](https://highwaysengland.co.uk/) - Primary Project Sponsor
* [Centre for Digital Built Britain](https://www.cdbb.cam.ac.uk/what-we-do/national-digital-twin-programme) - National Digital Twin Programme

**Teams and Individuals**

* [Jillur Quddus](https://hyperlearning.ai/team/jillurquddus) - Project Team Lead & Lead Engineer
* [Natasha Chowdory](https://hyperlearning.ai/team/natashachowdory) - Lead User Researcher
* [Christian Carestia](https://www.linkedin.com/in/christiancarestia) - Lead Front End Engineer
* [Michal Kostyal](https://www.linkedin.com/in/michalkostyal) - Front End Engineer
* [Tan Kent](https://www.linkedin.com/in/armandtan) - Front End Engineer

**Open Source Frameworks**

* [OpenJDK 11](https://openjdk.java.net/projects/jdk/11/)
* [OWL API](https://github.com/owlcs/owlapi/wiki)
* [Apache TinkerPop](https://tinkerpop.apache.org/)
* [Spring Framework](https://spring.io/)
* [React](https://reactjs.org/)
* [NextJs](https://nextjs.org/)
* [Redux-zero](https://github.com/redux-zero/redux-zero)
