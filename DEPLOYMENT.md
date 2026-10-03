# Deployment Guide

Hotel Management System can be deployed in multiple ways. Choose the method that best fits your infrastructure.

## Table of Contents

1. [Local Development](#local-development)
2. [Docker Deployment](#docker-deployment)
3. [Cloud Deployments](#cloud-deployments)
4. [CI/CD Pipeline](#cicd-pipeline)
5. [Troubleshooting](#troubleshooting)

---

## Local Development

### Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher
- Git

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/WonderTech-byte/hotelManagementSystem.git
   cd hotelManagementSystem
   ```

2. **Configure environment**
   ```bash
   cp .env.example .env
   ```
   
   Edit `.env` and add your credentials:
   ```bash
   MAILJET_API_KEY=your-mailjet-api-key
   MAILJET_SECRET_KEY=your-mailjet-secret-key
   MAILJET_SENDER_EMAIL=your-email@yourdomain.com
   MAILJET_SENDER_NAME=Hotel Management System
   CLOUD_NAME=your-cloudinary-name
   API_KEY=your-cloudinary-key
   API_SECRET=your-cloudinary-secret
   ```

3. **Run the deployment script**
   
   **Linux/Mac:**
   ```bash
   ./deploy.sh check      # Verify prerequisites
   ./deploy.sh build      # Build application
   ./deploy.sh local      # Run locally
   ```
   
   **Windows:**
   ```bash
   deploy.bat check
   deploy.bat build
   deploy.bat local
   ```

4. **Access the application**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console

---

## Docker Deployment

### Prerequisites

- Docker 20.10 or higher
- Docker Compose 2.0 or higher
- Git

### Quick Start

```bash
# Linux/Mac
./deploy.sh docker

# Windows
deploy.bat docker
```

### Manual Docker Deployment

```bash
# Build Docker image
docker build -t hotel-management:latest .

# Start containers
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop containers
docker-compose down
```

### Docker Compose Configuration

The `docker-compose.yml` includes:
- **app** service: Spring Boot application on port 8080
- **volume**: Local database storage
- **network**: Isolated hotel-network for service communication
- **health checks**: Automatic container restart on failure

### Environment Variables in Docker

Environment variables are passed via `.env` file. They're automatically loaded by docker-compose.

---

## Cloud Deployments

### Azure App Service

**Prerequisites:**
- Azure CLI installed
- Azure subscription
- App Service created

**Deployment:**

```bash
./deploy.sh azure
```

You'll be prompted for:
- Azure App Service name
- Azure Resource Group name

**Manual Deployment:**

```bash
az webapp up --name my-app --resource-group my-rg --runtime "JAVA|17-java17" --sku B1
```

**Post-Deployment Configuration:**

1. Set application settings:
   ```bash
   az webapp config appsettings set \
     --resource-group my-rg \
     --name my-app \
     --settings MAILJET_API_KEY="your-key" \
                MAILJET_SECRET_KEY="your-secret" \
                MAILJET_SENDER_EMAIL="your-email"
   ```

2. Access your app: `https://my-app.azurewebsites.net`

---

### AWS Elastic Beanstalk

**Prerequisites:**
- AWS CLI installed
- AWS credentials configured
- Elastic Beanstalk CLI (eb)
- Application and environment created

**Deployment:**

```bash
./deploy.sh aws
```

You'll be prompted for:
- Application name
- Environment name

**Manual Deployment:**

```bash
# Initialize Elastic Beanstalk (first time only)
eb init -p java-17 hotel-management

# Create environment (first time only)
eb create production

# Deploy
eb deploy
```

**Post-Deployment Configuration:**

1. Set environment variables:
   ```bash
   eb setenv MAILJET_API_KEY=your-key \
             MAILJET_SECRET_KEY=your-secret \
             MAILJET_SENDER_EMAIL=your-email
   ```

2. View environment info:
   ```bash
   eb status
   eb open
   ```

---

## CI/CD Pipeline

The GitHub Actions workflow automatically:

1. **Builds** the application on every push
2. **Runs tests** to ensure quality
3. **Builds Docker image** on successful build
4. **Deploys to staging** on develop branch
5. **Deploys to production** on master branch

### GitHub Secrets Configuration

Add these secrets to your GitHub repository:

```
DEPLOY_KEY              # SSH private key for deployment
DEPLOY_HOST_STAGING     # Staging server hostname
DEPLOY_HOST_PROD        # Production server hostname
DEPLOY_USER             # Deployment user
```

### Workflow File Location

`.github/workflows/build-deploy.yml`

### Viewing Workflow Status

1. Go to Actions tab on GitHub
2. Select "Build and Deploy" workflow
3. View logs for each job

---

## Database

The application uses **H2 Database** for local development.

### H2 Console

Access the embedded database console:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:./hotel_db`
- User: `sa`
- Password: (leave empty)

### Database Files

- **Local**: `./hotel_db.mv.db` (auto-created)
- **Docker**: Volume `mysql_data` in docker-compose.yml

### Schema Management

Hibernate automatically creates/updates tables via:
```properties
spring.jpa.hibernate.ddl-auto=update
```

---

## Email Configuration (Mailjet)

### Getting Mailjet Credentials

1. Sign up at [Mailjet.com](https://www.mailjet.com)
2. Go to Account Settings → API Keys
3. Copy API Key and Secret Key
4. Create a Sender Email in Sender List
5. Add credentials to `.env` file

### Testing Email

```bash
curl -X POST http://localhost:8080/api/emails/test \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","subject":"Test","message":"Hello"}'
```

---

## Monitoring & Logging

### Local Logs
```bash
tail -f app.log
```

### Docker Logs
```bash
docker-compose logs -f app          # Follow app logs
docker-compose logs app --tail 100  # Last 100 lines
```

### Application Endpoints

- **Health Check**: GET `/actuator/health`
- **Metrics**: GET `/actuator/metrics`
- **Swagger**: http://localhost:8080/swagger-ui.html

---

## Troubleshooting

### Build Failures

```bash
# Clean and rebuild
mvn clean install -DskipTests

# Check Java version
java -version  # Should be 17+

# Check Maven version
mvn -version   # Should be 3.6.0+
```

### Docker Issues

```bash
# See all containers
docker ps -a

# Inspect container logs
docker logs hotel-management-app

# Rebuild image
docker build --no-cache -t hotel-management:latest .

# Remove dangling images
docker image prune
```

### Connection Errors

```bash
# Check if port 8080 is in use
lsof -i :8080  # Linux/Mac
netstat -ano | findstr :8080  # Windows

# Use different port
export SERVER_PORT=8090
./deploy.sh local
```

### Email Not Sending

1. Verify Mailjet credentials in `.env`
2. Check logs for Mailjet API errors
3. Verify sender email is approved in Mailjet
4. Check recipient email is valid

### Database Issues

```bash
# Reset database
rm -f hotel_db.mv.db  # Stop app first

# Check database status
curl http://localhost:8080/h2-console
```

---

## Rollback

### Docker Rollback
```bash
# View image history
docker image history hotel-management:latest

# Run specific version
docker run -p 8080:8080 hotel-management:v1.0.0
```

### Application Rollback
```bash
# Revert to previous commit
git revert HEAD
git push

# CI/CD will automatically redeploy
```

---

## Performance Tuning

### JVM Settings

Set these environment variables for production:

```bash
export JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"
```

### Database Connection Pool

Adjust in `application.properties`:
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

---

## Security Best Practices

1. **Never commit secrets** - Use `.env` file (in .gitignore)
2. **Use HTTPS** - Enable SSL/TLS on production
3. **Firewall** - Restrict database and admin ports
4. **Regular backups** - Schedule automated database backups
5. **Log rotation** - Set up log rotation for large log files
6. **Updates** - Keep dependencies updated via `mvn dependency:update-check`

---

## Support

For issues or questions:
1. Check logs: `docker-compose logs app`
2. Review Swagger documentation: http://localhost:8080/swagger-ui.html
3. Check GitHub Issues: https://github.com/WonderTech-byte/hotelManagementSystem/issues

---

**Last Updated**: 2026-10-03
**Version**: 1.0.0
