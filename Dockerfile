# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
# Install X11 libraries
RUN apt-get update && apt-get install -y \
    libx11-6 \
    libxext-dev \
    libxrender-dev \
    libxtst-dev \
    libxi-dev \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app

# Create a non-root user to run the application
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Environment variables
ENV JAVA_OPTS="-Xms512m -Xmx512m"

EXPOSE 8080

# Set the entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]