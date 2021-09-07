# ncl-ui
Development of official NCL UI

[`Download`](https://github.com/nus-ncl/service-web/releases)
![Build Status](https://github.com/nus-ncl/service-web/actions/workflows/gradle.yml/badge.svg)
[![Coverage Status][cs-img]][cs-lnk]
[![Quality Gate][qg-img]][qg-lnk]

- Tomcat (embedded by Spring Boot)
- WSDL (Axis2 or Spring WSDL?)
- BPM (jBPM, jBoss)
- Angular.js
- Web Templating (Spring MVC?, JSP, Thymeleaf)
- Event driven (WebSocket, Node.js, Spring Reactor)
- No DB access

## Notes
1. Set spring.thymeleaf.cache=false to allow thymeleaf template to be refresh via browser without rebooting entire web app
    - use gradle tasks - bootRun

[cs-img]: https://coveralls.io/repos/github/nus-ncl/service-web/badge.svg?branch=master
[cs-lnk]: https://coveralls.io/github/nus-ncl/service-web?branch=master
[qg-img]: https://sonarcloud.io/api/project_badges/measure?project=nus-ncl_service-web&metric=alert_status
[qg-lnk]: https://sonarcloud.io/dashboard?id=nus-ncl_service-web
