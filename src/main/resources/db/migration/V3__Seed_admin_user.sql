-- V3__Seed_admin_user.sql
INSERT INTO tb_user (name, email, password, enabled, created_at, updated_at)
VALUES (
    'Admin',
    'admin@admin.com',
    '$2a$10$7cS8Qt1w/50A8mXhF9sZ6uGmXWW8WTi0X63hOdZUT3RzStLkKmDZK',
    TRUE,
    NOW(),
    NOW()
)
ON CONFLICT (email) DO NOTHING;
