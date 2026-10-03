#!/bin/bash

################################################################################
# Hotel Management System - Deployment Script
# This script automates building and deploying the application
# Supports: local, docker, and cloud deployments
################################################################################

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="Hotel Management System"
VERSION="1.0.0"

# Functions
print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

# Check prerequisites
check_prerequisites() {
    print_header "Checking Prerequisites"
    
    local missing=0
    
    # Check Java
    if ! command -v java &> /dev/null; then
        print_error "Java not found. Install Java 17 or higher"
        missing=1
    else
        print_success "Java $(java -version 2>&1 | head -n 1)"
    fi
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        print_error "Maven not found. Install Maven 3.6.0 or higher"
        missing=1
    else
        print_success "Maven $(mvn -v 2>&1 | head -n 1)"
    fi
    
    # Check Docker (optional)
    if command -v docker &> /dev/null; then
        print_success "Docker $(docker --version)"
    else
        print_warning "Docker not found. Docker deployment will not be available"
    fi
    
    if [ $missing -eq 1 ]; then
        print_error "Missing required prerequisites"
        exit 1
    fi
}

# Load environment variables
load_env() {
    if [ ! -f ".env" ]; then
        print_error ".env file not found"
        print_info "Copy .env.example to .env and update with your credentials"
        exit 1
    fi
    
    set -a
    source .env
    set +a
    print_success "Environment variables loaded"
}

# Validate environment
validate_env() {
    print_header "Validating Configuration"
    
    local missing=0
    
    if [ -z "$MAILJET_API_KEY" ]; then
        print_error "MAILJET_API_KEY not set"
        missing=1
    else
        print_success "Mailjet API Key configured"
    fi
    
    if [ -z "$MAILJET_SECRET_KEY" ]; then
        print_error "MAILJET_SECRET_KEY not set"
        missing=1
    else
        print_success "Mailjet Secret Key configured"
    fi
    
    if [ -z "$MAILJET_SENDER_EMAIL" ]; then
        print_warning "MAILJET_SENDER_EMAIL not set, using default"
    else
        print_success "Mailjet Sender Email: $MAILJET_SENDER_EMAIL"
    fi
    
    if [ $missing -eq 1 ]; then
        print_error "Missing required environment variables"
        exit 1
    fi
}

# Build the application
build() {
    print_header "Building Application"
    
    cd "$SCRIPT_DIR"
    
    print_info "Running Maven build..."
    if mvn clean install -DskipTests -q; then
        print_success "Build completed successfully"
    else
        print_error "Build failed"
        exit 1
    fi
}

# Run tests
run_tests() {
    print_header "Running Tests"
    
    cd "$SCRIPT_DIR"
    
    if mvn test -q; then
        print_success "All tests passed"
    else
        print_warning "Some tests failed"
    fi
}

# Deploy locally (JAR)
deploy_local() {
    print_header "Deploying Locally (JAR)"
    
    cd "$SCRIPT_DIR"
    
    local jar_file="target/hotel-management-system-*.jar"
    
    if [ ! -f $jar_file ]; then
        print_error "JAR file not found. Run build first"
        exit 1
    fi
    
    print_info "Starting application on http://localhost:8080"
    print_info "Press Ctrl+C to stop"
    
    java -jar $jar_file
}

# Deploy with Docker
deploy_docker() {
    print_header "Deploying with Docker"
    
    cd "$SCRIPT_DIR"
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker not installed"
        exit 1
    fi
    
    print_info "Building Docker image..."
    docker build -t hotel-management:latest .
    print_success "Docker image built"
    
    print_info "Stopping existing containers..."
    docker-compose down 2>/dev/null || true
    
    print_info "Starting containers..."
    docker-compose up -d
    
    sleep 3
    
    if [ "$(docker ps -q -f status=running -f ancestor=hotel-management:latest)" ]; then
        print_success "Application running on http://localhost:8080"
        print_info "View logs: docker-compose logs -f app"
    else
        print_error "Failed to start containers"
        docker-compose logs
        exit 1
    fi
}

