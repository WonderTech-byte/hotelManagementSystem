# Deployment Cheatsheet

Quick reference for common deployment tasks.

## Setup (First Time)

```bash
# Clone repository
git clone https://github.com/WonderTech-byte/hotelManagementSystem.git
cd hotelManagementSystem

# Configure environment
cp .env.example .env
# Edit .env with your Mailjet and Cloudinary credentials

# Verify everything is ready
./deploy.sh check  # or deploy.bat check on Windows
```

---

## Local Development

```bash
# Build and run locally
./deploy.sh local

# Run just build
./deploy.sh build

# Run tests
./deploy.sh test

# Check if app is running
./deploy.sh health
```

**Access:**
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

---

## Docker Deployment

```bash
# Build and run with Docker
./deploy.sh docker

# View logs
docker-compose logs -f app

# Stop containers
docker-compose down

# Restart containers
docker-compose restart

# Remove all data
docker-compose down -v
```

---

## Cloud Deployment

### Azure
```bash
./deploy.sh azure
# Follow prompts for App Service name and Resource Group
```

### AWS
```bash
./deploy.sh aws
# Follow prompts for Elastic Beanstalk details
```

---

## Useful Commands

```bash
# Clean build artifacts
./deploy.sh clean
mvn clean

# View dependencies
mvn dependency:tree

# Check for security vulnerabilities
mvn org.owasp:dependency-check-maven:check

# Format code
mvn spotless:apply

# Generate documentation
mvn site

# Package for deployment
mvn clean package -DskipTests
```

---

## Docker Commands

```bash
# Build image
docker build -t hotel-management:latest .

# Run container
docker run -p 8080:8080 hotel-management:latest

# View running containers
docker ps

# View all containers
docker ps -a

# View container logs
docker logs <container-id>
docker logs -f <container-id>  # Follow logs

# Enter container shell
docker exec -it <container-id> /bin/bash

# Stop container
docker stop <container-id>

# Remove container
docker rm <container-id>

# Remove image
docker rmi hotel-management:latest
```

---

## Docker Compose Commands

```bash
# Start services
docker-compose up

# Start in background
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs
docker-compose logs -f app        # Follow app logs
docker-compose logs app --tail 50 # Last 50 lines

# Restart services
docker-compose restart

# Rebuild images
docker-compose build --no-cache

# Remove data volumes
docker-compose down -v

# View resource usage
docker stats

# Scale service
docker-compose up -d --scale app=3
```

---

## Troubleshooting

```bash
# Check Java installation
java -version

# Check Maven installation
mvn -version

# Check Docker installation
docker --version
docker-compose --version

# Check if port is available
# Linux/Mac
lsof -i :8080

# Windows
netstat -ano | findstr :8080

# Kill process on port 8080
# Linux/Mac
kill -9 <PID>

# Windows
taskkill /PID <PID> /F

# View app logs
tail -f app.log

# Check database
curl http://localhost:8080/h2-console

# Health check
curl http://localhost:8080/actuator/health
```

---

## Environment Variables

```bash
# Required
MAILJET_API_KEY=your-key
MAILJET_SECRET_KEY=your-secret
MAILJET_SENDER_EMAIL=your-email@domain.com

# Optional (with defaults)
MAILJET_SENDER_NAME=Hotel Management System
CLOUD_NAME=your-cloudinary
API_KEY=your-api-key
API_SECRET=your-api-secret
PASSWORD_RESET_BASE_URL=http://localhost:8080/reset-password
```

---

## Git Workflow

```bash
# View status
git status

# See changes
git diff

# Stage changes
git add .

# Commit
git commit -m "Your message"

# Push
git push origin master

# Pull latest
git pull

# View commit history
git log --oneline -10

# Undo last commit (not pushed)
git reset --soft HEAD~1
```

---

## API Endpoints

```bash
# User Management
POST   /api/users/register              # Register user
POST   /api/auth/login                  # Login
POST   /api/auth/refresh                # Refresh token
POST   /api/auth/logout                 # Logout

# Rooms
GET    /api/rooms                       # List all rooms
GET    /api/rooms/{id}                  # Get room details
POST   /api/rooms                       # Create room
PUT    /api/rooms/{id}                  # Update room
DELETE /api/rooms/{id}                  # Delete room

# Bookings
GET    /api/bookings                    # List bookings
GET    /api/bookings/{id}               # Get booking details
POST   /api/bookings                    # Create booking
PUT    /api/bookings/{id}               # Update booking
DELETE /api/bookings/{id}               # Cancel booking

# Health & Metrics
GET    /actuator/health                 # App health
GET    /actuator/metrics                # Metrics
GET    /swagger-ui.html                 # Swagger documentation
```

---

## Database Operations

```bash
# H2 Console
# URL: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:./hotel_db
# User: sa
# Password: (empty)

# Connection string
jdbc:h2:./hotel_db;MODE=MySQL

# Backup database
cp hotel_db.mv.db hotel_db.backup.mv.db

# Restore database
cp hotel_db.backup.mv.db hotel_db.mv.db

# Reset database (delete data)
rm -f hotel_db.mv.db
```

---

## CI/CD Pipeline

```bash
# Trigger build (push to repository)
git push origin master

# View build status
# Go to: GitHub Actions tab

# Check workflow file
cat .github/workflows/build-deploy.yml

# View logs
# Click on workflow run → click on job → view logs
```

---

## Performance Monitoring

```bash
# JVM metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# HTTP metrics
curl http://localhost:8080/actuator/metrics/http.server.requests

# All metrics
curl http://localhost:8080/actuator/metrics

# System info
curl http://localhost:8080/actuator/env
```

---

## Tips

✅ **DO:**
- Always test locally before pushing
- Use `.env` file for sensitive data
- Commit often with meaningful messages
- Keep dependencies up to date
- Monitor logs in production
- Set up automated backups

❌ **DON'T:**
- Commit secrets or API keys
- Push without testing
- Use master for active development (use develop branch)
- Ignore error logs
- Forget to configure environment variables
- Run database operations without backup

---

**Quick Help:**
```bash
./deploy.sh help          # Show all commands
./deploy.sh check         # Verify setup
./deploy.sh health        # Check if app is running
```

---

**For detailed information, see DEPLOYMENT.md**
