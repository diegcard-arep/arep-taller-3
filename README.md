# 🌐 WebApp Framework - HTTP Server with Web Framework

A complete HTTP server implemented from scratch in Java, with an integrated web framework that supports REST routes, static files, and multiple MIME types. The project includes both a low-level HTTP server and a high-level web framework for rapid application development.

## 🎯 Key Features

### Core HTTP Server
- ✅ Full-featured HTTP server with no external dependencies
- ✅ Support for static files (HTML, CSS, JS, images, fonts)
- ✅ In-memory file caching for improved performance
- ✅ Automatic MIME type detection
- ✅ Integrated error handling and logging

### Web Framework
- ✅ Spark/Express-like web framework for Java
- ✅ Route definition with lambda functions
- ✅ Automatic query parameter handling
- ✅ Customizable JSON responses and content types
- ✅ Simplified static file configuration

### Containerization and Deployment
- ✅ Full Docker support
- ✅ Optimized multi-stage Dockerfile
- ✅ Docker Compose for development
- ✅ Standalone executable JAR

## 🏗️ System Architecture

![Architecture Diagram](img/diagram.png)

### Main Components

| Component | File | Description |
|------------|----------|-------------|
| **HttpServer** | `HttpServer.java` | Base HTTP server that handles connections, routing, and MIME types |
| **WebApp Framework** | `WebApp.java` | High-level web framework for defining routes and services |
| **Request/Response** | `Request.java`, `Response.java` | HTTP encapsulation objects |
| **Demo Application** | `RestApiDemo.java` | Framework demo application |
| **Configuration** | `ServerConfig.java` | Centralized server configuration |

## Project Structure

```
arep-taller-2/
├── src/
│ ├── main/
│ │ ├── java/com/escuelaing/arep/
│ │ │ ├── HttpServer.java # Main HTTP Server
│ │ │ ├── WebApp.java # Web Framework
│ │ │ ├── Request.java # HTTP Request Object
│ │ │ ├── Response.java # HTTP Response Object
│ │ │ ├── RouteHandler.java # Interface for handlers
│ │ │ ├── RestApiDemo.java # Demo application
│ │ │ └── config/
│ │ │ └── ServerConfig.java # Server configuration
│ │ └── resources/
│ │ └── static/ # Static web files
│ │ ├── index.html # Home page
│ │ ├── styles.css # CSS styles
│ │ ├── app.js # Client-side logic
│ │ └── logo.svg # App logo
│ └── test/
│ └── java/com/escuelaing/arep/
│ └── HttpServerTest.java # Unit tests
├── target/ # Maven output
├── Dockerfile # Docker configuration
├── docker-compose.yml # Compose for development
├── pom.xml # Maven configuration
├── README.md # This file
└── LICENSE.md # MIT License
```

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Maven 3.6+
- Docker (optional for containerization)

### Installation Methods and Execution

#### Option 1: Direct Execution with Maven

```bash
# Clone and compile
git clone https://github.com/diegcard-arep/arep-taller-2.git
cd arep-taller-2
mvn clean compile
```

```bash
# Run server (recommended)
mvn exec:java -Dexec.mainClass="com.escuelaing.arep.RestApiDemo"

# Alternatives
java -cp target/classes com.escuelaing.arep.RestApiDemo
java -cp target/urlobject-1.0-SNAPSHOT.jar com.escuelaing.arep.RestApiDemo
```

#### Option 2: Using Docker

```bash
# Build and run with Docker Compose
docker-compose up --build

# Or manually
docker build -t arep-taller-2 .
docker run -p 35000:35000 arep-taller-2
```

#### Option 3: Executable JAR

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/urlobject-1.0-SNAPSHOT.jar
```

### Accessing the Application

```text
http://localhost:35000
```

### Stopping the Server

- Press `Ctrl+C` in the terminal
- The server will log shutdown information and shut down gracefully

## 🔧 Using the Framework

### Basic Example

```java
import static com.escuelaing.arep.WebApp.*;

