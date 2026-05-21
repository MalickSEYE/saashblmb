#!/bin/bash
# ============================================================
# deploy.sh — Script de déploiement Mouride SaaS Platform
# Usage: ./scripts/deploy.sh [staging|production]
# ============================================================

set -euo pipefail

ENV=${1:-staging}
COMPOSE_FILE="docker-compose.yml"

echo "🕌 Mouride SaaS Platform — Déploiement $ENV"
echo "================================================"

# Vérifications
if [ ! -f ".env" ]; then
  echo "❌ Fichier .env introuvable. Copier .env.example → .env"
  exit 1
fi

# Pull dernières images
echo "📥 Récupération des images..."
docker-compose -f $COMPOSE_FILE pull

# Démarrage
echo "🚀 Démarrage des services..."
docker-compose -f $COMPOSE_FILE up -d --remove-orphans

# Attente santé
echo "⏳ Attente de la santé des services..."
sleep 10
docker-compose -f $COMPOSE_FILE ps

# Nettoyage
echo "🧹 Nettoyage des images orphelines..."
docker image prune -f

echo ""
echo "✅ Déploiement terminé !"
echo "   Frontend  : http://localhost:4200"
echo "   Backend   : http://localhost:8080/swagger-ui.html"
echo "   RabbitMQ  : http://localhost:15672"
echo "   MinIO     : http://localhost:9001"
