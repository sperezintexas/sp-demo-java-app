# Project Guidelines for Kotlin Spring Boot Demo Application

## Project Overview

This is a Kotlin Spring Boot application that demonstrates RESTful API functionality for managing todos and messages. The application is built with Spring Boot 3.4.4 and uses Java 21.

### Key Features
- RESTful API endpoints for managing todos and messages
- H2 in-memory database for data storage
- Spring Data JDBC for database operations
- JUnit 5 with Mockito for testing

## Project Structure

### Main Components

- **Controllers**: Handle HTTP requests and responses
  - `HomeController`: Redirects the root URL to the index.html page
  - `MessageController`: Handles message-related endpoints (GET, POST)
  - `TodoController`: Handles todo-related endpoints (GET, POST, PUT, DELETE)

- **Services**: Contain business logic
  - `MessageService`: Provides methods for finding and saving messages
  - `TodoService`: Provides CRUD operations for todos

- **Repositories**: Interface with the database
  - `MessageRepository`: Extends CrudRepository for Message entities
  - `TodoRepository`: Extends CrudRepository for Todo entities

- **Data Models**:
  - `Message`: Represents a message with text and ID
  - `Todo`: Represents a todo with title, description, completion status, and creation timestamp

### Directory Structure

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/
│   │       └── sealights/
│   │           └── demoapp/
│   │               ├── config/
│   │               ├── controller/
│   │               ├── data/
│   │               ├── repository/
│   │               ├── service/
│   │               └── DemoAppApplication.kt
│   └── resources/
│       ├── static/
│       │   └── index.html
│       └── templates/
└── test/
    └── kotlin/
        └── com/
            └── sealights/
                └── demoapp/
```

## Development Guidelines

### Testing

- Run tests before submitting changes to ensure functionality is preserved
- Tests can be run using Gradle: `./gradlew test`
- The project uses JUnit 5 with Mockito for testing

### Building

- The project can be built using Gradle: `./gradlew build`
- Java 21 is required for building and running the application

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Write clear and concise comments
- Maintain proper indentation and formatting

### API Endpoints

#### Message API
- `GET /api/messages`: Get all messages
- `GET /api/messages/{id}`: Get a message by ID
- `POST /api/messages`: Create a new message

#### Todo API
- `GET /api/todos`: Get all todos
- `GET /api/todos/{id}`: Get a todo by ID
- `POST /api/todos`: Create a new todo
- `PUT /api/todos/{id}`: Update a todo
- `DELETE /api/todos/{id}`: Delete a todo

## Running the Application

The application can be run using Gradle:

```
./gradlew bootRun
```

Once started, the application will be available at http://localhost:8080
