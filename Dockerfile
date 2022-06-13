FROM openjdk:11-jdk

ADD . /service-web
WORKDIR /service-web/
RUN mkdir -p build/libs/ \
  && ./gradlew build \
  && cp build/libs/web-service*.jar /web-service.jar \
  && ./gradlew clean \
  && rm -rf build/*

WORKDIR /
EXPOSE 8999
ENTRYPOINT []