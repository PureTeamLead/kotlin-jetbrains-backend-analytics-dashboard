FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/Analytics-Backend-1.0-SNAPSHOT.jar app.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "app.jar"]