public class MyApplication { 
public static void main(String[] args) { 
// Configure static files 
staticfiles("/static"); 

// Define REST routes 
get("/hello", (req, resp) -> { 
String name = req.getValues("name"); 
return "Hello " + (name.isEmpty() ? "World" : name) + "!"; 
}); 

get("/api/data", (req, resp) -> { 
resp.type("application/json"); 
return "{\"message\": \"Hello from API\"}"; 
}); 

// Start server 
start(); 
}
}
```
### Framework Features

- **Simple Routes**: Define routes with lambda functions
- **Query Parameters**: Easily access them with `req.getValues("param")`
- **Content Types**: Configure responses with `resp.type("mime-type")`
- **Static Files**: Configure them with `staticfiles("/path")`

## 🌐 API and Endpoints

### Static Files

Support for multiple file types with automatic MIME detection:

| Type | Extensions | Content-Type |
|------|-------------|--------------|
| HTML | .html, .htm | text/html |
| CSS | .css | text/css |
| JavaScript | .js | application/javascript |
| Images | .png, .jpg, .gif, .svg, .ico, .webp | image/* |
| Fonts | .woff, .woff2, .ttf, .eot | font/* |
| Documents | .pdf, .txt, .zip | application/* |

### Demo REST Endpoints

| Endpoint | Method | Description | Example |
|----------|--------|-------------|---------|
| `/App/hello` | GET | Custom greeting | `/App/hello?name=Diego` |
| `/App/pi` | GET | PI value | Numeric response |
| `/App/greet` | GET | Multi-language greeting | `/App/greet?name=Maria&lang=es` |
| `/App/info` | GET | Framework information | JSON response |

### Server Configuration

| Parameter | Value | Description |
|-----------|-------|-------------|
| Port | 35000 | Server listening port |
| Static Directory | `/static` | Root directory for static files |
| Connection Type | Sequential | Handles one request at a time |
| Cache | In-memory | Stores files in memory for better performance |

### Demo Web Interface

The application includes a complete interface with:

- Interactive forms for testing APIs
- Asynchronous communication with JavaScript
- Responsive interface with modern CSS
- Loading state and error handling

## 🧪 Tests

### Run All Tests

```bash
mvn test
```

### Expected Results

```text
[INFO] -------------------------------------------------------
[INFO] T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.escuelaing.arep.HttpServerTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] Results:
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Test Coverage

Tests include:

- HTTP server unit tests
- MIME type validation
- REST endpoint testing
- Error handling and edge cases
- Query parameter validation
- JSON responses and status codes

## 📦 Technologies and Dependencies

- **Java 21**: Base language with modern features
- **Maven 3.9+**: Dependency management and building
- **JUnit 5**: Testing framework
- **Vanilla JavaScript**: Framework-free frontend
- **CSS3**: Modern responsive styles
- **Docker**: Containerization and deployment

## 🐳 Deployment with Docker

### Building the Image

```bash
# Building with Docker Compose (recommended)
docker-compose up --build

# Manual build
docker build -t arep-taller-2 .
```

### Dockerfile Features

- **Multi-stage build**: Image size optimization
- **Alpine Base**: Lightweight production image
- **Standalone JAR**: No external dependencies
- **Configured port**: Automatically exposes port 35000

### Useful Docker Commands

```bash
# Run in background
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Rebuild without cache
docker-compose build --no-cache
```

## 📊 Metrics and Performance

### Performance Features

- **In-Memory Cache**: Static files cached for fast response
- **Sequential Connections**: One request at a time (ideal for development/demo)
- **Optimized Logging**: Logging system configured for debugging
- **JAR Size**: ~10MB (no external dependencies)

### Known Limitations

- Sequential connection handling (non-concurrent)
- Unlimited memory cache
- No data persistence
- No authentication/authorization

## 🔍 Technical Details

### Request Flow

1. **Receive**: `HttpServer` receives the connection
2. **Parsing**: Extracts method, route, and HTTP headers
3. **Routing**: `WebApp` verifies registered routes
4. **Processing**: Executes handler or serve a static file
5. **Response**: Sends an HTTP response with appropriate headers

### MIME Type Handling

```java
// Automatically supported MIME types
.html/.htm → text/html
.css → text/css
.js → application/javascript
.png → image/png
.jpg/.jpeg → image/jpeg
.svg → image/svg+xml
// ... and more
```

### Advanced Configuration

```java
// Port Configuration
ServerConfig.setPort(8080);

// Static File Configuration
staticfiles("/public");

// Routes with multiple parameters
get("/user", (req, resp) -> { 
String name = req.getValues("name"); 
String age = req.getValues("age"); 
return "User: " + name + ", Age: " + age;
});
```

## � Development and Contribution

### Class Structure

```text
HttpServer (Core)
├── TCP Connection Handling
├── HTTP Parsing
├── Static File Serving
└── Framework Integration

WebApp (Framework)
├── Route Registration
├── Handler Management
├── Static Configuration
└── Server Lifecycle

Request/Response
├── HTTP Encapsulation
├── Parameter Parsing
├── Header Configuration
└── Content Types
```

## 🚨 Troubleshooting

### Common Problems

#### Port in use

```bash
# Check processes on port 35000
lsof -i :35000
kill -9 <PID>
```

#### Compilation Errors

```bash
# Clean and recompile
mvn clean install
```

#### Docker Problems

```bash
# Clean containers
docker system prune -a
```

### Debug Logs

The server includes detailed debugging logs:

```text
INFO: HTTP Server started on port 35000
INFO: Serving files from: /path/to/static
INFO: Request: GET /App/hello?name=Diego
INFO: Framework route handled: /App/hello
```

## 👨‍💻 Author

**Diego Cardenas** - [diegcard](https://github.com/diegcard)

## 📄 License

This project is licensed under the MIT License - see [LICENSE.md](LICENSE.md) for details.

## 🎓 Academic Context

**Julio Garavito Colombian School of Engineering**
**Enterprise Architectures (AREP) - Workshop 2**

### Learning Objectives

- Implementing HTTP servers from scratch
- Developing minimalist web frameworks
- Handling network protocols in Java
- Containerization with Docker
- Distributed architectures and microservices