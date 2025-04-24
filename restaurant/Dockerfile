# Use JDK 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy built JAR to container
COPY target/restaurant-0.0.1-SNAPSHOT.jar app.jar

# Expose app port
EXPOSE 8081

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
