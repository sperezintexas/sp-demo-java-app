#!/bin/bash

# Load environment variables from .env file if it exists
if [ -f .env ]; then
  export $(cat .env | grep -v '^#' | xargs)
  echo "Loaded environment variables from .env file"
else
  echo "Warning: .env file not found. Make sure to set the required environment variables manually."
fi

# Set default values for environment variables if not already set
SL_TOKEN=${SL_TOKEN:-"your-token-here"}
SL_APPNAME=${SL_APPNAME:-"demo-app"}
SL_BUILDNAME=${SL_BUILDNAME:-"build-local-$(date +%s)"}
SL_BRANCHNAME=${SL_BRANCHNAME:-"main"}

echo "Using the following configuration:"
echo "SL_APPNAME: $SL_APPNAME"
echo "SL_BUILDNAME: $SL_BUILDNAME"
echo "SL_BRANCHNAME: $SL_BRANCHNAME"
echo "SL_TOKEN: ${SL_TOKEN:0:10}... (truncated for security)"

# Create sealights directory if it doesn't exist
mkdir -p sealights

# Download SeaLights Java Agent if not already downloaded
if [ ! -f sealights/sl-build-scanner.jar ]; then
  echo "Downloading SeaLights Java Agent..."
  wget -nv https://agents.sealights.co/sealights-java/sealights-java-latest.zip
  unzip -oq sealights-java-latest.zip -d sealights
  echo "SeaLights Java Agent version used is:"
  cat sealights/sealights-java-version.txt
else
  echo "SeaLights Java Agent already downloaded."
fi

# Run the createBuildSession command and capture output
echo "Running sl.config command..."
java -jar sealights/sl-build-scanner.jar -config  -tokenfile "./sealights/sltoken.txt" \
-appname "sp-demo-java-app"  -buildname "DEV"  -branchname "main" \
-pi "com.sealights.demoapp.*"
echo "Output from config:"
cat session_id.txt

echo "Session ID has been saved to buildSessionId.txt"