# Deploy to Azure App Service
deploy_azure() {
    print_header "Deploying to Azure App Service"
    
    print_warning "This requires Azure CLI and credentials configured"
    print_info "Ensure you have:"
    print_info "  - Azure CLI installed (az --version)"
    print_info "  - Azure subscription credentials configured"
    print_info "  - App Service created on Azure"
    
    read -p "Continue? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Deployment cancelled"
        return
    fi
    
    cd "$SCRIPT_DIR"
    
    if ! command -v az &> /dev/null; then
        print_error "Azure CLI not found. Install from https://docs.microsoft.com/cli/azure"
        exit 1
    fi
    
    read -p "Enter Azure App Service name: " app_service
    read -p "Enter Azure Resource Group name: " resource_group
    
    print_info "Building for Azure..."
    mvn clean package -DskipTests -q
    
    print_info "Deploying to Azure..."
    az webapp up --name "$app_service" --resource-group "$resource_group" --runtime "JAVA|17-java17" --sku B1
    
    print_success "Deployment to Azure completed"
    print_info "Application available at: https://${app_service}.azurewebsites.net"
}

# Deploy to AWS
deploy_aws() {
    print_header "Deploying to AWS"
    
    print_warning "This requires AWS CLI and credentials configured"
    print_info "Ensure you have:"
    print_info "  - AWS CLI installed"
    print_info "  - AWS credentials configured (~/.aws/credentials)"
    print_info "  - Elastic Beanstalk application and environment created"
    
    read -p "Continue? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Deployment cancelled"
        return
    fi
    
    cd "$SCRIPT_DIR"
    
    if ! command -v aws &> /dev/null; then
        print_error "AWS CLI not found. Install from https://aws.amazon.com/cli"
        exit 1
    fi
    
    read -p "Enter Elastic Beanstalk application name: " app_name
    read -p "Enter environment name: " env_name
    
    print_info "Building for AWS..."
    mvn clean package -DskipTests -q
    
    print_info "Deploying to AWS Elastic Beanstalk..."
    eb deploy "$env_name"
    
    print_success "Deployment to AWS completed"
}

# Health check
health_check() {
    print_header "Health Check"
    
    local url="http://localhost:8080/swagger-ui.html"
    local max_retries=30
    local retry=0
    
    print_info "Checking application health..."
    
    while [ $retry -lt $max_retries ]; do
        if curl -s "$url" > /dev/null 2>&1; then
            print_success "Application is healthy"
            print_info "Swagger UI: $url"
            return 0
        fi
        
        retry=$((retry + 1))
        echo -ne "\rAttempt $retry/$max_retries..."
        sleep 1
    done
    
    print_error "Application is not responding after ${max_retries} attempts"
    return 1
}

# Display usage
usage() {
    cat << EOF
${BLUE}$PROJECT_NAME - Deployment Script${NC}

Usage: ./deploy.sh [COMMAND] [OPTIONS]

Commands:
    check           Check prerequisites
    build           Build the application
    test            Run tests
    local           Deploy locally (runs JAR)
    docker          Deploy using Docker Compose
    azure           Deploy to Azure App Service
    aws             Deploy to AWS Elastic Beanstalk
    health          Check application health
    clean           Clean build artifacts
    help            Show this message

Examples:
    ./deploy.sh check                    # Check prerequisites
    ./deploy.sh build                    # Build application
    ./deploy.sh docker                   # Deploy with Docker
    ./deploy.sh health                   # Check if app is running

Environment:
    Create .env file with:
    - MAILJET_API_KEY
    - MAILJET_SECRET_KEY
    - MAILJET_SENDER_EMAIL
    - CLOUD_NAME (Cloudinary)
    - API_KEY (Cloudinary)
    - API_SECRET (Cloudinary)

EOF
}

# Main
main() {
    local command="${1:-help}"
    
    case "$command" in
        check)
            check_prerequisites
            ;;
        build)
            check_prerequisites
            load_env
            build
            ;;
        test)
            check_prerequisites
            load_env
            run_tests
            ;;
        local)
            check_prerequisites
            load_env
            validate_env
            build
            deploy_local
            ;;
        docker)
            check_prerequisites
            load_env
            validate_env
            build
            deploy_docker
            health_check
            ;;
        azure)
            check_prerequisites
            load_env
            validate_env
            deploy_azure
            ;;
        aws)
            check_prerequisites
            load_env
            validate_env
            deploy_aws
            ;;
        health)
            health_check
            ;;
        clean)
            print_header "Cleaning Build Artifacts"
            mvn clean
            print_success "Cleaned"
            ;;
        help)
            usage
            ;;
        *)
            print_error "Unknown command: $command"
            usage
            exit 1
            ;;
    esac
}

main "$@"
