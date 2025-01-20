# Use Eclipse Temurin JDK 23 as the base image
FROM eclipse-temurin:23-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the Maven configuration file
COPY pom.xml ./

# Copy the source code
COPY src ./src

# Copy Maven wrapper files
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./

# Make the Maven wrapper executable
RUN chmod +x ./mvnw

# Build the application and skip tests during build
RUN ./mvnw package -DskipTests

# Copy the jar file and rename it
RUN mv target/*.jar target/app.jar

# Expose port 8080
EXPOSE 8080

# Set database environment variables with defaults
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://academic-hub-postgres:5432/academic_hub_db
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=273001

# Run the Spring Boot application
CMD ["java", "-jar", "target/app.jar"]