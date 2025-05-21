# Spring Boot Demo Application

This is a simple Spring Boot application that demonstrates the use of Spring Boot, Spring Data JDBC.

## Features

- RESTful API for managing todos and messages
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
   A welcome page will be displayed with information about the available API endpoints.

5. To stop the application:
   ```
   docker compose down
   ```

### Building the Docker Image

When building the Docker image, you can pass build arguments to set default values for the Sealights environment variables:

```bash
docker build -t sp-demo-java-app \
  --build-arg SL_TOKEN="your_token" \
  --build-arg SL_APPNAME="your_app_name" \
  --build-arg SL_BUILDNAME="your_build_name" \
  --build-arg SL_LABID="your_lab_id" \
  --build-arg SL_INCLUDES="your_includes_pattern" \
  .
```

These build arguments will be used as default values for the environment variables in the Docker image. However, they can be overridden at runtime using the `-e` flag with `docker run`.

### Running with Docker Run

If you want to run the application using `docker run` instead of Docker Compose, you need to ensure that the Sealights environment variables are properly passed to the container. The application uses the following environment variables:

- `SL_TOKEN` - Sealights token
- `SL_APPNAME` - Sealights application name
- `SL_BUILDNAME` - Sealights build name
- `SL_LABID` - Sealights lab ID
- `SL_INCLUDES` - Sealights includes pattern

These variables are defined in the `.env` file and are automatically loaded by Docker Compose. However, when using `docker run`, you need to pass them explicitly.

For convenience, we've provided scripts that load these variables from the `.env` file and pass them to the Docker container:

#### Windows (PowerShell)

```powershell
.\run-docker.ps1
```

#### Linux/macOS (Bash)

```bash
chmod +x run-docker.sh
./run-docker.sh
```

Alternatively, you can pass the environment variables manually:

```bash
docker run -p 8080:8080 \
  -e SL_TOKEN="your_token" \
  -e SL_APPNAME="your_app_name" \
  -e SL_BUILDNAME="your_build_name" \
  -e SL_LABID="your_lab_id" \
  -e SL_INCLUDES="your_includes_pattern" \
  sp-demo-java-app
```

## API Endpoints

### Todos

- `GET /api/todos` - Get all todos
- `GET /api/todos/{id}` - Get a todo by ID
- `POST /api/todos` - Create a new todo
- `PUT /api/todos/{id}` - Update a todo
- `DELETE /api/todos/{id}` - Delete a todo

### Messages

- `GET /api/messages` - Get all messages
- `GET /api/messages/{id}` - Get a message by ID
- `POST /api/messages` - Create a new message

## Development

### Running Locally

### Run the application:
   ```
   ./gradlew bootRun
   ```


### Running Tests

```
./gradlew test
```

### Test Coverage Reports

This project uses JaCoCo for test coverage reporting. JaCoCo generates detailed reports showing which lines of code are covered by tests and which are not.

#### Generating Coverage Reports

- **Unit Test Coverage**: Run unit tests and generate coverage report
  ```
  ./gradlew test jacocoTestReport
  ```

- **Integration Test Coverage**: Run integration tests and generate coverage report
  ```
  ./gradlew integrationTest jacocoIntegrationTestReport
  ```

- **Aggregated Coverage Report**: Generate a combined report for both unit and integration tests
  ```
  ./gradlew jacocoAggregatedReport
  ```

#### Viewing Coverage Reports

After generating the reports, you can find them in the following locations:

- Unit Test Coverage: `build/reports/jacoco/test/index.html`
- Integration Test Coverage: `build/reports/jacoco/integrationTest/index.html`
- Aggregated Coverage: `build/reports/jacoco/aggregated/index.html`

Open these HTML files in your browser to view detailed coverage information, including:

- Overall project coverage percentage
- Package-level coverage statistics
- Class-level coverage details
- Line-by-line coverage highlighting

The reports help identify areas of code that lack test coverage, allowing you to focus your testing efforts more effectively.
