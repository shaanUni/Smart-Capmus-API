# Smart Campus API

A RESTful API built with JAX-RS for managing university rooms, sensors, and historical sensor readings in a Smart Campus environment.

## Overview

This project implements a versioned REST API for three main resources:

- **Room**: a campus room with an id, name, capacity, and assigned sensor ids
- **Sensor**: a device installed in a room, such as a CO2 or temperature sensor
- **SensorReading**: a historical reading recorded for a specific sensor

The API is built using:

- Java
- Maven
- JAX-RS (Jersey)
- Embedded Grizzly HTTP server

The application uses in memory data structures, not a DB.

## API Design

Base path:

`/api/v1`

Main resources:

- `/api/v1/rooms`
- `/api/v1/sensors`

Nested sub-resource:

- `/api/v1/sensors/{sensorId}/readings`

### Features

- discovery endpoint at `GET /api/v1/`
- create, list, fetch, and delete rooms
- create, list, fetch, and filter sensors by type
- nested sensor reading history
- automatic update of a sensor's `currentValue` when a reading is posted
- custom exception mapping for:
  - `409 Conflict`
  - `422 Unprocessable Entity`
  - `403 Forbidden`
  - `500 Internal Server Error`
- request and response logging using JAX-RS filters

## Project Structure

```text
src/main/java/com/mycompany/smart/campus/api/
├── SmartCampusApi.java
├── config/
│   └── AppConfig.java
├── model/
│   ├── ApiError.java
│   ├── Room.java
│   ├── Sensor.java
│   └── SensorReading.java
├── store/
│   └── DataStore.java
├── resource/
│   ├── DiscoveryResource.java
│   ├── RoomResource.java
│   ├── SensorResource.java
│   └── SensorReadingResource.java
├── exception/
│   ├── LinkedResourceNotFoundException.java
│   ├── RoomNotEmptyException.java
│   └── SensorUnavailableException.java
├── mapper/
│   ├── GenericExceptionMapper.java
│   ├── LinkedResourceNotFoundExceptionMapper.java
│   ├── RoomNotEmptyExceptionMapper.java
│   └── SensorUnavailableExceptionMapper.java
└── filter/
    └── ApiLoggingFilter.java
```

## How to Build and Run

### Requirements
Java  
Maven  
apache netbeans

### Build
```
Clik the Green arrow button at the top
```

### Run

From NetBeans, run SmartCampusApi.java.

When the server starts successfully, it runs at:

```
http://localhost:8080/api/v1/
```

If port 8080 is already in use, stop the existing process or change the port in SmartCampusApi.java.

## Endpoint Summary

### Discovery
GET /api/v1/

### Rooms
GET /api/v1/rooms  
POST /api/v1/rooms  
GET /api/v1/rooms/{roomId}  
DELETE /api/v1/rooms/{roomId}  

### Sensors
GET /api/v1/sensors  
POST /api/v1/sensors  
GET /api/v1/sensors/{sensorId}  
GET /api/v1/sensors?type=CO2  

### Sensor Readings
GET /api/v1/sensors/{sensorId}/readings  
POST /api/v1/sensors/{sensorId}/readings  

## Sample curl Commands

### 1. Discovery endpoint
```
curl -X GET http://localhost:8080/api/v1/
```

### 2. List all rooms
```
curl -X GET http://localhost:8080/api/v1/rooms
```

### 3. Create a room
```
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"SCI-201\",\"name\":\"Science Lab\",\"capacity\":30}"
```

### 4. Create a sensor
```
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"CO2-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":0,\"roomId\":\"SCI-201\"}"
```

### 5. Filter sensors by type
```
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
```

### 6. Add a reading to a sensor
```
curl -X POST http://localhost:8080/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":412.5}"
```

### 7. Attempt to delete a room that still has sensors
```
curl -X DELETE http://localhost:8080/api/v1/rooms/SCI-201
```

## Example Request Bodies

### Create room
```json
{
  "id": "SCI-201",
  "name": "Science Lab",
  "capacity": 30
}
```

### Create sensor
```json
{
  "id": "CO2-001",
  "type": "CO2",
  "status": "ACTIVE",
  "currentValue": 0,
  "roomId": "SCI-201"
}
```

### Create maintenance sensor
```json
{
  "id": "TEMP-M1",
  "type": "Temperature",
  "status": "MAINTENANCE",
  "currentValue": 0,
  "roomId": "SCI-201"
}
```

### Add reading
```json
{
  "value": 412.5
}
```

## Error Handling

The API uses custom JSON error responses instead of returning raw stack traces or default server error pages.

Implemented cases include:

- 409 Conflict: deleting a room that still has sensors  
- 422 Unprocessable Entity: creating a sensor with a non-existent room  
- 403 Forbidden: posting a reading to a sensor in MAINTENANCE  
- 500 Internal Server Error: unexpected runtime failures  

## Notes

