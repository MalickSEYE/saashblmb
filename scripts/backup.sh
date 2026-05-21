#!/bin/bash
# ============================================================
# backup.sh — Sauvegarde automatique PostgreSQL
# Ajouter en crontab : 0 */6 * * * /opt/mouride/scripts/backup.sh
# ============================================================

set -euo pipefail

BACKUP_DIR="/opt/mouride/backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
DB_CONTAINER="mouride-db"
DB_NAME="mouride"
DB_USER="mouride"
KEEP_DAYS=7

mkdir -p $BACKUP_DIR

echo "📦 Sauvegarde PostgreSQL — $TIMESTAMP"
docker exec $DB_CONTAINER pg_dump -U $DB_USER $DB_NAME \
  | gzip > "$BACKUP_DIR/mouride_$TIMESTAMP.sql.gz"

echo "✅ Sauvegarde créée : mouride_$TIMESTAMP.sql.gz"

# Rotation : supprimer les backups > KEEP_DAYS jours
find $BACKUP_DIR -name "mouride_*.sql.gz" -mtime +$KEEP_DAYS -delete
echo "🧹 Anciennes sauvegardes (> $KEEP_DAYS jours) supprimées"

echo "📊 Sauvegardes disponibles :"
ls -lh $BACKUP_DIR/*.sql.gz 2>/dev/null || echo "  Aucune"
