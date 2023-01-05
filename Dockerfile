FROM openjdk:17-alpine
COPY target/*.jar /app.jar
EXPOSE 7000
ENTRYPOINT ["java", "-jar", "/app.jar"]