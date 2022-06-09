FROM adoptopenjdk/openjdk11:ubi
ADD build/libs/web-service-*.jar /web-service.jar
ADD scripts/app.sh app.sh
EXPOSE 8999
ENTRYPOINT ["./app.sh"]