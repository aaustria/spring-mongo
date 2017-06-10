# Spring Boot with MongoDB
Sample project for learning Spring

# Dependencies

- Maven ( OSX: brew install maven )

# Build and Deploy
```bash
mvn clean package
```
# Running jars locally
```bash
#run application
java -jar ./target/event-0.0.1-SNAPSHOT.jar
```

# API

```bash
Get all events
GET /api/events?page={pageNumber}

Get event by Id
GET /api/event/{id}

Get events by title
GET /api/events?t={title}&page={pageNumber}

Get events by start date and end date
GET /api/events/s/{start}/e/{end}

Submit new event
POST /api/event

Update event
PUT /api/event/{id}

Delete event
DELETE /api/event/{id}

Register user to the event
PUT /api/event/register/{eventId}/{userId}

Deregister user to the event
PUT /api/event/deregister/{id}/{userId}

Create new user
POST /api/user/register

Get user by username
GET /api/user/{userName}
```
