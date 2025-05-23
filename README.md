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

### SonarQube Analysis

This project is configured for code quality analysis using SonarQube/SonarCloud. The configuration automatically handles the potential conflict between manual and automatic analysis.

#### Required Configuration

Before running SonarQube analysis, you need to set up the following:

1. **SONAR_TOKEN Environment Variable**:
   - Create a token in your SonarCloud account (https://sonarcloud.io/account/security)
   - Set the environment variable in your shell:
     ```
     # For Linux/macOS
     export SONAR_TOKEN="your-sonar-token"

     # For Windows Command Prompt
     set SONAR_TOKEN=your-sonar-token

     # For Windows PowerShell
     $env:SONAR_TOKEN="your-sonar-token"
     ```

2. **Project Properties**:
   - The `sonar.projectKey` and `sonar.organization` properties are already set in the `gradle.properties` file
   - Make sure these match your SonarCloud project settings

#### Helper Scripts

For convenience, helper scripts are provided to run SonarQube analysis with the proper environment variables:

**Linux/macOS (Bash):**
```bash
# Make the script executable
chmod +x scripts/run-sonar-analysis.sh

# Run the analysis with your token
./scripts/run-sonar-analysis.sh your-sonar-token
```

**Windows (PowerShell):**
```powershell
# Run the analysis with your token
.\scripts\run-sonar-analysis.ps1 your-sonar-token
```

These scripts will:
- Set the SONAR_TOKEN environment variable
- Run the SonarQube analysis
- Provide feedback on the results

#### Analysis Options

There are two ways to run SonarQube analysis:

1. **Local Analysis** (recommended for development):
   ```
   ./gradlew sonarLocal
   ```
   This runs analysis locally without sending data to SonarCloud. It automatically disables automatic analysis to avoid conflicts.

2. **SonarCloud Analysis** (for CI or manual submission):
   ```
   ./gradlew sonar
   ```
   This runs analysis and sends results to SonarCloud:
   - When running in CI (GitHub Actions), it enables automatic analysis.
   - When running locally, it disables automatic analysis to avoid conflicts.

#### Help Information

To display help information about SonarQube analysis options:
```
./gradlew sonarHelp
```

#### Avoiding Conflicts

The configuration automatically detects whether you're running in a CI environment (GitHub Actions) or locally, and adjusts settings accordingly to prevent the "You are running manual analysis while Automatic Analysis is enabled" error.

### Code Style with ktlint

This project uses [ktlint](https://github.com/pinterest/ktlint) to enforce consistent code style across all Kotlin files. ktlint is a linter and formatter that follows the official Kotlin coding conventions.

#### Checking Code Style

To check if your code follows the style guidelines:

```
./gradlew ktlintCheck
```

This will scan all Kotlin files and report any style violations without making changes.

#### Formatting Code

To automatically format your code according to the style guidelines:

```
./gradlew ktlintFormat
```

This will fix most style issues automatically. Some issues (like wildcard imports) require manual fixes.

#### Format Before Build

For convenience, a task is provided to format code and then build the project:

```
./gradlew formatAndBuild
```

Note: The `formatAndBuild` task will fail if there are code style violations that cannot be automatically fixed, such as wildcard imports. In these cases, you'll need to manually fix the issues before the build can succeed.

#### IDE Integration

For the best development experience, consider installing ktlint plugins for your IDE:

- **IntelliJ IDEA**: Install the "Ktlint" plugin from the JetBrains Marketplace
- **VS Code**: Install the "Ktlint" extension

These plugins will highlight style issues as you type and can format code on save.

#### Style Rules

The project follows the standard ktlint rules, which include:
- No wildcard imports
- Consistent indentation (4 spaces)
- Proper spacing around operators
- Consistent naming conventions
- And many more

For a complete list of rules, see the [ktlint documentation](https://github.com/pinterest/ktlint#standard-rules).
