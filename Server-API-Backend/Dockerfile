# Use the official Maven image with OpenJDK 20 for the build stage
FROM maven:3.9.3-eclipse-temurin-20 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml file and the src directory into the container
COPY pom.xml .
COPY src ./src
COPY images /app/images

# Build the application using Maven
RUN mvn clean install -DskipTests

# Use the official Eclipse Temurin OpenJDK 20 image for the execution stage
FROM eclipse-temurin:20-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file from the build stage to the execution stage
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar .

# Expose the port on which the application will listen
EXPOSE 8080

# Set the environment variable to activate the 'docker' profile
ENV SPRING_PROFILES_ACTIVE=docker

# Define the command to run the application
CMD ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]