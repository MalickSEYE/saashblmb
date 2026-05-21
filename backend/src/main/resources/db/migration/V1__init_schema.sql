-- ============================================================
-- V1__init_schema.sql — Mouride SaaS Platform
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ── ENUM TYPES ─────────────────────────────────────────────
CREATE TYPE user_role        AS ENUM ('SUPER_ADMIN','ADMIN','RESPONSABLE','MEMBRE');
CREATE TYPE membre_statut    AS ENUM ('ACTIF','INACTIF','SUSPENDU','EN_ATTENTE');
CREATE TYPE membre_sexe      AS ENUM ('MASCULIN','FEMININ','AUTRE');
CREATE TYPE cotisation_type  AS ENUM ('MENSUELLE','ANNUELLE','SPECIALE','DON');
CREATE TYPE cotisation_statut AS ENUM ('EN_ATTENTE','VALIDEE','REJETEE');
CREATE TYPE paiement_moyen   AS ENUM ('WAVE','ORANGE_MONEY','FREE_MONEY','CARTE','MANUEL');
CREATE TYPE evenement_type   AS ENUM ('MAGAL','GAMOU','ZIAR','CONFERENCE','REUNION','AUTRE');
CREATE TYPE evenement_statut AS ENUM ('PLANIFIE','EN_COURS','TERMINE','ANNULE');
CREATE TYPE contenu_type     AS ENUM ('KHASSAIDE','ARTICLE','AUDIO','VIDEO','PDF','CITATION');
CREATE TYPE projet_statut    AS ENUM ('EN_COURS','TERMINE','SUSPENDU','PLANIFIE');
CREATE TYPE notif_canal      AS ENUM ('EMAIL','SMS','WHATSAPP','IN_APP');
CREATE TYPE notif_statut     AS ENUM ('EN_ATTENTE','ENVOYE','ECHEC','LU');

