-- ============================================================
-- V1__init_schema.sql — Mouride SaaS Platform
-- ENUMs remplacés par VARCHAR pour compatibilité Hibernate
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ── USERS ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    email          VARCHAR(255) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    role           VARCHAR(50)  NOT NULL DEFAULT 'MEMBRE',
    is_active      BOOLEAN     NOT NULL DEFAULT TRUE,
    totp_secret    VARCHAR(100),
    last_login     TIMESTAMP,
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── DAHIRAS ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS dahiras (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    nom             VARCHAR(200) NOT NULL,
    code            VARCHAR(20)  UNIQUE,
    description     TEXT,
    ville           VARCHAR(100),
    pays            VARCHAR(100) NOT NULL DEFAULT 'Sénégal',
    adresse         TEXT,
    telephone       VARCHAR(20),
    email           VARCHAR(255),
    responsable_id  UUID,
    date_creation   DATE,
    is_active       BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── MEMBRES ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS membres (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID        REFERENCES users(id) ON DELETE SET NULL,
    numero_membre       VARCHAR(20) NOT NULL UNIQUE,
    nom                 VARCHAR(100) NOT NULL,
    prenom              VARCHAR(100) NOT NULL,
    telephone           VARCHAR(20),
    email               VARCHAR(255),
    adresse             TEXT,
    date_naissance      DATE,
    sexe                VARCHAR(20),
    ville               VARCHAR(100),
    pays                VARCHAR(100) DEFAULT 'Sénégal',
    profession          VARCHAR(150),
    fonction_religieuse VARCHAR(100),
    dahira_id           UUID        REFERENCES dahiras(id) ON DELETE SET NULL,
    statut              VARCHAR(50)  NOT NULL DEFAULT 'EN_ATTENTE',
    photo_url           VARCHAR(500),
    date_adhesion       DATE        NOT NULL DEFAULT CURRENT_DATE,
    notes               TEXT,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP   NOT NULL DEFAULT NOW()
);

ALTER TABLE dahiras ADD COLUMN IF NOT EXISTS responsable_id_fk UUID REFERENCES membres(id) ON DELETE SET NULL;

-- ── COTISATIONS ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cotisations (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    membre_id           UUID        NOT NULL REFERENCES membres(id),
    dahira_id           UUID        REFERENCES dahiras(id),
    montant             DECIMAL(12,2) NOT NULL CHECK (montant > 0),
    type                VARCHAR(50)  NOT NULL DEFAULT 'MENSUELLE',
    periode             VARCHAR(7),
    statut              VARCHAR(50)  NOT NULL DEFAULT 'EN_ATTENTE',
    moyen_paiement      VARCHAR(50)  NOT NULL DEFAULT 'MANUEL',
    reference_paiement  VARCHAR(100),
    notes               TEXT,
    validee_par         UUID        REFERENCES users(id),
    date_validation     TIMESTAMP,
    date_paiement       TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── EVENEMENTS ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS evenements (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    titre           VARCHAR(300) NOT NULL,
    type            VARCHAR(50)  NOT NULL DEFAULT 'REUNION',
    description     TEXT,
    date_debut      TIMESTAMP   NOT NULL,
    date_fin        TIMESTAMP,
    lieu            VARCHAR(300),
    capacite_max    INTEGER,
    prix_entree     DECIMAL(10,2) DEFAULT 0,
    image_url       VARCHAR(500),
    organisateur_id UUID        REFERENCES users(id),
    dahira_id       UUID        REFERENCES dahiras(id),
    statut          VARCHAR(50)  NOT NULL DEFAULT 'PLANIFIE',
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── PARTICIPATIONS ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS participations (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    membre_id       UUID        NOT NULL REFERENCES membres(id),
    evenement_id    UUID        NOT NULL REFERENCES evenements(id),
    qr_code         VARCHAR(200) UNIQUE,
    present         BOOLEAN     DEFAULT FALSE,
    checked_at      TIMESTAMP,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE(membre_id, evenement_id)
);

-- ── CONTENUS RELIGIEUX ────────────────────────────────────
CREATE TABLE IF NOT EXISTS contenus_religieux (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    type            VARCHAR(50)  NOT NULL,
    titre           VARCHAR(300) NOT NULL,
    description     TEXT,
    contenu         TEXT,
    url_fichier     VARCHAR(500),
    thumbnail_url   VARCHAR(500),
    auteur          VARCHAR(200),
    langue          VARCHAR(10)  DEFAULT 'fr',
    duree_secondes  INTEGER,
    nb_vues         INTEGER      DEFAULT 0,
    est_publie      BOOLEAN      DEFAULT FALSE,
    publie_par      UUID         REFERENCES users(id),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── PROJETS SOCIAUX ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS projets_sociaux (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    titre           VARCHAR(300) NOT NULL,
    description     TEXT,
    budget_cible    DECIMAL(14,2) DEFAULT 0,
    montant_collecte DECIMAL(14,2) DEFAULT 0,
    date_debut      DATE,
    date_fin        DATE,
    statut          VARCHAR(50)  NOT NULL DEFAULT 'PLANIFIE',
    image_url       VARCHAR(500),
    responsable_id  UUID        REFERENCES membres(id),
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── NOTIFICATIONS ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    destinataire_id UUID        NOT NULL REFERENCES users(id),
    canal           VARCHAR(50)  NOT NULL DEFAULT 'IN_APP',
    sujet           VARCHAR(300),
    message         TEXT        NOT NULL,
    statut          VARCHAR(50)  NOT NULL DEFAULT 'EN_ATTENTE',
    lu              BOOLEAN     DEFAULT FALSE,
    lu_at           TIMESTAMP,
    sent_at         TIMESTAMP,
    error_message   TEXT,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── ANNONCES ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS annonces (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    titre           VARCHAR(300) NOT NULL,
    contenu         TEXT        NOT NULL,
    image_url       VARCHAR(500),
    auteur_id       UUID        REFERENCES users(id),
    est_publiee     BOOLEAN     DEFAULT FALSE,
    date_expiration TIMESTAMP,
    nb_vues         INTEGER     DEFAULT 0,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── AUDIT LOGS ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS audit_logs (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID        REFERENCES users(id),
    action          VARCHAR(100) NOT NULL,
    entite          VARCHAR(100) NOT NULL,
    entite_id       UUID,
    ancienne_valeur TEXT,
    nouvelle_valeur TEXT,
    ip_address      VARCHAR(45),
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── INDEX ─────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_membres_dahira      ON membres(dahira_id);
CREATE INDEX IF NOT EXISTS idx_membres_statut      ON membres(statut);
CREATE INDEX IF NOT EXISTS idx_cotisations_membre  ON cotisations(membre_id);
CREATE INDEX IF NOT EXISTS idx_cotisations_statut  ON cotisations(statut);
CREATE INDEX IF NOT EXISTS idx_evenements_date     ON evenements(date_debut);
CREATE INDEX IF NOT EXISTS idx_notifications_dest  ON notifications(destinataire_id, lu);
