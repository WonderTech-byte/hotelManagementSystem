#!/bin/bash
set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🐳 Hotel Management System - Docker Startup${NC}"
echo "=============================================="
echo ""

# Check if .env file exists
if [ ! -f .env ]; then
    echo -e "${BLUE}ℹ️  .env file not found${NC}"
    echo "Creating .env from .env.example..."
    cp .env .env
    echo -e "${GREEN}✅ Created .env${NC}"
    echo ""
fi

# Check if port 3306 is in use
if netstat -tuln 2>/dev/null | grep -q ':3306 '; then
    echo -e "${YELLOW}⚠️  Port 3306 is already in use (local MySQL running)${NC}"
    echo "Using docker-compose.dev.yml (MySQL on port 3307)"
    echo ""
    docker-compose -f docker-compose.dev.yml up --build "$@"
else
    # Only check for external service variables (not DB since Docker manages it)
    external_vars=(
        "JWT_SECRET"
        "MAIL_USERNAME"
        "MAIL_PASSWORD"
        "CLOUD_NAME"
        "API_KEY"
        "API_SECRET"
    )

    missing_vars=()

    for var in "${external_vars[@]}"; do
        if ! grep -q "^${var}=" .env; then
            missing_vars+=("$var")
        else
            value=$(grep "^${var}=" .env | cut -d'=' -f2)
            # Check if value is placeholder
            if [[ "$value" == *"your-"* ]] || [ -z "$value" ]; then
                missing_vars+=("$var (placeholder value)")
            fi
        fi
    done

    if [ ${#missing_vars[@]} -gt 0 ]; then
        echo -e "${YELLOW}⚠️  WARNING: Unconfigured external services:${NC}"
        echo ""
        for var in "${missing_vars[@]}"; do
            echo "  - $var"
        done
        echo ""
        echo -e "${BLUE}The app will start but these features won't work:${NC}"
        echo "  - Email (password reset, notifications)"
        echo "  - Image uploads (Cloudinary)"
        echo ""
        read -p "Continue without configuring? (y/N) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            echo "Please edit .env with your credentials and try again"
            exit 1
        fi
    fi

    echo -e "${GREEN}✅ .env configured${NC}"
    echo ""
    echo "Starting Docker containers..."
    echo ""

    # Run docker-compose
    docker-compose up --build "$@"
fi
