# Environment Variables Configuration

This document explains all environment variables used in the Hotel Management System.

## Quick Start

### Local Development

```bash
# Copy example file
cp .env.example .env

# Edit with your credentials
nano .env

# Run application
./deploy.sh docker
```

### Production Deployment

```bash
# Copy production template
cp docker.env docker.prod.env

# Edit with production credentials
nano docker.prod.env

# Deploy
docker-compose --env-file docker.prod.env up -d
```

---

## Environment Variables Reference

### JWT Configuration

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `JWT_SECRET` | ✅ Yes | N/A | Secret key for JWT token generation. Must be strong and random. Min 32 characters |

**Example:**
```bash
JWT_SECRET=YourVerySecureRandomKeyWithAtLeast32CharacterMinimum
```

---

### Mailjet Email Service

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `MAILJET_API_KEY` | ✅ Yes | N/A | Mailjet API key from account settings |
| `MAILJET_SECRET_KEY` | ✅ Yes | N/A | Mailjet secret key from account settings |
| `MAILJET_SENDER_EMAIL` | ✅ Yes | N/A | Verified sender email address in Mailjet |
| `MAILJET_SENDER_NAME` | ❌ No | Hotel Management System | Display name for emails |

**Setup:**
1. Sign up at [Mailjet.com](https://www.mailjet.com)
2. Go to Account Settings → API Keys
3. Copy API Key and Secret Key
4. Add a verified sender email
5. Copy values to `.env`

**Example:**
```bash
MAILJET_API_KEY=abc123def456
MAILJET_SECRET_KEY=xyz789uvw012
MAILJET_SENDER_EMAIL=noreply@yourdomain.com
MAILJET_SENDER_NAME=Hotel Management System
```

---

### Cloudinary Image Management

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `CLOUD_NAME` | ✅ Yes | N/A | Cloudinary cloud name from dashboard |
| `API_KEY` | ✅ Yes | N/A | Cloudinary API key |
| `API_SECRET` | ✅ Yes | N/A | Cloudinary API secret |

**Setup:**
1. Sign up at [Cloudinary.com](https://cloudinary.com)
2. Go to Dashboard → Account Details
3. Copy Cloud Name, API Key, and API Secret
4. Add to `.env`

**Example:**
```bash
CLOUD_NAME=your-cloud-name
API_KEY=123456789012345
API_SECRET=abcdefghijklmnopqrstuvwxyz
```

---

### Application Configuration

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `PASSWORD_RESET_BASE_URL` | ❌ No | `http://localhost:5173/auth/reset-password` | Frontend URL for password reset links |

**How It Works:**
- If not set, backend auto-detects frontend domain from HTTP request headers
- Useful for multi-domain/staging environments
- Used as fallback when Origin/Referer headers unavailable

**Examples:**
```bash
# Local development
PASSWORD_RESET_BASE_URL=http://localhost:5173/auth/reset-password

# Production
PASSWORD_RESET_BASE_URL=https://yourdomain.com/auth/reset-password

# Staging
PASSWORD_RESET_BASE_URL=https://staging.yourdomain.com/auth/reset-password
```

---

### Database Configuration

These are automatically configured for H2 embedded database:

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:h2:./hotel_db;...` | H2 database connection URL |
| `SPRING_DATASOURCE_DRIVER_CLASS_NAME` | `org.h2.Driver` | H2 JDBC driver |
| `SPRING_DATASOURCE_USERNAME` | `sa` | Database user (H2 default) |
| `SPRING_DATASOURCE_PASSWORD` | (empty) | Database password (H2 default) |

**Note:** These are preconfigured in docker-compose files. Override only if needed.

---

### JPA/Hibernate Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Schema auto-update: `create`, `create-drop`, `update`, `validate`, `none` |
| `SPRING_JPA_SHOW_SQL` | `false` | Log SQL statements (development only) |
| `SPRING_H2_CONSOLE_ENABLED` | `true` | Enable H2 web console at `/h2-console` |

**Development:**
```bash
SPRING_JPA_SHOW_SQL=true
SPRING_H2_CONSOLE_ENABLED=true
```

**Production:**
```bash
SPRING_JPA_SHOW_SQL=false
SPRING_H2_CONSOLE_ENABLED=false
```

---

## Files That Use Environment Variables

### `.env` (Local Development)
- Used by deploy scripts
- Loaded by docker-compose automatically
- Add to `.gitignore` (never commit secrets)

### `docker.env` (Production Template)
- Template for production deployments
- Must be copied and customized before use
- Used with: `docker-compose --env-file docker.env up`

### `docker-compose.yml` (Main)
- Uses `env_file: - .env` to load variables
- Maps environment variables to Spring Boot properties

### `docker-compose.dev.yml` (Development)
- Same as docker-compose.yml
- Used for local development with Docker

---

## How Environment Variables Flow

```
.env file
   ↓
docker-compose.yml (env_file directive)
   ↓
Docker Container (environment)
   ↓
Spring Boot Application
   ↓
application.properties (uses ${VARIABLE_NAME})
```

### Example:

**In .env:**
```bash
MAILJET_API_KEY=abc123
```

**In docker-compose.yml:**
```yaml
env_file:
  - .env
environment:
  MAILJET_API_KEY: ${MAILJET_API_KEY}
```

**In application.properties:**
```properties
mailjet.api-key=${MAILJET_API_KEY}
```

---

## Validation & Testing

### Verify Environment Variables Are Loaded

```bash
# Check running container
docker exec hotel-management-app env | grep MAILJET

# View logs
docker-compose logs app
```

### Test Email Configuration

```bash
# Make a forgot-password request to test Mailjet
curl -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'
```

### Troubleshooting

**Emails not sending:**
1. Check Mailjet API key: `docker exec hotel-management-app env | grep MAILJET`
2. Verify sender email is approved in Mailjet dashboard
3. Check application logs: `docker-compose logs -f app`

**Wrong password reset URL:**
1. Check PASSWORD_RESET_BASE_URL: `docker exec hotel-management-app env | grep PASSWORD`
2. Backend auto-detects from Origin header if not set
3. Check frontend is making CORS requests properly

**Database errors:**
1. Verify H2 configuration: `docker exec hotel-management-app env | grep SPRING_DATASOURCE`
2. Access H2 console: `http://localhost:8080/h2-console`
3. Clear database: `docker-compose down -v` (removes volumes)

---

## Security Best Practices

✅ **DO:**
- Keep `.env` file in `.gitignore` (don't commit to Git)
- Use strong random secrets (min 32 characters for JWT_SECRET)
- Rotate secrets periodically in production
- Use different credentials for dev and production
- Store production secrets in secure vaults (AWS Secrets Manager, Vault, etc.)

❌ **DON'T:**
- Commit `.env` file to repository
- Share secrets via email or chat
- Use same secrets in dev and production
- Use placeholder values in production
- Log sensitive values

---

## Deployment Examples

### Local Development
```bash
cp .env.example .env
# Edit .env with your credentials
docker-compose up -d
```

### Production on VPS
```bash
cp docker.env production.env
# Edit production.env with production credentials
docker-compose --env-file production.env up -d
```

### Production on AWS
```bash
# Using AWS Secrets Manager (recommended)
# Retrieve secrets and pass as environment variables
docker-compose \
  -e MAILJET_API_KEY=$(aws secretsmanager get-secret-value --secret-id mailjet-key) \
  up -d
```

### Production on Azure
```bash
# Using Azure Key Vault
# Retrieve secrets and pass to docker
docker run \
  -e MAILJET_API_KEY=$(az keyvault secret show --vault-name my-vault --name mailjet-key) \
  hotel-management:latest
```

---

**Last Updated:** 2026-10-03  
**Version:** 1.0.0
