FROM adoptopenjdk/openjdk11:ubi
VOLUME /service-web-app
ADD build/libs/web-service-*.jar web-service.jar
EXPOSE 8999
ENTRYPOINT ["java", "-jar","/web-service.jar"]