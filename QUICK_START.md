# Quick Reference - Docker Commands

## Start the Application

**Fastest way:**
```bash
./docker-start.sh          # Linux/Mac
docker-start.bat           # Windows
docker-compose up --build  # Manual
```

## Database (Auto-Managed by Docker)
- Host: `localhost:3306`
- User: `hoteluser`
- Password: `hotelpass`
- Database: `hotel_db`
- **No setup needed** - Docker creates it automatically

## Configure Email & Image Uploads (Optional)
```bash
cp .env .env
nano .env  # Edit with your credentials
./docker-start.sh
```

## Access Application
- App: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Health: `http://localhost:8080/actuator/health`

## Common Operations

| Command | What it does |
|---------|--------------|
| `docker-compose up --build` | Start from scratch |
| `docker-compose down` | Stop containers |
| `docker-compose logs app` | View app logs |
| `docker-compose logs mysql` | View database logs |
| `docker-compose ps` | See container status |
| `docker-compose down -v` | Remove everything including data |
| `docker-compose restart` | Restart containers |

## If You Have Make (Linux/Mac)
```bash
make setup   # Create .env
make start   # Start app
make logs    # View logs
make stop    # Stop app
make clean   # Remove everything
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Port 8080 already in use | `docker-compose down` then restart |
| Port 3306 already in use | Change in docker-compose.yml or stop MySQL |
| MySQL won't connect | `docker-compose logs mysql` |
| App won't start | `docker-compose logs app` |
| Need fresh start | `docker-compose down -v` then `docker-compose up --build` |

## When to Use .env

- .env is **optional** for basic testing
- .env is **needed** for:
  - Sending emails (JWT_SECRET, MAIL_*)
  - Image uploads (CLOUDINARY_*)
  - Production deployment (change JWT_SECRET)
