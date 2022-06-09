FROM adoptopenjdk/openjdk11:ubi
RUN ./gradlew build
ADD build/libs/web-service-*.jar /web-service.jar
ADD scripts/app.sh app.sh
EXPOSE 8999
ENTRYPOINT ["./app.sh"]