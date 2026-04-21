# 🏛️ Smart Campus Sensor & Room Management API

[![Java 17](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![JAX-RS](https://img.shields.io/badge/JAX--RS-3.1-orange.svg)](https://jakarta.ee/specifications/restful-ws/)
[![Maven](https://img.shields.io/badge/Maven-3.0+-green.svg)](https://maven.apache.org/)

## 📑 Table of Contents
- [Project Overview](#1-project-overview)
- [How to Build & Run](#2-how-to-build--run)
- [Sample cURL Commands](#3-sample-curl-commands-api-demonstration)
- [Assessment Conceptual Report](#4-assessment-conceptual-report-qa)

---

## 1. 🚀 Project Overview
This project is a high-performance RESTful web service built using Java and JAX-RS (Jakarta RESTful Web Services), specifically the Jersey framework powered by a built-in Grizzly HTTP server.

It is designed to manage university 'Smart Campus' infrastructure, including `Rooms`, `Sensors` (e.g. CO2, Temperature), and historical nested `SensorReadings`. Per assignment constraints, this application solely utilizes basic Java mapping techniques (`ConcurrentHashMap`) acting as an in-memory datastore rather than an external database. It implements strict layered error handling with appropriate HTTP status codes (409, 422, 403, 500) and API-wide traffic logging.

## 2. 🛠️ How to Build & Run
**Prerequisites:** You must have Java 17 and Maven 3+ installed and configured on your system path.

1. Clone the repository and navigate into the root directory of the project in terminal.
2. Compile and package the application using Maven:
   ```bash
   mvn clean compile
   ```
3. Boot up the embedded Grizzly Server instance via the Maven Exec Plugin:
   ```bash
   mvn exec:java
   ```
4. The server will launch immediately and listen for requests at: `http://localhost:8080/api/v1`
5. Press `ENTER` directly in the terminal where it's running when you are ready to cleanly shut down the server process.

## 3. 🧪 Sample cURL Commands (API Demonstration)

Here are five key endpoints demonstrating integration with varying parts of the API:

**1. View Setup & Discovery Endpoint (HATEOAS Linked Roots)**
```bash
curl -X GET http://localhost:8080/api/v1/
```

**2. Create a new Room using JSON Array format**
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"LIB-888", "name":"Library Digital Hub", "capacity":75}'
```

**3. Fetch a List of All Currently Managed Rooms**
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

**4. Link a New Sensor to the freshly created Room**
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"LUM-66", "type":"Lighting", "status":"ACTIVE", "roomId":"LIB-888"}'
```

**5. Process a new Reading for that Sensor (Sub-Resource Location)**
*Due to mapping logic, successfully appending this nested Reading will automatically update the parent LUM-66's caching.*
```bash
curl -X POST http://localhost:8080/api/v1/sensors/LUM-66/readings \
-H "Content-Type: application/json" \
-d '{"value":85.0}'
```

***

## 4. 📝 Assessment Conceptual Report (Q&A)

**Part 1: Service Architecture & Setup**

*   **Question**: Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.
*   **Answer**: JAX-RS resource classes have a default per-request lifecycle model. This implies that a new object of the resource class is made in each incoming HTTP request and is destroyed after a reply is made. The data should be stored outside the resource instance to ensure that it is not lost or subject to race conditions in case of using in-memory data structures (such as Maps or Lists) whose lifecycle is this way. This is usually done by either a static collection data store making the data store a Singleton or by using thread-safe collections (such as ConcurrentHashMap) in an application-scoped provider so that concurrent access modifying the data by multiple requests does not corrupt it or cause a concurrency exception.

*   **Question**: Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?
*   **Answer**: The Hypermedia as the Engine of Application State (HATEOAS) enables clients to discover actions and resources dynamically, by using links included in the API responses instead of hardcoded URIs and documenting everything in advance. This isolates the client to the routing topology of the server and thus the API is less susceptible to changes, a change of resource URL results in the client following the new link in the response without needing a hard-coded code change in the client.

**Part 2: Room Management**

*   **Question**: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.
*   **Answer**: Sending just the IDs reduces the size of the payload, saving network bandwidth and accelerating the initial response, but causes the client to have to send many more requests (the N+1 problem) to get the information about those rooms, which raises latency. On the other hand, full object retrieval consumes more bandwidth and server-side processing on the first request, but removes the need to make subsequent calls which can be more efficient in many cases when used by clients to render lists or dashboards.

*   **Question**: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.
*   **Answer**: Yes, the `DELETE` operation is idempotent. In case a client sends a duplicate of the same Delete request with the exact same room, the initial request will be effectively executed to remove the room and a 204 No Content or a 200 OK response will be received. Further requests of the same type will discover that the room no longer exists and will give a 404 Not Found. Although the first and the second request have different HTTP status code, the server state (the room is deleted) is the same, and this meets the definition of idempotency.

**Part 3: Sensor Operations & Linking**

*   **Question**: We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?
*   **Answer**: The `@Consumes(MediaType.APPLICATION_JSON)` annotation acts as a strict filter that states that the resource accepts only the data in the form of a JSON. In case a client delivers data using an alternate format such as text/plain or application/xml, JAX-RS will reject the request before it can even get to the method logic. It automatically sends an HTTP/415 Unsupported Media Type error to the client, enforcing accurate content negotiation and not allowing the application to make the effort of parsing invalid payload types.

*   **Question**: You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?
*   **Answer**: The query parameters (e.g., `?type=CO2`) can be used to better filter, sort or search as they are not based on the hierarchical organization of the resource, but serve to reduce an existing collection. Path parameters (e.g., /type/CO2) suggest to find a particular, distinct nested resource entity. The query parameters are optional and can be easily combined (e.g., `?type=CO2&status=ACTIVE) and thus is far more adaptable to dynamically filtering without contaminating the static routing structure of the API.

**Part 4: Deep Nesting with Sub-Resources**

*   **Question**: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?
*   **Answer**: Sub-Resource Locator pattern enables a parent resource (e.g., SensorResource) to pass on the processing of child paths (e.g., /{ sensor id }/readings ) to a specific child resource class (SensorReadingResource). This eliminates huge, single-class controllers by following the Single Responsibility Principle. It maintains a code style that is more modular, routing logic that is significantly easier to read and test, and naturally retains the contextual parent ID (the sensorId) without having to manually map it in each nested endpoint method.

**Part 5: Advanced Error Handling**

*   **Question**: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?
*   **Answer**: The HTTP 404 Not Found status means that the endpoint (the URL) does not exist. But, when POSTing a new sensor whose roomId does not exist, the target address (`/api/v1/sensors) is legitimate, and the JSON representation is syntactically correct. The HTTP 422 Unprocessable Entity is more precise as it means that the server knows the type and syntax of the request entity, but cannot handle the instructions within it because of semantic validation errors (in this case, a failure of a foreign key existence check).

*   **Question**: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?
*   **Answer**: Publication of raw Java stack trace exposes internal technical information to external consumers. A hacker can obtain details regarding the exact frameworks utilized, the version of those libraries, internal package and classes structure of the application, and possibly the operating system or file paths it is running on. The information footprint can greatly help an attacker locate specific vulnerabilities or create specific injection attacks.

*   **Question**: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?
*   **Answer**: JAX-RS filters are a declarative, centralized mechanism to manage cross-cutting concerns such as logging or security using the principle of Aspect-Oriented Programming. Filtering provides uniformity throughout the API, significantly decreases the amount of duplication in the code, and ensures that the underlying business logic within the resource methods is simple and only focused on processing the request, not on infrastructure scaffolding.
