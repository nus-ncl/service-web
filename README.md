# ncl-ui
Development of official NCL UI

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
