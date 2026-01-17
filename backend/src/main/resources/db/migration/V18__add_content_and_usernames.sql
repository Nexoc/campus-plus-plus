-- V18: Add content field to threads and username fields to threads/posts/comments

-- Add content (optional description) to threads
ALTER TABLE app.threads ADD COLUMN content TEXT;

-- Add username fields to store creator names
ALTER TABLE app.threads ADD COLUMN created_by_name VARCHAR(255);
ALTER TABLE app.posts ADD COLUMN user_name VARCHAR(255);
ALTER TABLE app.comments ADD COLUMN user_name VARCHAR(255);

-- Create indexes for better query performance
CREATE INDEX idx_threads_created_by_name ON app.threads(created_by_name);
CREATE INDEX idx_posts_user_name ON app.posts(user_name);
CREATE INDEX idx_comments_user_name ON app.comments(user_name);
