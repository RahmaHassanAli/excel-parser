# Excel Parser API

## What it does
A Spring Boot REST API that:
- Uploads an Excel (.xlsx) file with employee records.
- Parses and validates rows (required fields, email and mobile formats, numeric salary).
- Stores the uploaded file on the server (uploads/).
- Returns processing status, valid records count, validation errors, and stored file path.
- Provides a download endpoint that returns the stored file as a Base64 string.

## Endpoints
- `POST /api/excel/upload` — multipart form-data key: `file`. Returns JSON:
  - `status` (SUCCESS / FAILED)
  - `validRecords` (int)
  - `errors` (array of validation errors with row numbers)
  - `storedFilePath` (string)

- `POST /api/excel/download` — JSON body: `{ "filePath": "<server path>" }`. Returns:
  - `fileName`
  - `base64` (Base64 encoded file content)

## Run locally
Requirements: Java 17+, Maven
```bash
mvn clean package
mvn spring-boot:run
# app runs at http://localhost:8080
