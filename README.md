# ncl-ui
Development of official NCL UI

[![Build Status][ci-img]][ci-lnk] [![Coverage Status][co-img]][co-lnk]

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

[ci-img]: https://travis-ci.org/nus-ncl/service-web.svg?branch=master
[ci-lnk]: https://travis-ci.org/nus-ncl/service-web
[co-img]: https://coveralls.io/repos/github/nus-ncl/service-web/badge.svg?branch=master
[ci-lnk]: https://coveralls.io/github/nus-ncl/service-web?branch=master
