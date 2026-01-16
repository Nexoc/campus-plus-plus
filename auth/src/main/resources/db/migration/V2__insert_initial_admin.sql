-- Insert initial admin user
-- Password: admin123 (bcrypt hash)

INSERT INTO users (
  id,
  email,
  nickname,
  password_hash,
  role,
  enabled,
  account_non_locked
) VALUES (
  gen_random_uuid(),
  'admin@test.com',
  'my name',
  '$2a$12$QzPOk6ym.QvmyHAlBQXRSO6i1y/3.vwGSTssrqlts/yBd.LmkwBgC',
  'Moderator',
  true,
  true
);
