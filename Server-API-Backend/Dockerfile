# Stage 1: build app
FROM maven:3.9.8-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml /app/pom.xml

COPY src ./src

COPY images /app/images

RUN mvn clean install -DskipTests

# Stage 2: run the app 

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/my-app.jar /app/my-app.jar
COPY --from=build /app/images /app/images

EXPOSE 8080
# Set the environment variable to activate the 'docker' profile
ENV SPRING_PROFILES_ACTIVE=docker

CMD ["java", "-jar", "/app/my-app.jar"]

