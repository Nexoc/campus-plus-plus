-- =====================================================
-- V1: Initial database schema for Campus++ Backend
-- =====================================================
-- This migration initializes the application schema
-- and defines shared database primitives.
--
-- No domain tables are created here.
-- Domain-specific tables must be introduced
-- in separate migrations.
-- =====================================================

-- -----------------------------
-- Create application schema
-- -----------------------------
CREATE SCHEMA IF NOT EXISTS app;

-- -----------------------------
-- Set default search path
-- -----------------------------
SET search_path TO app;

-- -----------------------------
-- (Optional) Enum-like types
-- -----------------------------
-- NOTE:
-- PostgreSQL ENUMs are avoided to keep migrations flexible.
-- Status values will be enforced via CHECK constraints
-- in domain-specific tables.

-- -----------------------------
-- Schema ownership (optional)
-- -----------------------------
-- Uncomment if you want explicit ownership control
-- ALTER SCHEMA app OWNER TO campus_backend;

-- =====================================================
-- End of V1
-- =====================================================
