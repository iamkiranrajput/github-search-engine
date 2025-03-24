# GitHub API Service

The **GitHub API Service** is a **Spring Boot-based** application that interacts with the **GitHub REST API** to fetch repositories, user details, and other GitHub-related information. It provides a structured way to integrate GitHub functionalities, ensuring pagination, sorting, logging, and exception handling.

---

## 🚀 Quick Links

- **Postman API Collection:** [Postman Documentation](#) *(Add actual link)*
- **Docker Hub Image:**
  - **GitHub API Service**: `docker pull yourdockerhub/github-api:latest`

---

## 🛠️ Features

✅ **Fetch GitHub Repositories**: Retrieve public repositories for a user or organization.  
✅ **Get User Information**: Fetch GitHub user details using their username.  
✅ **Pagination & Sorting**: Supports pagination and sorting for large data sets.  
✅ **Logging & Exception Handling**: Uses **Slf4j** for structured logging and **GlobalExceptionHandler** for error management.  
✅ **Swagger API Documentation**: Provides an interactive API documentation via Swagger UI.  

---

## 📌 Technology Stack

- **Java 11+** → Core backend logic  
- **Spring Boot** → Framework for REST API  
- **Spring WebClient** → For consuming GitHub API  
- **JPA/Hibernate** → ORM for data persistence (if needed)  
- **PostgreSQL / H2** → Database options  
- **Lombok** → Reduce boilerplate code  
- **Maven** → Dependency management  

---

## 🏗️ Installation Guide

### 🔹 Prerequisites

- **Java 11+**
- **Maven 3.6+**
- **Git**
- **A Spring-compatible IDE** (IntelliJ, Eclipse, etc.)

### 🔹 Steps to Setup

1️⃣ **Clone the Repository**  
```
git clone https://github.com/iamkiranrajput/github-api-service.git
```

2️⃣ Navigate to the Project Directory
3️⃣ Install Dependencies & Build the Project
```
mvn clean install
```

4️⃣ Run the Application
```
mvn spring-boot:run
```

📂 Project Structure
```
src/
 ├── main/
 │   ├── java/
 │   │   └── com/githubapi/
 │   │       ├── controller/        # REST controllers
 │   │       ├── dto/               # Data Transfer Objects (DTOs)
 │   │       ├── exception/         # Custom exception handlers
 │   │       ├── service/           # Business logic layer
 │   │       ├── util/              # Utility classes
 │   │       └── client/            # GitHub API client integration
 │   └── resources/
 │       ├── application.properties # Application configuration
 └── test/
     └── java/                      # Unit and integration tests
```

⚙️ Configuration
The application requires a GitHub Personal Access Token (PAT) for authentication when interacting with the GitHub API.

🔹 Sample application.properties
properties
Copy
Edit
github.api.base-url=https://api.github.com
github.api.token=your_github_access_token
server.port=8080
📖 Usage
🔹 Fetch Public Repositories
Endpoint: GET /api/github/repos/{username}
Example Response:

json
```
[
  {
    "name": "Spring-Boot-App",
    "url": "https://github.com/iamkiranrajput/Spring-Boot-App",
    "stars": 45,
    "forks": 10
  }
]
```
🔹 Fetch GitHub User Details

Endpoint: GET /api/github/user/{username}
Example Response:

```
{
  "username": "iamkiranrajput",
  "profileUrl": "https://github.com/iamkiranrajput",
  "publicRepos": 15,
  "followers": 200
}
```

📜 Logging & Exception Handling
Logging: Uses Slf4j for structured logging of API calls and errors.
Global Exception Handling: Handles cases like RateLimitExceededException, DatabaseAccessException, etc.

🔹 Custom Exceptions
Exception	Description
RateLimitExceededException	Thrown when GitHub API rate limit is reached.
ApiException	Generic exception handler for API errors.

🗂 API Endpoints
HTTP Method	Endpoint	Description

GET	/api/github/repos/{username}	Fetch all public repositories of a GitHub user
GET	/api/github/user/{username}	Fetch GitHub user profile details (on working)
GET	/api/github/repos/{username}?page=0&size=5&sort=stars,desc	Fetch repositories with pagination & sorting
🛠️ Testing the API
To test the API, use Postman or cURL.

Swagger UI: Available at http://localhost:8080/swagger-ui.html

⭐ If you find this project useful, give it a star on GitHub! ⭐
