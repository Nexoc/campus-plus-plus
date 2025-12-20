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
  '$2a$10$7QZ6v2kB5uXQKZCzP9G8We7Y2bZJz7z0q6Zl6gYvG9K9c1C9xQH9K',
  'ADMIN',
  true,
  true
);
