# ncl-ui
Development of official NCL UI

[![Download][gp-img]][gp-lnk]
[![Build Status][bs-img]][bs-lnk]
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

[gp-img]: https://github.githubassets.com/images/modules/site/packages/packages.svg
[gp-lnk]: https://github.com/nus-ncl/service-web/releases
[bs-img]: https://travis-ci.org/nus-ncl/service-web.svg?branch=master
[bs-lnk]: https://travis-ci.org/nus-ncl/service-web
[cs-img]: https://coveralls.io/repos/github/nus-ncl/service-web/badge.svg?branch=master
[cs-lnk]: https://coveralls.io/github/nus-ncl/service-web?branch=master
[qg-img]: https://sonarcloud.io/api/project_badges/measure?project=nus-ncl_service-web&metric=alert_status
[qg-lnk]: https://sonarcloud.io/dashboard?id=nus-ncl_service-web
