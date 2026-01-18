FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# We copy the JAR already built by Maven on your host machine
COPY target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]