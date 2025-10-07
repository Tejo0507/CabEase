# Build stage
FROM maven:3.8.5-openjdk-11 AS build

# Set the working directory
WORKDIR /app

# Copy the backend source code
COPY backend/pom.xml ./pom.xml
COPY backend/src ./src

# Build the application and copy dependencies
RUN mvn -f pom.xml clean package dependency:copy-dependencies

# ---

# Package stage
FROM openjdk:11-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the war file from the build stage
COPY --from=build /app/target/cabease.war /app/cabease.war

# Copy webapp-runner from the build stage
COPY --from=build /app/target/dependency/webapp-runner.jar /app/webapp-runner.jar

# Expose the port
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "webapp-runner.jar", "--port", "8080", "cabease.war"]
