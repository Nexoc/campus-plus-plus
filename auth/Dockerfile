# ==============================
# BUILD STAGE
# ==============================
# Build Spring Boot application inside Docker
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copy Maven wrapper and config first (for caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build application JAR
RUN ./mvnw clean package -DskipTests


# ==============================
# RUNTIME STAGE
# ==============================
# Lightweight Java runtime only
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