This coursework uses JAX-RS only  
No Spring Boot is used  
No SQL or external database is used  
Data is stored using in-memory collections, so restarting the application resets the data  
Please contact Shaan if any questions - w2072520@westminster.ac.uk

## Report Answers

### Part 1.1 — Resource lifecycle

By default, JAX-RS resource classes are usually request-scoped. This means that a new resource instance is created for each incoming request. This design prevents shared mutable state inside resource objects and reduces the risk of thread-safety issues. Since this API keeps data in memory, shared data should not be stored in the instance fields of resource classes. If it were, the data could be lost or inconsistent between requests. Instead, shared collections should be kept in a separate shared store. These should be protected with thread-safe structures or synchronization to avoid race conditions when multiple requests access the API simultaneously.


### Part 1.2 — Hypermedia / HATEOAS

Hypermedia is considered an advanced REST principle because the server provides navigation links inside responses, allowing clients to discover what actions and resources are available dynamically. This reduces coupling between the client and hard-coded endpoint knowledge. Compared with relying only on static documentation, hypermedia makes the API more self-descriptive and easier to evolve, because clients can follow links exposed by the service rather than assuming all URLs in advance.


### Part 2.1 — Returning IDs vs full room objects

Returning only room IDs reduces payload size and saves bandwidth, which is useful when there are many rooms or when clients only need identifiers for later requests. However, it usually forces the client to make additional requests to fetch details such as room name, capacity, and assigned sensors. Returning full room objects increases payload size but improves usability because clients receive all important metadata in a single response. In this implementation, full room objects were returned because they are more practical for testing and reduce unnecessary follow-up requests.

### Part 2.2 — Is DELETE idempotent?

DELETE is idempotent when sending the same request multiple times has the same overall effect on server state as sending it once. In this implementation, if a room is empty and exists, the first DELETE removes it. A repeated DELETE for the same room then returns 404 Not Found because the room no longer exists, but the server state remains unchanged after the first successful deletion. If the room contains sensors, the DELETE request is rejected with 409 Conflict each time until the condition changes. Therefore, the operation is idempotent with respect to state, even though repeated requests may return different status codes.

### Part 3.1 — What happens if the client sends non-JSON?

The POST method is annotated with @Consumes(MediaType.APPLICATION_JSON), so the API declares that it expects JSON request bodies. If a client sends text/plain, application/xml, or another unsupported media type, JAX-RS will fail content negotiation and normally return 415 Unsupported Media Type. The request will not be deserialised into the Java object because there is no suitable message body reader for that format in the method contract. This protects the endpoint by enforcing a clear and predictable input format.

### Part 3.2 — Why use a query parameter for filtering?

Using @QueryParam("type") is more appropriate for filtering because the client is still requesting the same collection resource, namely the sensors collection, but with a constraint applied. Query parameters are widely understood as a mechanism for search, filtering, sorting, and pagination. A path such as /sensors/type/CO2 suggests a different resource hierarchy rather than a filtered view of the same collection. Query parameters are therefore more flexible, more conventional, and easier to extend later with additional filters such as status or room.

### Part 4.1 — Benefits of the Sub-Resource Locator pattern

The Sub-Resource Locator pattern improves structure by delegating nested resource behaviour to a dedicated class. Instead of keeping all logic for sensors and their readings inside one large resource class, the readings behaviour is separated into SensorReadingResource, which is responsible only for the readings of a specific sensor. This improves readability, maintainability, and testability. In larger APIs, it prevents controller classes from becoming overly large and makes the resource hierarchy clearer because each class represents a logical responsibility.

### Part 5.2 — Why is 422 more accurate than 404 here?

HTTP 422 Unprocessable Entity is often more accurate than 404 Not Found in this case because the request itself reaches a valid endpoint and the JSON body is syntactically valid, but one field inside that JSON references a resource that does not exist. A 404 usually means the requested URI itself does not exist. Here, the endpoint /sensors exists, but the submitted payload cannot be processed because the roomId is invalid. Therefore, 422 better communicates that the semantic problem lies inside the request body rather than the URL.

### Part 5.4 — Risks of exposing Java stack traces

Exposing internal Java stack traces creates security risks because it reveals technical details about the implementation. An attacker may learn class names, package names, framework versions, file paths, method names, internal control flow, and which parts of the code failed. This information can help them identify weak points, guess the project structure, and craft more targeted attacks. Returning a generic 500 Internal Server Error message instead is safer because it avoids leaking sensitive implementation details to external users.

### Part 5.5 — Why use filters for logging?

Filters are better for cross-cutting concerns like logging because they keep logging logic separate from business logic. Instead of repeating Logger.info() statements in every resource method, one request filter and one response filter can handle logging consistently for all endpoints. This reduces duplication, improves maintainability, and ensures a standard logging approach across the API. It also makes the resource classes cleaner and more focused on handling domain behaviour rather than infrastructure concerns.
````
