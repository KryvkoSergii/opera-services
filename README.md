# Clear Breath – Distributed Audio Analysis Platform (Kotlin, TypeScript, Python, AI)

## Overview

Clear Breath is a prototype platform for analyzing respiratory acoustic signals (such as breathing and cough) using pre-trained deep learning models.

The system demonstrates how modern AI models can be integrated into a scalable, production-like architecture to process audio data asynchronously and deliver interpretable results to end users.

This project was developed as part of a Master’s research work focused on applying deep learning to non-invasive respiratory signal analysis.

---

## Key Features

### 🎤 Audio Input
- Accepts recordings from:
  - Built-in device microphones
  - External electronic stethoscopes
- Supports:
  - File upload
  - Real-time recording via web interface

---

### ⚙️ Asynchronous Processing
- Audio is processed via an asynchronous pipeline
- Services are decoupled using a message queue pattern
- Designed for scalability and fault tolerance

---

### 🧠 Deep Learning Inference
- Uses pre-trained respiratory acoustic model **OPERA-CT**
- Extracts feature embeddings and performs classification tasks
- Designed to work effectively with limited labeled datasets

---

### 📊 Interpretable Results
- Returns probabilistic estimates of potential respiratory conditions
- Displays results in a user-friendly interface
- Stores and provides access to historical analyses

---

### ⚠️ Non-Medical Disclaimer
> This system is **not a medical diagnostic tool**.  
> It provides probabilistic insights for research and demonstration purposes only.

---

## Architecture

The platform follows a modular, microservices-based architecture.

### Frontend (React / TypeScript)
- Handles user interaction and audio capture
- Displays processing status and analysis results

### Backend Orchestrator (Kotlin / Spring Boot)
- Manages request lifecycle
- Handles authentication and user management
- Coordinates workflow between services
- Stores metadata and results in the database

### ML Inference Service (Python / PyTorch)
- Performs feature extraction and model inference
- Uses OPERA-based pre-trained models

### Infrastructure
- Object storage for audio files (S3-compatible)
- Message queue for asynchronous communication (SQS)
- Relational database for metadata and results

> This separation of concerns allows independent scaling of compute-intensive ML workloads and user-facing services.

---

## Processing Pipeline

1. User records or uploads an audio sample  
2. Audio is normalized and preprocessed  
3. Request is sent to an asynchronous processing queue  
4. ML service performs inference using OPERA-CT  
5. Results are stored and returned to the user  

---

## Motivation

Respiratory sound analysis has strong potential for non-invasive pre-screening, especially in environments with limited access to medical professionals.

This project explores how:
- Large-scale pre-trained models  
- Modern web technologies  
- Cloud-native architectures  

can be combined into a practical, extensible system.

---

## Limitations

- Not clinically validated  
- Sensitive to recording quality and environment  
- Model performance depends on domain similarity to training data  

---

## Repository Structure

This repository is a monorepo that contains three main packages:

- `/frontend` - React-based web application (TypeScript, npm)
- `/backend` - Backend services (Kotlin/Gradle, Python/pip)
- `/contract` - Shared contracts/interfaces between frontend and backend

## Backend Architecture

The backend consists of two main services:

### 1. Orchestration ("analysis") Service

- Coordinates the analysis workflow  
- Sends events to extractor service via SQS  
- Receives results via SQS and updates the database  
- Exposes REST APIs for frontend consumption  
- Handles authentication and user management  
- Stores analysis results and metadata  
- Resamples audio files (e.g., 16kHz, mono) before processing  

---

### 2. Extractor Service

- Responsible for feature extraction and ML inference  
- Listens to SQS for incoming analysis requests  
- Processes audio using libraries such as Librosa  
- Generates feature embeddings  
- Sends results back via SQS  

---

## Local Development

The entire stack - frontend, backend services, and any dependent infrastructure \- can be started locally using Docker Compose.

`docker-compose up`

### Prerequisites

- Docker
- Docker Compose
- NPM (for frontend development)
- Gradle (for backend development)
- Python (for extractor service development)
