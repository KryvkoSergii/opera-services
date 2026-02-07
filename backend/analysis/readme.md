# Analysis Backend Module

This module is the orchestration \(\"analysis\"\) service responsible for coordinating the audio analysis workflow and exposing results via REST APIs.

## Responsibilities

- Coordinates the analysis workflow
- Sends events to the extractor service using SQS
- Receives results from the extractor service via SQS and updates the database accordingly
- Exposes REST APIs for the frontend to consume and display results
- Handles authentication and user management
- Manages database interactions for storing analysis results and user data
- Resamples audio files to a consistent format (e.g., 16kHz, mono) using FFmpeg before sending them to the extractor service

## Technology stack

- Language: Kotlin
- Framework: Spring Boot
- Build tool: Gradle
- Messaging: Amazon SQS
- Remote storage: Amazon S3 for storing audio files
- Database: configured via Spring Data
- Audio processing: FFmpeg for resampling audio files

## Building

To build using Gradle:
```bash
./gradlew build
```

to run the service:

```bash
./gradlew bootRun
```