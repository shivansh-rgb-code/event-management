# GDG Backend — Task 1

## Event Management System

A REST API for managing society events. This project was built as part of the GDG Backend Task 1 to practice CRUD operations, validation, database integration, API features, business logic, automated testing, and audit logging.

## Tech Stack

* Java
* Spring Boot
* Spring Data JPA
* MySQL
* H2
* Maven
* Postman
* JUnit

## Features

* Create, view, update and delete events
* Input validation
* Search events by name or description
* Filter events by venue and status
* Pagination
* Sorting by date, name and capacity
* Consistent error responses
* Business rule validation
* Duplicate event detection
* MySQL database storage
* Automated API testing
* Persistent audit logging for event creation, updates and deletion

## Event Details

Each event contains:

* Event name
* Description
* Start date and time
* End date and time
* Venue
* Maximum capacity
* Status

Available statuses:

* `UPCOMING`
* `ONGOING`
* `COMPLETED`
* `CANCELLED`

## API Endpoints

| Method | Endpoint       | Purpose         |
| ------ | -------------- | --------------- |
| POST   | `/events`      | Create an event |
| GET    | `/events`      | Get events      |
| GET    | `/events/{id}` | Get one event   |
| PUT    | `/events/{id}` | Update an event |
| DELETE | `/events/{id}` | Delete an event |

### Search and Filtering

```text
GET /events?search=hackathon
GET /events?venue=auditorium
GET /events?status=UPCOMING
```

Search and filters can also be combined.

### Pagination

```text
GET /events?page=1&limit=10
```

### Sorting

```text
GET /events?sort=date&order=asc
```

Supported sorting fields include date, name and capacity.

## Validation and Business Rules

The API enforces the following rules:

* Required fields cannot be empty.
* Capacity must be greater than 0.
* An event cannot be created with a start time in the past.
* End time must be after start time.
* Event status must match the event's date and time.
* Ongoing, completed and cancelled events cannot be freely updated.
* Only upcoming events can be deleted.
* Duplicate events are rejected.

For duplicate detection, an event is considered a duplicate when it has the same name, start date/time and venue as another event.

## Error Handling

The API returns appropriate HTTP status codes:

* `200 OK` — successful request
* `201 Created` — event successfully created
* `204 No Content` — event successfully deleted
* `400 Bad Request` — validation or business-rule failure
* `404 Not Found` — event does not exist
* `409 Conflict` — duplicate event

Errors are returned in a consistent JSON format instead of exposing raw database or framework errors.

## Audit Logging

The application maintains a persistent audit log for successful event operations.

The following actions are recorded:

* `CREATE`
* `UPDATE`
* `DELETE`

Each audit record stores:

* Event ID
* Event name
* Action performed
* Timestamp

Audit records are stored separately from events, allowing the history of an event to remain available even after the event itself is deleted.

Audit logging is performed internally by the service layer and does not require a separate API endpoint.

## Automated Testing

The project includes automated API tests using:

* JUnit
* Spring Boot Test
* MockMvc
* H2 in-memory database

The test suite verifies CRUD operations, validation, business rules, duplicate detection, search, filtering, pagination, sorting, and audit logging.

The tests are isolated from the development MySQL database by using an H2 in-memory database.

Current test result:

```text
Tests run: 17
Failures: 0
Errors: 0
Skipped: 0
```

## Database

The project uses MySQL for normal application use.

Create a database named:

```sql
CREATE DATABASE gdg_events;
```

The application uses Spring Data JPA to create and update the required tables.

For automated tests, an H2 in-memory database is used instead of MySQL.

## Running the Project

### 1. Clone the repository

```bash
git clone <your-repository-url>
```

### 2. Create the database

Create the `gdg_events` database in MySQL.

### 3. Configure MySQL

Update `application.properties` with your MySQL username and password.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gdg_events
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

Do not commit your actual database password to GitHub.

An `application.properties.example` file is included as a configuration template.

### 4. Run the application

Run the Spring Boot application from IntelliJ or using Maven.

The API will run at:

```text
http://localhost:8080
```

## Running Tests

Run the automated test suite using Maven:

```bash
./mvnw clean test
```

On Windows:

```powershell
.\mvnw.cmd clean test
```

The tests use the H2 in-memory database and do not modify the MySQL development database.

## Postman

The `postman` folder contains the Postman collection used to test the API.

The collection demonstrates:

* CRUD operations
* Validation failures
* Business-rule failures
* Duplicate detection
* Search and filtering
* Pagination
* Sorting

Import the collection into Postman and make sure the Spring Boot application is running before sending requests.

## Project Structure

```text
src/main/java/com/example/event_management
├── controller
├── entity
├── exception
├── repository
└── service

src/test/java/com/example/event_management
├── EventManagementApplicationTests.java
└── EventControllerTests.java

postman
└── GDG-Backend-Task1.postman_collection.json
```

## Author

Shivansh Srivastava
