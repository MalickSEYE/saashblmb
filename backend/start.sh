#!/bin/sh
set -e

echo "==> PORT=$PORT"
echo "==> DATABASE_URL present: $([ -n "$DATABASE_URL" ] && echo YES || echo NO)"
echo "==> REDIS_HOST=$REDIS_HOST"
echo "==> SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE"

if [ -n "$DATABASE_URL" ]; then
  WITHOUT_PROTO=$(echo "$DATABASE_URL" | sed 's|postgres://||')
  USERPASS=$(echo "$WITHOUT_PROTO" | cut -d@ -f1)
  HOSTPORTDB=$(echo "$WITHOUT_PROTO" | cut -d@ -f2)
  DB_USER_EXTRACTED=$(echo "$USERPASS" | cut -d: -f1)
  DB_PASS_EXTRACTED=$(echo "$USERPASS" | cut -d: -f2)
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${HOSTPORTDB}"
  export SPRING_DATASOURCE_USERNAME="$DB_USER_EXTRACTED"
  export SPRING_DATASOURCE_PASSWORD="$DB_PASS_EXTRACTED"
  echo "==> SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL"
fi

APP_PORT=${PORT:-8080}
echo "==> Starting on port $APP_PORT"

exec java \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=70.0 \
  -Xss256k \
  -Djava.security.egd=file:/dev/./urandom \
  -Dserver.port=$APP_PORT \
  -Dlogging.level.root=INFO \
  -Dlogging.level.com.mouride=DEBUG \
  -Dlogging.level.org.flywaydb=DEBUG \
  -Dlogging.level.org.springframework.boot.autoconfigure=DEBUG \
  -jar /app/app.jar 2>&1
