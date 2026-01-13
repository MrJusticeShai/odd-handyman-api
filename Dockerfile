# Stage 1: Build
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first (for caching dependencies)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build app
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy built jar
COPY --from=build /app/target/odd-handyman-api-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]
