# Book Management System - Java 21 Edition

A comprehensive Spring Boot application for managing books with full CRUD operations, ISBN-13 generation, and RESTful APIs. Built with Java 21 and modern Spring Boot features.

## Features

- **CRUD Operations**: Create, Read, Update, Delete books
- **ISBN-13 Generation**: Auto-generated unique ISBN numbers following ISBN-13 standards
- **Input Validation**: Comprehensive validation for all input fields
- **Pagination & Sorting**: Efficient data retrieval with pagination support
- **Search Functionality**: Search books by title, author, or both
- **Idempotent APIs**: All endpoints are designed to be idempotent
- **Exception Handling**: Global exception handling with proper error responses
- **API Documentation**: Swagger/OpenAPI documentation
- **Docker Support**: Containerized deployment with multi-stage builds
- **Comprehensive Testing**: Unit tests with high coverage
- **Java 21 Features**: Utilizes modern Java 21 language features

## Technology Stack

- **Java 21** (LTS) with preview features enabled
- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **Hibernate 6.x**
- **MySQL 8.2**
- **Maven 3.9+**
- **JUnit 5**
- **Mockito**
- **Swagger/OpenAPI 3**
- **Docker & Docker Compose**

## Java 21 Features Used

- **String Templates**: Enhanced string formatting with `STR.` template processor
- **Pattern Matching**: Advanced switch expressions with pattern matching
- **Records**: Immutable data classes for DTOs and validation results
- **Enhanced Switch Expressions**: More readable and maintainable code
- **Improved Null Safety**: Better null handling with modern patterns
- **ZGC Garbage Collector**: Optimized for low-latency applications

## Prerequisites

- **Java 21** or higher (OpenJDK or Oracle JDK)
- **Maven 3.9** or higher
- **MySQL 9.2** or higher (or Docker)
- **Docker** (optional, for containerized deployment)

## Getting Started

### Local Setup with Java 21

1. **Verify Java 21 Installation**
   ```bash
   java --version
   # Should show Java 21.x.x
   mvn clean compile
   mvn spring-boot:run
   # Should be live on port 8080