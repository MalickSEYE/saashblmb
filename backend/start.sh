#!/bin/sh
set -e

echo "==> PORT=$PORT"
echo "==> DB_HOST=$DB_HOST"
echo "==> DB_PORT=$DB_PORT"
echo "==> DB_NAME=$DB_NAME"
echo "==> DB_USER=$DB_USER"

# Construire l'URL JDBC depuis les variables séparées injectées par render.yaml
# (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD)
if [ -n "$DB_HOST" ] && [ -n "$DB_NAME" ]; then
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT:-5432}/${DB_NAME}"
  export SPRING_DATASOURCE_USERNAME="${DB_USER}"
  export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"
  echo "==> JDBC URL: jdbc:postgresql://${DB_HOST}:${DB_PORT:-5432}/${DB_NAME}"
elif [ -n "$DATABASE_URL" ]; then
  # Fallback: parser DATABASE_URL manuellement
  # Format: postgres://user:pass@host:port/db
  TMPURL=$(echo "$DATABASE_URL" | sed 's|postgres://||' | sed 's|postgresql://||')
  USERPASS=$(echo "$TMPURL" | sed 's/@.*//')
  HOSTPART=$(echo "$TMPURL" | sed 's/.*@//')
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${HOSTPART}"
  export SPRING_DATASOURCE_USERNAME=$(echo "$USERPASS" | cut -d: -f1)
  export SPRING_DATASOURCE_PASSWORD=$(echo "$USERPASS" | cut -d: -f2-)
  echo "==> JDBC URL (from DATABASE_URL): jdbc:postgresql://${HOSTPART}"
else
  echo "==> ERREUR: Aucune variable de base de données trouvée!"
  exit 1
fi

APP_PORT=${PORT:-8080}
echo "==> Démarrage sur port $APP_PORT"

exec java \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=70.0 \
  -Xss256k \
  -Djava.security.egd=file:/dev/./urandom \
  -Dserver.port=$APP_PORT \
  -jar /app/app.jar
