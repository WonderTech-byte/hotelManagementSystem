@echo off
REM Hotel Management System - Deployment Script (Windows)
REM This script automates building and deploying the application

setlocal enabledelayedexpansion
cd /d "%~dp0"

set PROJECT_NAME=Hotel Management System
set VERSION=1.0.0

REM Colors
set GREEN=[92m
set RED=[91m
set YELLOW=[93m
set BLUE=[94m
set RESET=[0m

goto :main

:print_header
    echo.
    echo %BLUE%================================%RESET%
    echo %BLUE%%~1%RESET%
    echo %BLUE%================================%RESET%
    goto :eof

:print_success
    echo %GREEN%[OK] %~1%RESET%
    goto :eof

:print_error
    echo %RED%[ERROR] %~1%RESET%
    goto :eof

:print_warning
    echo %YELLOW%[WARNING] %~1%RESET%
    goto :eof

:print_info
    echo %BLUE%[INFO] %~1%RESET%
    goto :eof

:check_prerequisites
    call :print_header "Checking Prerequisites"
    
    where java >nul 2>nul
    if errorlevel 1 (
        call :print_error "Java not found. Install Java 17 or higher"
        exit /b 1
    )
    for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| findstr "version"') do set JAVA_VER=%%i
    call :print_success "Java found: %JAVA_VER%"
    
    where mvn >nul 2>nul
    if errorlevel 1 (
        call :print_error "Maven not found. Install Maven 3.6.0 or higher"
        exit /b 1
    )
    call :print_success "Maven found"
    
    where docker >nul 2>nul
    if errorlevel 1 (
        call :print_warning "Docker not found. Docker deployment unavailable"
    ) else (
        call :print_success "Docker found"
    )
    goto :eof

:load_env
    if not exist ".env" (
        call :print_error ".env file not found"
        call :print_info "Copy .env.example to .env and update with your credentials"
        exit /b 1
    )
    
    for /f "usebackq delims== tokens=1,*" %%a in (".env") do (
        if not "%%a"=="" if not "%%a:~0,1%"=="#" (
            set "%%a=%%b"
        )
    )
    call :print_success "Environment variables loaded"
    goto :eof

:validate_env
    call :print_header "Validating Configuration"
    
    if "!MAILJET_API_KEY!"=="" (
        call :print_error "MAILJET_API_KEY not set"
        exit /b 1
    )
    call :print_success "Mailjet API Key configured"
    
    if "!MAILJET_SECRET_KEY!"=="" (
        call :print_error "MAILJET_SECRET_KEY not set"
        exit /b 1
    )
    call :print_success "Mailjet Secret Key configured"
    
    if "!MAILJET_SENDER_EMAIL!"=="" (
        call :print_warning "MAILJET_SENDER_EMAIL not set, using default"
    ) else (
        call :print_success "Mailjet Sender Email: !MAILJET_SENDER_EMAIL!"
    )
    goto :eof

:build
    call :print_header "Building Application"
    
    call :print_info "Running Maven build..."
    call mvn clean install -DskipTests -q
    if errorlevel 1 (
        call :print_error "Build failed"
        exit /b 1
    )
    call :print_success "Build completed successfully"
    goto :eof

:run_tests
    call :print_header "Running Tests"
    
    call mvn test -q
    if errorlevel 1 (
        call :print_warning "Some tests failed"
    ) else (
        call :print_success "All tests passed"
    )
    goto :eof

:deploy_local
    call :print_header "Deploying Locally (JAR)"
    
    for /f %%i in ('dir /b target\hotel-management-system-*.jar 2^>nul') do set JAR_FILE=%%i
    
    if "!JAR_FILE!"=="" (
        call :print_error "JAR file not found. Run build first"
        exit /b 1
    )
    
    call :print_info "Starting application on http://localhost:8080"
    call :print_info "Press Ctrl+C to stop"
    
    java -jar "target\!JAR_FILE!"
    goto :eof

:deploy_docker
    call :print_header "Deploying with Docker"
    
    where docker >nul 2>nul
    if errorlevel 1 (
        call :print_error "Docker not installed"
        exit /b 1
    )
    
    call :print_info "Building Docker image..."
    call docker build -t hotel-management:latest .
    call :print_success "Docker image built"
    
    call :print_info "Stopping existing containers..."
    call docker-compose down 2>nul
    
    call :print_info "Starting containers..."
    call docker-compose up -d
    
    timeout /t 3 /nobreak
    
    call :print_success "Application running on http://localhost:8080"
    call :print_info "View logs: docker-compose logs -f app"
    goto :eof

:health_check
    call :print_header "Health Check"
    
    set URL=http://localhost:8080/swagger-ui.html
    set MAX_RETRIES=30
    set RETRY=0
    
    call :print_info "Checking application health..."
    
    :health_loop
    if %RETRY% geq %MAX_RETRIES% (
        call :print_error "Application is not responding after %MAX_RETRIES% attempts"
        exit /b 1
    )
    
    curl -s "%URL%" >nul 2>&1
    if errorlevel 1 (
        set /a RETRY=%RETRY%+1
        echo Attempt %RETRY%/%MAX_RETRIES%...
        timeout /t 1 /nobreak >nul
        goto :health_loop
    )
    
    call :print_success "Application is healthy"
    call :print_info "Swagger UI: %URL%"
    goto :eof

:usage
    echo.
    echo %BLUE%%PROJECT_NAME% - Deployment Script%RESET%
    echo.
    echo Usage: deploy.bat [COMMAND]
    echo.
    echo Commands:
    echo   check      Check prerequisites
    echo   build      Build the application
    echo   test       Run tests
    echo   local      Deploy locally (runs JAR)
    echo   docker     Deploy using Docker Compose
    echo   health     Check application health
    echo   clean      Clean build artifacts
    echo   help       Show this message
    echo.
    echo Examples:
    echo   deploy.bat check                 # Check prerequisites
    echo   deploy.bat build                 # Build application
    echo   deploy.bat docker                # Deploy with Docker
    echo   deploy.bat health                # Check if app is running
    echo.
    goto :eof

:main
    set COMMAND=%1
    if "!COMMAND!"=="" set COMMAND=help
    
    if "!COMMAND!"=="check" (
        call :check_prerequisites
    ) else if "!COMMAND!"=="build" (
        call :check_prerequisites
        call :load_env
        call :build
    ) else if "!COMMAND!"=="test" (
        call :check_prerequisites
        call :load_env
        call :run_tests
    ) else if "!COMMAND!"=="local" (
        call :check_prerequisites
        call :load_env
        call :validate_env
        call :build
        call :deploy_local
    ) else if "!COMMAND!"=="docker" (
        call :check_prerequisites
        call :load_env
        call :validate_env
        call :build
        call :deploy_docker
        call :health_check
    ) else if "!COMMAND!"=="health" (
        call :health_check
    ) else if "!COMMAND!"=="clean" (
        call :print_header "Cleaning Build Artifacts"
        call mvn clean
        call :print_success "Cleaned"
    ) else if "!COMMAND!"=="help" (
        call :usage
    ) else (
        call :print_error "Unknown command: !COMMAND!"
        call :usage
        exit /b 1
    )
    
    endlocal
