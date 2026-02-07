
# Clear Breath - Audio Analysis Platform

This repository is a monorepo that contains three main packages:

- `/frontend` - React-based web application (TypeScript, npm)
- `/backend` - Backend services (Kotlin/Gradle, Python/pip)
- `/contract` - Shared contracts/interfaces between frontend and backend

## Backend Architecture

The backend consists of two services:

1. **Orchestration \("analysis"\) service**  
   - Coordinates the analysis workflow  
   - Sends event to extractor service using SQS
   - Receives results from extractor service via SQS and updates the database accordingly
   - Exposes REST APIs for the frontend to consume and display results
   - Handles authentication and user management
   - Manages database interactions for storing analysis results and user data
   - Resamples audio files to a consistent format (e.g., 16kHz, mono) before sending them to the extractor service


2. **Extractor service**  
   - Responsible for extracting and preparing raw data  
   - Listens to SQS for incoming analysis requests from the orchestration service
   - Extracts features from the audio files (e.g., using Librosa or similar libraries)
   - Sends the extracted features back to the orchestration service via SQS for further processing

## Local Development With Docker Compose

The entire stack - frontend, backend services, and any dependent infrastructure \- can be started locally using Docker Compose.

`docker-compose up`

### Prerequisites

- Docker
- Docker Compose
- NPM (for frontend development)
- Gradle (for backend development)
- Python (for extractor service development)