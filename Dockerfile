FROM adoptopenjdk/openjdk11:ubi
VOLUME /service-web-app
ADD build/libs/web-service-*.jar /app/web-service.jar
ADD src/main/resources/application.properties /app/application.properties
EXPOSE 8999
ENTRYPOINT ["java" ,"-Djava.security.egd=file:/dev/./urandom --spring.config.location=classpath:file:/app/application.properties","-jar","/app/web-service.jar"]