-- ── USERS ─────────────────────────────────────────────────
CREATE TABLE users (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    email          VARCHAR(255) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    role           user_role   NOT NULL DEFAULT 'MEMBRE',
    is_active      BOOLEAN     NOT NULL DEFAULT TRUE,
    totp_secret    VARCHAR(100),
    last_login     TIMESTAMP,
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── DAHIRAS ───────────────────────────────────────────────
CREATE TABLE dahiras (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    nom             VARCHAR(200) NOT NULL,
    code            VARCHAR(20)  UNIQUE,
    description     TEXT,
    ville           VARCHAR(100),
    pays            VARCHAR(100) NOT NULL DEFAULT 'Sénégal',
    adresse         TEXT,
    telephone       VARCHAR(20),
    email           VARCHAR(255),
    date_creation   DATE,
    is_active       BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── MEMBRES ───────────────────────────────────────────────
CREATE TABLE membres (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID        REFERENCES users(id) ON DELETE SET NULL,
    numero_membre       VARCHAR(20) NOT NULL UNIQUE,
    nom                 VARCHAR(100) NOT NULL,
    prenom              VARCHAR(100) NOT NULL,
    telephone           VARCHAR(20),
    email               VARCHAR(255),
    adresse             TEXT,
    date_naissance      DATE,
    sexe                membre_sexe,
    ville               VARCHAR(100),
    pays                VARCHAR(100) DEFAULT 'Sénégal',
    profession          VARCHAR(150),
    fonction_religieuse VARCHAR(100),
    dahira_id           UUID        REFERENCES dahiras(id) ON DELETE SET NULL,
    statut              membre_statut NOT NULL DEFAULT 'EN_ATTENTE',
    photo_url           VARCHAR(500),
    date_adhesion       DATE        NOT NULL DEFAULT CURRENT_DATE,
    notes               TEXT,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- Responsable du Dahira (FK circulaire résolue après création)
ALTER TABLE dahiras ADD COLUMN responsable_id UUID REFERENCES membres(id) ON DELETE SET NULL;

-- ── COTISATIONS ───────────────────────────────────────────
CREATE TABLE cotisations (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    membre_id           UUID        NOT NULL REFERENCES membres(id),
    dahira_id           UUID        REFERENCES dahiras(id),
    montant             DECIMAL(12,2) NOT NULL CHECK (montant > 0),
    type                cotisation_type NOT NULL DEFAULT 'MENSUELLE',
    periode             VARCHAR(7),   -- format YYYY-MM
    statut              cotisation_statut NOT NULL DEFAULT 'EN_ATTENTE',
    moyen_paiement      paiement_moyen NOT NULL DEFAULT 'MANUEL',
    reference_paiement  VARCHAR(100),
    notes               TEXT,
    validee_par         UUID        REFERENCES users(id),
    date_validation     TIMESTAMP,
    date_paiement       TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── EVENEMENTS ────────────────────────────────────────────
CREATE TABLE evenements (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    titre           VARCHAR(300) NOT NULL,
    type            evenement_type NOT NULL DEFAULT 'REUNION',
    description     TEXT,
    date_debut      TIMESTAMP   NOT NULL,
    date_fin        TIMESTAMP,
    lieu            VARCHAR(300),
    capacite_max    INTEGER,
    prix_entree     DECIMAL(10,2) DEFAULT 0,
    image_url       VARCHAR(500),
    organisateur_id UUID        REFERENCES users(id),
    dahira_id       UUID        REFERENCES dahiras(id),
    statut          evenement_statut NOT NULL DEFAULT 'PLANIFIE',
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── PARTICIPATIONS ────────────────────────────────────────
CREATE TABLE participations (
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
CREATE TABLE contenus_religieux (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    type            contenu_type NOT NULL,
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
CREATE TABLE projets_sociaux (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    titre           VARCHAR(300) NOT NULL,
    description     TEXT,
    budget_cible    DECIMAL(14,2) DEFAULT 0,
    montant_collecte DECIMAL(14,2) DEFAULT 0,
    date_debut      DATE,
    date_fin        DATE,
    statut          projet_statut NOT NULL DEFAULT 'PLANIFIE',
    image_url       VARCHAR(500),
    responsable_id  UUID        REFERENCES membres(id),
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── DONS PROJETS ──────────────────────────────────────────
CREATE TABLE dons_projets (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    projet_id       UUID        NOT NULL REFERENCES projets_sociaux(id),
    membre_id       UUID        REFERENCES membres(id),
    montant         DECIMAL(12,2) NOT NULL CHECK (montant > 0),
    message         TEXT,
    anonyme         BOOLEAN     DEFAULT FALSE,
    moyen_paiement  paiement_moyen NOT NULL DEFAULT 'MANUEL',
    reference       VARCHAR(100),
    date_don        TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── NOTIFICATIONS ─────────────────────────────────────────
CREATE TABLE notifications (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    destinataire_id UUID        NOT NULL REFERENCES users(id),
    canal           notif_canal NOT NULL DEFAULT 'IN_APP',
    sujet           VARCHAR(300),
    message         TEXT        NOT NULL,
    statut          notif_statut NOT NULL DEFAULT 'EN_ATTENTE',
    lu              BOOLEAN     DEFAULT FALSE,
    lu_at           TIMESTAMP,
    sent_at         TIMESTAMP,
    error_message   TEXT,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── ANNONCES ──────────────────────────────────────────────
CREATE TABLE annonces (
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

-- ── TRANSACTIONS FINANCIÈRES ──────────────────────────────
CREATE TABLE transactions_financieres (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    type            VARCHAR(50) NOT NULL,  -- COTISATION, DON, DEPENSE, etc.
    reference_id    UUID,
    montant         DECIMAL(12,2) NOT NULL,
    description     TEXT,
    solde_apres     DECIMAL(14,2),
    effectue_par    UUID        REFERENCES users(id),
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── AUDIT LOGS ────────────────────────────────────────────
CREATE TABLE audit_logs (
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
CREATE INDEX idx_membres_dahira      ON membres(dahira_id);
CREATE INDEX idx_membres_statut      ON membres(statut);
CREATE INDEX idx_membres_nom         ON membres(nom, prenom);
CREATE INDEX idx_cotisations_membre  ON cotisations(membre_id);
CREATE INDEX idx_cotisations_periode ON cotisations(periode);
CREATE INDEX idx_cotisations_statut  ON cotisations(statut);
CREATE INDEX idx_evenements_date     ON evenements(date_debut);
CREATE INDEX idx_notifications_dest  ON notifications(destinataire_id, lu);
CREATE INDEX idx_audit_user          ON audit_logs(user_id, created_at);
CREATE INDEX idx_contenus_type       ON contenus_religieux(type, est_publie);
