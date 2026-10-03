@echo off
REM Hotel Management System - Docker Startup (Windows)

setlocal enabledelayedexpansion

echo.
echo ================================================
echo Docker Hotel Management System - Startup
echo ================================================
echo.

REM Check if .env file exists
if not exist .env (
    echo [INFO] .env file not found - creating from .env.example
    copy .env.example .env
    echo [OK] Created .env
    echo.
    echo [WARNING] Edit .env with your credentials:
    echo   - JWT_SECRET (keep as-is or change for production^)
    echo   - MAIL_USERNAME ^& MAIL_PASSWORD (Gmail^)
    echo   - CLOUD_NAME, API_KEY, API_SECRET (Cloudinary^)
    echo.
)

echo [OK] .env file ready
echo.
echo Starting Docker containers...
echo.

docker-compose up --build %*

endlocal

