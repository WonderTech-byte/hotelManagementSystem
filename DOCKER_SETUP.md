# Docker Setup for Hotel Management System

## Quick Start

### 1. Start with defaults (database managed by Docker)
```bash
# Linux/Mac
./docker-start.sh

# Windows
docker-start.bat

# Or direct
docker-compose up --build
```

The database (MySQL) is automatically created by Docker with defaults.

### 2. Configure external services (.env file)
For email and image uploads to work, create/edit `.env`:

```bash
cp .env .env
```

Then edit with your credentials:
```env
JWT_SECRET=your-super-secret-jwt-key
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
CLOUD_NAME=your-cloudinary-cloud-name
API_KEY=your-cloudinary-api-key
API_SECRET=your-cloudinary-api-secret
```

**Database credentials are auto-generated** (no .env needed for MySQL):
- User: `hoteluser`
- Password: `hotelpass`
- Database: `hotel_db`

### 3. Access the app
```
App: http://localhost:8080
API Docs: http://localhost:8080/swagger-ui.html
Database: localhost:3306
```

## Environment Variables

| Variable | Default | Required for | Notes |
|----------|---------|-------------|-------|
| `MYSQL_ROOT_PASSWORD` | `samuel` | Docker | MySQL auto-managed |
| `MYSQL_DATABASE` | `hotel_db` | Docker | Docker auto-managed |
| `MYSQL_USER` | `hoteluser` | Docker | Docker auto-managed |
| `MYSQL_PASSWORD` | `hotelpass` | Docker | Docker auto-managed |
| `JWT_SECRET` | None - From .env | Auth | Must be set for login |
| `MAIL_USERNAME` | None - From .env | Email | Optional - password reset won't work |
| `MAIL_PASSWORD` | None - From .env | Email | Optional - password reset won't work |
| `CLOUD_NAME` | None - From .env | Uploads | Optional - image upload won't work |
| `API_KEY` | None - From .env | Uploads | Optional - image upload won't work |
| `API_SECRET` | None - From .env | Uploads | Optional - image upload won't work |

## Workflows

### Start & test basic functionality
```bash
./docker-start.sh
# Or just: docker-compose up --build
```
✅ App runs | ❌ Email disabled | ❌ Image upload disabled

### Full setup with email & images
```bash
cp .env .env
nano .env  # Edit with your credentials
./docker-start.sh
```
✅ App runs | ✅ Email works | ✅ Image upload works

## Troubleshooting

### "variable not set" error
Docker needs the `.env` file for external services. Create it:
```bash
cp .env .env
docker-compose up --build
```

### MySQL connection refused
```bash
docker-compose logs mysql
docker-compose restart mysql
```

### App won't start
```bash
docker-compose logs app
```

### Recreate everything from scratch
```bash
docker-compose down -v
docker-compose up --build
```

## Architecture

```
┌──────────────────────────────────────┐
│  Spring Boot App                     │
│  Port: 8080                          │
│  Needs: JWT_SECRET                   │
│  Optional: MAIL_*, CLOUDINARY_*      │
└────────────────┬─────────────────────┘
                 │ JDBC
                 ▼
┌──────────────────────────────────────┐
│  MySQL 8.0 (Docker Managed)          │
│  Port: 3306                          │
│  Auto-created with defaults          │
│  Data persisted in volume            │
└──────────────────────────────────────┘
```

## Files Included

- `Dockerfile` - Multi-stage build
- `docker-compose.yml` - App + MySQL with defaults
- `docker-start.sh` - Linux/Mac launcher
- `docker-start.bat` - Windows launcher
- `.env.example` - Template for external services
- `Makefile` - Helper commands (Linux/Mac)
- `DOCKER_SETUP.md` - This guide

## Fixed Issues

✅ Spring Security 6+ Compatibility
✅ Multi-stage Docker Build
✅ Health Checks (MySQL + App)
✅ Database defaults (Docker auto-managed)
✅ External services configurable via .env
✅ .env excluded from git

## Security Notes

- Database credentials are defaults (for dev) - change in production
- .env contains secrets - never commit
- .env.example is safe to commit
- Change JWT_SECRET in production
- Use app-specific Gmail password, not your actual password
