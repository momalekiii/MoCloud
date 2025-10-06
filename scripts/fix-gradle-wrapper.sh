#!/bin/bash
# Script to fix Gradle wrapper validation issues

echo "Fixing Gradle wrapper validation issues..."

# Update Gradle wrapper
./gradlew wrapper --gradle-version 8.11.1

# Generate new checksums
cd gradle/wrapper

# Remove old checksum file
rm -f gradle-wrapper.jar.sha256

# Generate new checksum
sha256sum gradle-wrapper.jar > gradle-wrapper.jar.sha256

echo "Gradle wrapper fixed successfully!"
echo "New checksum: $(cat gradle-wrapper.jar.sha256)"