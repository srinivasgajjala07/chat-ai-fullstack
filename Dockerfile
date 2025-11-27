# -------- Stage 1: Build the JAR using Maven --------
FROM eclipse-temurin:17-jdk-alpine AS build

# Install Maven
RUN apk update && apk add maven

WORKDIR /app
COPY . .

# Build the jar
RUN mvn clean package -DskipTests

# -------- Stage 2: Run the Spring Boot app --------
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
