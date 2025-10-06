#!/bin/bash
# Script to verify Gradle wrapper integrity

echo "Verifying Gradle wrapper integrity..."

# Check if gradle-wrapper.jar exists
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "ERROR: gradle-wrapper.jar not found!"
    exit 1
fi

# Check if checksum file exists
if [ ! -f "gradle/wrapper/gradle-wrapper.jar.sha256" ]; then
    echo "ERROR: gradle-wrapper.jar.sha256 not found!"
    exit 1
fi

# Verify checksum
cd gradle/wrapper
COMPUTED_CHECKSUM=$(sha256sum gradle-wrapper.jar | cut -d ' ' -f 1)
STORED_CHECKSUM=$(cat gradle-wrapper.jar.sha256 | tr -d ' \t\n')

if [ "$COMPUTED_CHECKSUM" = "$STORED_CHECKSUM" ]; then
    echo "✓ Gradle wrapper checksum verification PASSED"
    echo "Checksum: $STORED_CHECKSUM"
    exit 0
else
    echo "✗ Gradle wrapper checksum verification FAILED"
    echo "Computed: $COMPUTED_CHECKSUM"
    echo "Stored:   $STORED_CHECKSUM"
    exit 1
fi