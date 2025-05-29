# Multi-stage build for Java 21
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application with Java 21 optimizations
RUN ./mvnw clean package -DskipTests -Dmaven.compiler.release=21

# Runtime stage with Java 21
FROM eclipse-temurin:21-jre-alpine

# Add metadata
LABEL maintainer="Book Management Team"
LABEL version="2.0"
LABEL description="Book Management System with Java 21"

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/book-management-system-*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM optimization for Java 21
ENV JAVA_OPTS="-XX:+UseZGC -XX:+UnlockExperimentalVMOptions -XX:+UseTransparentHugePages -Xms512m -Xmx1024m"

# Run the application with Java 21 optimizations
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]