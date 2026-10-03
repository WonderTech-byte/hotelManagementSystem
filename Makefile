.PHONY: help setup start stop logs clean

help:
	@echo "Hotel Management System - Docker Commands"
	@echo "=========================================="
	@echo ""
	@echo "make setup     - Create .env from .env.example"
	@echo "make start     - Start containers (Linux/Mac)"
	@echo "make stop      - Stop containers"
	@echo "make logs      - View container logs"
	@echo "make logs-app  - View app logs only"
	@echo "make logs-db   - View database logs only"
	@echo "make clean     - Remove all containers and volumes"
	@echo ""

setup:
	@if [ -f .env ]; then \
		echo "✓ .env already exists"; \
	else \
		cp .env.example .env; \
		echo "✓ Created .env from .env.example"; \
		echo "⚠️  Edit .env with your actual credentials before running 'make start'"; \
	fi

start: setup
	@echo "Starting Docker containers..."
	@if [ -x docker-start.sh ]; then \
		./docker-start.sh; \
	else \
		docker-compose up --build; \
	fi

stop:
	@echo "Stopping containers..."
	docker-compose down

logs:
	docker-compose logs -f

logs-app:
	docker-compose logs -f app

logs-db:
	docker-compose logs -f mysql

clean:
	@echo "Removing containers and volumes..."
	docker-compose down -v
	@echo "✓ Cleaned up"

ps:
	docker-compose ps

shell-app:
	docker-compose exec app /bin/sh

shell-db:
	docker-compose exec mysql mysql -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE}

rebuild:
	docker-compose build --no-cache
	docker-compose up
