FROM eclipse-temurin:17-jdk-alpine
LABEL auhors = "Yonas & Ivo"
MAINTAINER gaz

WORKDIR /var/elementedu
VOLUME /tmp

ARG JAR_FILE
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
