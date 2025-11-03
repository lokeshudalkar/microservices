# ---------- Stage 1: Build Stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

# ---------- Stage 2: Runtime Stage ----------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
EXPOSE 8083

COPY --from=build /app/target/*.jar application-service.jar

ENTRYPOINT ["java", "-jar", "application-service.jar"]

