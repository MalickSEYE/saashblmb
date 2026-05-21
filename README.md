# 🕌 Mouride SaaS Platform

Plateforme web et mobile de gestion complète pour les organismes religieux Mouride :
Dahiras, mosquées, associations, écoles coraniques et structures communautaires.

---

## 📋 Sommaire

1. [Stack technique](#stack-technique)
2. [Fonctionnalités](#fonctionnalités)
3. [Prérequis](#prérequis)
4. [Installation rapide](#installation-rapide)
5. [Structure du projet](#structure-du-projet)
6. [API REST](#api-rest)
7. [Configuration](#configuration)
8. [Déploiement](#déploiement)
9. [Sécurité](#sécurité)

---

## Stack technique

| Couche          | Technologie                              |
|-----------------|------------------------------------------|
| Frontend Web    | Angular 17 (Standalone Components)       |
| Mobile          | React Native / Capacitor (PWA)           |
| Backend API     | Spring Boot 3.2 — Java 17                |
| Base de données | PostgreSQL 15 + Flyway                   |
| Cache           | Redis 7                                  |
| Messaging       | RabbitMQ 3                               |
| Stockage        | MinIO (S3-compatible)                    |
| Sécurité        | Spring Security + JWT (JJWT 0.12)        |
| Documentation   | SpringDoc / Swagger UI                   |
| Build           | Maven 3.9 + npm / Angular CLI 17         |
| CI/CD           | GitHub Actions                           |
| Conteneurs      | Docker + Docker Compose                  |

---

## Fonctionnalités

### Modules implémentés (MVP)
- ✅ **Authentification** — JWT, refresh token, RBAC (4 rôles)
- ✅ **Gestion des membres** — CRUD, recherche, filtres, numérotation auto
- ✅ **Gestion des Dahiras** — CRUD, statistiques, membres par dahira
- ✅ **Cotisations** — Enregistrement, validation, rejet, Wave/OM/Manuel
- ✅ **Événements religieux** — Magal, Gamou, Ziar, Réunions
- ✅ **Dashboard** — KPIs globaux, statistiques en temps réel
- ✅ **Base de données** — Schéma complet (12 tables), Flyway migrations

### Modules planifiés (sprints suivants)
- 🔲 Export PDF/Excel des membres
- 🔲 Carte de membre numérique (PDF + QR Code)
- 🔲 Notifications (SMS, Email, WhatsApp)
- 🔲 Bibliothèque religieuse (Khassaïdes, audios, vidéos)
- 🔲 Projets sociaux et humanitaires
- 🔲 Chatbot religieux (IA — Anthropic API)
- 🔲 Traduction automatique Français/Wolof/Arabe
- 🔲 Application mobile React Native

---

## Prérequis

- Docker Desktop 24+ et Docker Compose v2
- Node.js 20+ (développement frontend)
- Java 17 JDK + Maven 3.9 (développement backend)
- Git

---

## Installation rapide

```bash
# 1. Cloner le projet
git clone https://github.com/votre-org/mouride-platform.git
cd mouride-platform

# 2. Configurer les variables d'environnement
cp .env.example .env
# Éditer .env et renseigner les valeurs

# 3. Démarrer tous les services
docker-compose up --build

# 4. Ouvrir l'application
# Frontend  → http://localhost:4200
# Swagger   → http://localhost:8080/swagger-ui.html
# RabbitMQ  → http://localhost:15672  (mouride / rabbit_mouride)
# MinIO     → http://localhost:9001   (minio_mouride / minio_secret_2024)
```

### Identifiants par défaut

| Service   | Email / User           | Mot de passe       |
|-----------|------------------------|--------------------|
| App Admin | admin@mouride.sn       | Admin2024!         |
| RabbitMQ  | mouride                | rabbit_mouride     |
| MinIO     | minio_mouride          | minio_secret_2024  |

> ⚠️ **Changer tous les mots de passe avant un déploiement en production !**

---

## Structure du projet

```
mouride-platform/
├── backend/                          # Spring Boot API
│   ├── src/main/java/com/mouride/
│   │   ├── MourideApplication.java   # Point d'entrée + OpenAPI config
│   │   ├── domain/
│   │   │   ├── model/                # Entités JPA (User, Membre, Dahira…)
│   │   │   └── repository/           # Repositories Spring Data JPA
│   │   ├── application/
│   │   │   ├── dto/                  # DTOs (Request/Response)
│   │   │   └── usecase/              # Services métier
│   │   ├── infrastructure/
│   │   │   └── security/             # JWT, Spring Security config
│   │   └── controller/               # REST Controllers
│   ├── src/main/resources/
│   │   ├── application.yml           # Configuration principale
│   │   └── db/migration/             # Migrations Flyway (SQL)
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                         # Angular 17
│   ├── src/app/
│   │   ├── core/
│   │   │   ├── services/             # AuthService, ApiService
│   │   │   ├── guards/               # authGuard, roleGuard
│   │   │   └── interceptors/         # JWT interceptor
│   │   ├── shared/
│   │   │   └── components/layout/    # Sidebar + Layout principal
│   │   └── features/
│   │       ├── auth/                 # Login, Register
│   │       ├── dashboard/            # Tableau de bord KPIs
│   │       ├── membres/              # Liste membres
│   │       ├── dahiras/              # Gestion Dahiras
│   │       ├── cotisations/          # Cotisations & finance
│   │       └── evenements/           # Événements religieux
│   ├── src/environments/             # Configuration API URLs
│   ├── Dockerfile
│   └── nginx.conf                    # Reverse proxy + SPA fallback
│
├── .github/workflows/
│   └── ci-cd.yml                     # Pipeline CI/CD GitHub Actions
├── scripts/
│   ├── deploy.sh                     # Script déploiement
│   └── backup.sh                     # Sauvegarde automatique BDD
├── docker-compose.yml                # Orchestration complète
├── .env.example                      # Template variables d'env
└── README.md
```

---

## API REST

Documentation interactive disponible à : `http://localhost:8080/swagger-ui.html`

### Endpoints principaux

| Module          | Méthode | Endpoint                           |
|-----------------|---------|-------------------------------------|
| Auth            | POST    | /api/v1/auth/login                  |
| Auth            | POST    | /api/v1/auth/register               |
| Auth            | POST    | /api/v1/auth/refresh                |
| Membres         | GET     | /api/v1/membres?search=&statut=     |
| Membres         | POST    | /api/v1/membres                     |
| Membres         | GET     | /api/v1/membres/stats               |
| Dahiras         | GET     | /api/v1/dahiras                     |
| Dahiras         | POST    | /api/v1/dahiras                     |
| Cotisations     | POST    | /api/v1/cotisations                 |
| Cotisations     | PUT     | /api/v1/cotisations/{id}/valider    |
| Cotisations     | GET     | /api/v1/cotisations/stats           |
| Événements      | GET     | /api/v1/evenements                  |
| Événements      | POST    | /api/v1/evenements                  |
| Dashboard       | GET     | /api/v1/dashboard/stats             |

### Authentification

```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@mouride.sn","password":"Admin2024!"}'

# Utiliser le token
curl http://localhost:8080/api/v1/membres \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

---

## Configuration

Toutes les variables sont dans `.env` (copier depuis `.env.example`) :

```env
DB_PASSWORD=           # Mot de passe PostgreSQL
REDIS_PASSWORD=        # Mot de passe Redis
JWT_SECRET=            # Clé secrète JWT (min. 256 bits)
MINIO_ACCESS_KEY=      # Accès MinIO
MINIO_SECRET_KEY=      # Secret MinIO
SMTP_HOST=             # Serveur email
WAVE_API_KEY=          # Wave (paiement mobile)
ORANGE_MONEY_API_KEY=  # Orange Money
ANTHROPIC_API_KEY=     # IA chatbot (optionnel)
```

---

## Déploiement

### Docker Compose (recommandé)
```bash
cp .env.example .env && nano .env  # Configurer les secrets
docker-compose up -d --build
```

### VPS / Serveur dédié
```bash
chmod +x scripts/deploy.sh
./scripts/deploy.sh production
```

### Sauvegarde automatique
```bash
# Ajouter au crontab (toutes les 6h)
0 */6 * * * /opt/mouride/scripts/backup.sh >> /var/log/mouride-backup.log 2>&1
```

---

## Sécurité

- 🔐 JWT (Access Token 1h, Refresh Token 7j)
- 🛡️ Spring Security avec RBAC à 4 niveaux
- 🔒 BCrypt (coût 12) pour les mots de passe
- 🌐 HTTPS obligatoire en production (TLS 1.3)
- 📋 Audit log complet de toutes les actions
- 🚦 Rate limiting sur les endpoints d'authentification
- ✅ Protection CORS, CSRF, XSS, SQL Injection

---

## Licence

Propriétaire — © Mouride SaaS Platform 2024

---

*Développé avec ❤️ pour la communauté Mouride mondiale*
