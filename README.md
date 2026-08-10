# ScrabbleSolver Monorepo

This is a monorepo structure with a Java Spring Boot backend and a React frontend.

## Directory Structure

```
ScrabbleSolver/
├── backend/              # Java Spring Boot API
│   ├── src/
│   ├── build.gradle
│   ├── gradlew
│   └── ...
└── frontend/             # React UI
    ├── src/
    ├── package.json
    ├── vite.config.js
    └── ...
```

## Getting Started

### Backend (Spring Boot)
```bash
cd backend
./gradlew bootRun
```
The backend will start on `http://localhost:8080` by default.

### Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```
The frontend will start on `http://localhost:5173` by default.

## Development Setup

1. **Backend Prerequisites:** Java 26+ (configured in `backend/build.gradle`)
2. **Frontend Prerequisites:** Node.js 18+ and npm

## API Communication

The frontend is configured to communicate with the backend. You'll need to set up CORS in Spring Boot if they're running on different ports (which they are during development).

Add to your Spring Boot backend:
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:5173")
                    .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
}
```

## Building for Production

### Backend
```bash
cd backend
./gradlew build
```

### Frontend
```bash
cd frontend
npm run build
```
This creates a `dist/` folder ready for deployment.

## Deployment

To serve both together, copy the frontend build output to Spring Boot's static folder:
```bash
cd frontend
npm run build
cp -r dist/* ../backend/src/main/resources/static/
```

Then deploy just the backend JAR.
