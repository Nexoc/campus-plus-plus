-- Insert initial admin user
-- Password: admin123 (bcrypt hash)

INSERT INTO users (
  id,
  email,
  password_hash,
  role,
  enabled,
  account_non_locked
) VALUES (
  gen_random_uuid(),
  'admin@test.com',
  '$2a$12$QzPOk6ym.QvmyHAlBQXRSO6i1y/3.vwGSTssrqlts/yBd.LmkwBgC',
  'ADMIN',
  true,
  true
);
