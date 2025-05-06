# Spring Boot Demo Application

This is a simple Spring Boot application that demonstrates the use of Spring Boot, Spring Data JDBC, and PostgreSQL.

## Features

- RESTful API for managing todos and messages
- PostgreSQL database for data persistence
- Docker support for easy deployment

## Prerequisites

- Docker and Docker Compose
- Java 21 (for local development)

## Running the Application with Docker

1. Make sure Docker Desktop is running on your system. If it's not running, start it before proceeding.

2. Clone the repository:
   ```
   git clone https://github.com/yourusername/sp-demo-java-app.git
   cd sp-demo-java-app
   ```

3. Build and run the application with Docker Compose:
   ```
   docker compose up -d
   ```

4. The application will be available at http://localhost:8080

5. To stop the application:
   ```
   docker compose down
   ```

## API Endpoints

### Todos

- `GET /api/todos` - Get all todos
- `GET /api/todos/{id}` - Get a todo by ID
- `POST /api/todos` - Create a new todo
- `PUT /api/todos/{id}` - Update a todo
- `DELETE /api/todos/{id}` - Delete a todo

### Messages

- `GET /` - Get all messages
- `GET /{id}` - Get a message by ID
- `POST /` - Create a new message

## Development

### Running Locally

1. Make sure Docker Desktop is running on your system. If it's not running, start it before proceeding.

2. Start a PostgreSQL instance:
   ```
   docker compose up -d postgres
   ```

3. Run the application:
   ```
   ./gradlew bootRun
   ```


### Running Tests

```
./gradlew test
```
