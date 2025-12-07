#!/bin/bash

echo "🚀 Starting Ride Hailing Backend..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running. Please start Docker first."
    exit 1
fi

echo "📦 Starting infrastructure services (PostgreSQL, Redis, Kafka)..."
docker-compose up -d

echo ""
echo "⏳ Waiting for services to be ready..."
sleep 10

echo ""
echo "🔨 Building application..."
mvn clean install -DskipTests

echo ""
echo "🚀 Starting Spring Boot application..."
mvn spring-boot:run

echo ""
echo "✅ Application started successfully!"
echo ""
echo "📍 API Base URL: http://localhost:8080"
echo "📍 Health Check: http://localhost:8080/api/health"
echo ""
echo "📖 See README.md for API documentation"
echo "📖 See API_TESTING.md for cURL examples"
