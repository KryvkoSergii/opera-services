# OPERA-CT Inference Module

This module is dedicated to accept events from AWS SQS, downloads audio-files from AWS S3, inference OPERA-CT encoder
using codebase `https://github.com/evelyn0414/OPERA.git` and emit result of inference to another AWS SQS.

## Overview

The service listens to an input AWS SQS queue with events describing audio files stored in AWS S3.  
For each event it:

1. Downloads the referenced audio file from AWS S3.
2. Runs inference using the OPERA-CT encoder.
3. Applies linear classifiers on top of the encoder outputs.
4. Publishes the prediction result to an output AWS SQS queue.

The module supports the following:

- audio-sources modality: microphone or stethoscope
- estimates probability of: COVID-19 and COPD

## Requirements

Module requires:

- AWS credentials with permissions to read and write to SQS queues.
- Access to OPERA-CT encoder (take from `https://github.com/evelyn0414/OPERA.git`).
- [Linear classifiers](https://drive.google.com/drive/u/0/folders/1Tg1nQmul87nRW6NbvmU-CToLGlhmz4_T) trained on top of OPERA-CT encoder outputs. These classifiers are not included in this repository
  and should be provided separately.

## Contracts and Model Generation

`/scripts/generate_models.py` generates models from the AsyncApi spec.  
The generated models are placed in `contracts/models/` and taken from `contract/events.yaml`.