FROM eclipse-temurin:21-jdk-alpine

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-Xmx2g", "-jar", "/app.jar"]