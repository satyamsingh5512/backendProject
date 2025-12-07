#!/bin/bash

echo "🛑 Stopping Ride Hailing Backend..."
echo ""

# Stop Docker containers
echo "📦 Stopping infrastructure services..."
docker-compose down

echo ""
echo "✅ All services stopped successfully!"
