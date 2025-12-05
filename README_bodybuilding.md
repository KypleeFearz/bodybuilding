# Fullstack Application (Java Backend & Svelte Frontend)

## Overview
This repository contains a fullstack web application demonstrating:
- A Java backend built with Maven
- A Svelte-based frontend
- A REST API architecture
- Containerization via Docker

## Tech Stack

### Backend
- Java  
- Maven  
- REST controllers  

### Frontend
- Svelte  
- JavaScript  
- Component-based UI  

### Deployment
- Dockerfile  
- Local and containerized execution  

## Repository Structure
/backend  
/frontend  
Dockerfile  
README.md  

## Running the Application

Backend:
```
cd backend
mvn clean install
mvn spring-boot:run
```

Frontend:
```
cd frontend
npm install
npm run dev
```

Docker:
```
docker build -t fullstack-app .
docker run -p 8080:8080 fullstack-app
```

## Features
- Clean separation of frontend and backend
- API-driven communication
- Demonstrates scalable system structure

## Purpose
This project documents practical software engineering skills in addition to data and AI expertise.
