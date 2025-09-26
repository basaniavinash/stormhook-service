FROM openjdk:21-jdk

WORKDIR /app

COPY /build/libs/stormhook-service-1.0-SNAPSHOT.jar .

CMD ["java", "-jar", "/app/stormhook-service-1.0-SNAPSHOT.jar"]

