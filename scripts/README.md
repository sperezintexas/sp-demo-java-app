# SeaLights Session ID Test Scripts

This directory contains scripts to test the SeaLights Session ID generation process that is used in the CI/CD pipeline.

## Available Scripts

### Linux/macOS (Bash)

- `test-sealights-session.sh`: Bash script for Linux/macOS environments

## How to Use

### Windows

#### Option 1: Using the Batch File (Easiest)

1. Navigate to the `scripts` directory in File Explorer
2. Double-click on `test-sealights-session.bat`

#### Option 2: Using PowerShell

1. Open PowerShell
2. Navigate to the project root directory
3. Run the script:
   ```powershell
   .\scripts\test-sealights-session.ps1
   ```

### Linux/macOS

1. Open Terminal
2. Navigate to the project root directory
3. Make the script executable (first time only):
   ```bash
   chmod +x scripts/test-sealights-session.sh
   ```
4. Run the script:
   ```bash
   ./scripts/test-sealights-session.sh
   ```

## What the Script Does

The script performs the following actions:

1. Loads environment variables from the `.env` file if it exists
2. Sets default values for required environment variables if not already set:
   - `SL_TOKEN`: Authentication token for SeaLights
   - `SL_APPNAME`: Application name in SeaLights
   - `SL_BUILDNAME`: Build name for SeaLights
   - `SL_BRANCHNAME`: Branch name for SeaLights
3. Downloads the SeaLights Java Agent if not already downloaded
4. Runs the `createBuildSession` command to generate a Session ID
5. Captures the output and saves it to `session_id.txt`

## Environment Variables

The script uses the following environment variables:

- `SL_TOKEN`: Authentication token for SeaLights
- `SL_APPNAME`: Application name in SeaLights (default: "demo-app")
- `SL_BUILDNAME`: Build name for SeaLights (default: "build-local-[timestamp]")
- `SL_BRANCHNAME`: Branch name for SeaLights (default: "main")

These can be set in the `.env` file or as environment variables before running the script.
