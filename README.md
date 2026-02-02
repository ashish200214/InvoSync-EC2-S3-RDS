

# InvoSync

InvoSync is a full-stack invoicing and quotation management application built with **React (Vite)** on the frontend and **Spring Boot** on the backend.
The project is fully containerized using **Docker**, served through **Nginx**, and secured with **SSL/TLS**.

I built this project to learn how a real application moves from local development to a production-like environment with domain, HTTPS, and proper service communication.

---

## Screenshots
<img width="1920" height="1008" alt="Screenshot 2026-02-03 010019" src="https://github.com/user-attachments/assets/cc76a4c2-72d5-4dc1-ad02-1dd76cddab79" />
<img width="1920" height="1008" alt="Screenshot 2026-02-03 003822" src="https://github.com/user-attachments/assets/5b03eaf5-340c-4e1d-a642-bca4c2b58475" />
<img width="1920" height="1008" alt="Screenshot 2026-02-03 003854" src="https://github.com/user-attachments/assets/c462f8ee-2a0c-48c2-bb72-aad8a5c2caae" />
<img width="1920" height="1008" alt="Screenshot 2026-02-03 010134" src="https://github.com/user-attachments/assets/372e1088-bf95-46fe-a8e5-febb17fb5f86" />

---

## What the Project Does

* Create and manage quotations
* Store records in MySQL
* Expose REST APIs from Spring Boot
* Connect frontend and backend through Nginx
* Run everything using Docker containers
* Serve the application securely on a domain

---

## Project Structure

```
Invosync_Project
│
├── FrontEnd/        → React + Vite application
├── BackEnd/         → Spring Boot REST API
├── docker-compose.yml
├── default.conf     → Nginx configuration
└── README.md
```

Each part runs in its own container and communicates over a Docker network.

---

## Frontend – Docker Setup

The frontend is built with Vite and React.
I used a multi-stage Dockerfile:

### What the Frontend Dockerfile Does

1. Use Node image to install dependencies
2. Build the project using `npm run build`
3. Copy the generated `dist` folder into an Nginx image
4. Serve the files from `/usr/share/nginx/html`

### Environment Variable in Vite

```
VITE_API_URL=https://ashg.online/api
```

This ensures the frontend always calls the backend through the domain and not through IP or port.

---

## Backend – Docker Setup

The backend is a Spring Boot REST API.

### What the Backend Dockerfile Does

1. Use OpenJDK image
2. Copy the built JAR file
3. Expose port **9090**
4. Start the app using `java -jar`

### Environment Variables in Spring Boot

I avoided hardcoding credentials:

```
spring.datasource.url=${JDBC_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
```

These values are passed from Docker Compose.

---

## Docker Compose – How Everything Connects

Docker Compose runs three services:

* **frontend** – Nginx serving React build
* **backend** – Spring Boot API
* **db** – MySQL database

### Service Communication

Inside Docker network:

```
frontend → backend:9090
backend  → db:3306
```

Docker provides internal DNS, so I didn’t need IP addresses.

---

## Nginx Configuration – Using default.conf

Instead of editing the main `nginx.conf`, I used:

```
/etc/nginx/conf.d/default.conf
```

This is the recommended approach in the official Nginx Docker image because:

* Core config remains untouched
* My changes load automatically
* Easier to maintain and update

### What default.conf Handles

1. Serve React frontend
2. Proxy `/api` to backend
3. HTTPS redirection

### Key Logic

```
location / {
    try_files $uri $uri/ /index.html;
}

location /api/ {
    proxy_pass http://backend:9090;
}
```

So:

* Normal routes → React
* `/api` → Spring Boot

This removed all CORS issues.

---

## SSL / TLS Setup

I added SSL certificates to Nginx:

```
ssl_certificate     /etc/ssl/cert.pem;
ssl_certificate_key /etc/ssl/key.pem;
```

HTTP is redirected to HTTPS:

```
listen 80;
return 301 https://$host$request_uri;
```

Now the app works only on secure connections.

---

## Domain Setup

The domain **ashg.online** points to my server.
Users access:

* Frontend → `https://ashg.online`
* API → `https://ashg.online/api`

Internal ports like 9090 are hidden.

---

## How to Run the Project

```
docker-compose up --build
```

Then open:

```
https://ashg.online
```

---

# Troubleshooting – Real Issues I Faced

### 1. Mixed Content Error

**Problem**

```
Blocked loading mixed active content
```

**Reason**

Frontend was HTTPS but API URL used HTTP.

**Fix**

Changed Vite variable to:

```
VITE_API_URL=https://ashg.online/api
```

---

### 2. Double /api Issue

**Problem**

```
/api/api/quotations
```

**Reason**

Base URL already had `/api` and I added it again in requests.

**Fix**

Use:

```
api.get("/quotations")
```

not

```
api.get("/api/quotations")
```

---

### 3. CORS Blocked

**Problem**

Browser blocked cross-origin request.

**Fix**

Used Nginx reverse proxy so frontend and backend share same origin.

---

### 4. “host not found in upstream backend”

**Problem**

Nginx couldn’t resolve backend container.

**Fix**

Made sure both services were in same docker-compose and used:

```
upstream backend_api {
    server backend:9090;
}
```

---

### 5. React Routes Returning 404

**Problem**

Refreshing `/quotations` returned 404.

**Fix**

Changed:

```
try_files $uri $uri/ =404;
```

to

```
try_files $uri $uri/ /index.html;
```

---

### 6. 400 Bad Request on POST

**Reason**

Request body didn’t match Spring DTO.

**Fix**

Sent proper JSON with headers.

---

## What I Learned

This project helped me understand:

* Writing Dockerfiles for frontend and backend
* Docker service networking
* Environment variables in Vite and Spring
* Nginx reverse proxy
* SSL/TLS configuration
* Debugging deployment issues

---


