#!/bin/sh
# start.sh — Convertit DATABASE_URL postgres:// en jdbc:postgresql://
# et démarre Spring Boot avec les bons paramètres

set -e

# Render fournit DATABASE_URL sous la forme :
# postgres://user:password@host:port/dbname
# Spring Boot a besoin de :
# jdbc:postgresql://host:port/dbname

if [ -n "$DATABASE_URL" ]; then
  # Remplacer "postgres://" par "jdbc:postgresql://"
  JDBC_URL=$(echo "$DATABASE_URL" | sed 's|postgres://|jdbc:postgresql://|')
  # Extraire user:password de l'URL et les mettre en variables séparées
  # Format: jdbc:postgresql://user:password@host:port/db
  DB_CREDENTIALS=$(echo "$JDBC_URL" | sed 's|jdbc:postgresql://\([^@]*\)@.*|\1|')
  DB_USER_EXTRACTED=$(echo "$DB_CREDENTIALS" | cut -d: -f1)
  DB_PASS_EXTRACTED=$(echo "$DB_CREDENTIALS" | cut -d: -f2)
  DB_HOST_PORT_DB=$(echo "$JDBC_URL" | sed 's|jdbc:postgresql://[^@]*@\(.*\)|\1|')
  
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST_PORT_DB}"
  export SPRING_DATASOURCE_USERNAME="${DB_USER_EXTRACTED}"
  export SPRING_DATASOURCE_PASSWORD="${DB_PASS_EXTRACTED}"
  
  echo "✅ SPRING_DATASOURCE_URL configuré"
else
  # Fallback: construire l'URL depuis les variables séparées
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
  export SPRING_DATASOURCE_USERNAME="${DB_USER}"
  export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"
  echo "✅ SPRING_DATASOURCE_URL construit depuis variables séparées"
fi

# Démarrer Spring Boot
exec java \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=70.0 \
  -Xss256k \
  -Djava.security.egd=file:/dev/./urandom \
  -Dserver.port=${PORT:-8080} \
  -jar app.jar
