# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Maven files first to cache dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x ./mvnw
# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src src
RUN ./mvnw package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Environment variables
ENV SPRING_APPLICATION_NAME=academic-management-hub \
  SERVER_PORT=8080 \
  SPRING_DATASOURCE_URL=jdbc:postgresql://academic-hub-postgres:5432/academic_hub_db \
  SPRING_JPA_HIBERNATE_DDL_AUTO=validate \
  SPRING_JPA_SHOW_SQL=false \
  SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect \
  SPRING_FLYWAY_ENABLED=true \
  SPRING_FLYWAY_BASELINE_ON_MIGRATE=true \
  SPRING_PROFILES_ACTIVE=prod \
  APP_FRONTEND_URL=http://localhost:3000 \
  JAVA_OPTS="-Xms512m -Xmx512m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]