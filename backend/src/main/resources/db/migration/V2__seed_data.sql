-- ============================================================
-- V2__seed_data.sql — Données initiales
-- ============================================================

-- Super administrateur (mot de passe: Admin2024!)
INSERT INTO users (id, email, password_hash, role, is_active)
VALUES (
  gen_random_uuid(),
  'admin@mouride.sn',
  '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TiGniQmDlseG7WZ/5L1k5r5Ug3Iq',
  'SUPER_ADMIN',
  TRUE
);

-- Dahiras exemples
INSERT INTO dahiras (id, nom, code, ville, pays, date_creation) VALUES
  ('11111111-1111-1111-1111-111111111111', 'Dahira Matlaboul Fawzaini Dakar', 'DK-MF-01', 'Dakar', 'Sénégal', '1985-03-12'),
  ('22222222-2222-2222-2222-222222222222', 'Dahira Hizbut Tarqiyyah Paris', 'FR-HT-01', 'Paris', 'France', '1992-06-20'),
  ('33333333-3333-3333-3333-333333333333', 'Dahira Mourides de Milan', 'IT-ML-01', 'Milan', 'Italie', '2001-09-15');
