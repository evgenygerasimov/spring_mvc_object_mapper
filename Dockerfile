FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/spring_mvc_object_mapper-1.0.0.jar app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080