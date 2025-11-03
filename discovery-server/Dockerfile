# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
EXPOSE 8761

COPY --from=build /app/target/*.jar discovery-service.jar

ENTRYPOINT ["java", "-jar", "discovery-service.jar"]
