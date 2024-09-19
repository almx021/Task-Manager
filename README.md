# Task-Manager
A REST API that allow users to create, read, update and delete tasks. Built with Java Spring and H2 database, tested using MockMVC and Mockito.

## How to Run

### With Maven

* Clone this repository
* Make sure you are using JDK 17 or superior and Maven 3.x
* You can build the project and run the tests by running ```mvn clean package```
* You can run again the tests if you want by running ```nvm test```
* Once successfully built, you can run the service by one of these two methods:
```
        mvn spring-boot:run
or
        java -jar target/task-manager-0.0.1-SNAPSHOT.jar
```
* Once the application runs you should see something like this
```
2024-09-19T02:21:20.361Z  INFO 1 --- [task-manager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2024-09-19T02:21:20.383Z  INFO 1 --- [task-manager] [           main] c.m.task_manager.TaskManagerApplication  : Started TaskManagerApplication in 5.582 seconds (process running for 6.097)
```
### With Docker
* To be implemented


## About the Endpoints

The base endpoint is http://localhost:8080

### 1. Create Task
- **URL**: `/api/tasks`
- **HTTP Method**: `POST`
- **Description**: Creates a new Task.
- **Request Body (JSON)**:
  ```json
    {
    "title": "Task title",
    "description": "Task description"
    }
  ```

### 2. Find Task
- **URL**: `/api/tasks/{id}`
- **HTTP Method**: `GET`
- **Description**: Finds a Task by its ID.

### 3. Find all Tasks
- **URL**: `/api/tasks`
- **HTTP Method**: `GET`
- **Description**: Finds all Tasks.

### 4. Update Task
- **URL**: `/api/tasks/{id}`
- **HTTP Method**: `PUT`
- **Description**: Updates a existing Task.
- **Request Body (JSON)**:
  ```json
    {
    "title": "new Task title",
    "description": "new Task description"
    }
  ```

### 5. Delete Task
- **URL**: `/api/tasks`
- **HTTP Method**: `DELETE`
- **Description**: Deletes a Task.

## Questions and Comments: alm021@hotmail.com