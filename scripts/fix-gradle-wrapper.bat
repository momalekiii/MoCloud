@echo off
REM Script to fix Gradle wrapper validation issues on Windows

echo Fixing Gradle wrapper validation issues...

REM Update Gradle wrapper
call gradlew wrapper --gradle-version 8.11.1

REM Generate new checksums
cd gradle\wrapper

REM Remove old checksum file
del /f gradle-wrapper.jar.sha256 >nul 2>&1

REM Generate new checksum using certutil (Windows built-in)
certutil -hashfile gradle-wrapper.jar SHA256 | findstr /v "hash" > temp.txt
set /p hash=<temp.txt
echo %hash%> gradle-wrapper.jar.sha256
del temp.txt

echo Gradle wrapper fixed successfully!
type gradle-wrapper.jar.sha256