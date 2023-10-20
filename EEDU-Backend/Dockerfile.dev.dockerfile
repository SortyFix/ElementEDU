# Start with a base image containing Java runtime
FROM openjdk:17-jdk-alpine

# Add Maintainer Info
LABEL maintainer="chniucg@gmail.com"

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=build/libs/EEDU-Backend-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
