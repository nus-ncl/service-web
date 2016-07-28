# ncl-ui
Development of official NCL UI

[![Build Status][bs-img]][bs-lnk]
[![Coverage Status][cs-img]][cs-lnk]
[![Quality Gate][qg-img]]()

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

[bs-img]: https://travis-ci.org/nus-ncl/service-web.svg?branch=master
[bs-lnk]: https://travis-ci.org/nus-ncl/service-web
[cs-img]: https://coveralls.io/repos/github/nus-ncl/service-web/badge.svg?branch=master
[cs-lnk]: https://coveralls.io/github/nus-ncl/service-web?branch=master
[qg-img]: https://sonarqube.com/api/badges/gate?key=nus-ncl:service-web
[qg-lnk]: https://sonarqube.com/dashboard/index/nus-ncl:service-web
