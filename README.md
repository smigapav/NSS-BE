# Meeting Room Reservation System

## Overview

The Meeting Room Reservation System is a Java-based microservice application designed to manage meeting room reservations.

The system allows registration, login, and reservation of meeting rooms by users. Users can view available meeting rooms, check their availability, and reserve a room for a specific date and time. The system also provides functionality to cancel reservations and view reservation history.

## How to access the application

- The application is hosted on Oracle cloud
Deployment is done by cloning/pulling both FE and BE repos on the server and running 
```bash
  docker-compose up --build
```
which ensures both the main application and the microservice gets compiled and the frontend builds.

- The frontend is accessible on the ip address 138.2.133.29 with the port 3000
- Access the app here: [Application](https://138.2.133.29:3000)

## Requirements
- Java/SpringBoot on backend
- React/TypeScript/Vite on frontend
- PostgreSQL database
- Data in the database
- Spring security authorization
- Loggers
- REST
- Production server (Oracle cloud)
- Pipes and Filters architecture

## Design Patterns

The application employs several design patterns to enhance its structure and functionality:

1. **Builder Pattern**: The Order class is constructed using the Builder pattern. This pattern allows for the creation of complex objects step by step, providing a clear and flexible way to create instances of the Order class.

2. **Singleton Pattern**: The microservice uses the Singleton pattern to ensure that only a single instance of the service exists throughout the application. This pattern is used to control access to shared resources, such as database connections or sockets.

3. **Factory Pattern**: The Logger class is created using the Factory pattern. This pattern provides a way to encapsulate the instantiation logic and gives the flexibility to create different types of loggers based on the context.

4. **Adapter Pattern**: The Mapper uses the Adapter pattern to convert the interface of a class into another interface that clients expect. This allows classes to work together that couldn't otherwise because of incompatible interfaces.

5. **Factory Method Pattern**: The Spring application initialization process uses the Factory Method pattern.
## Docker

Docker is used to containerize the application, providing a consistent environment for it to run in, regardless of the host system. The application and its dependencies are packaged into a Docker image, which can be run as a container on any system with Docker installed. The Docker configuration for the application is specified in the [docker-compose.yml](docker-compose.yml) file.

## Running the Application on local

To run the application on local, you need to have Docker installed on your system. 
Once Docker is installed, you need to package the projects, and then you can start the application by running the following command in the terminal
from the root directory of the project:

```bash
docker-compose up --build