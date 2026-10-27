-- PostgreSQL Database Schema for Employee Management REST API
-- Database: employeedb

-- Create database if not exists (run this connected as postgres superuser):
-- CREATE DATABASE employeedb;

-- Table definition for employees
CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL,
    salary NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Index for fast lookup by email
CREATE INDEX IF NOT EXISTS idx_employees_email ON employees(email);

-- Sample initial data (optional)
INSERT INTO employees (name, email, department, salary, created_at, updated_at)
VALUES 
    ('John Doe', 'john.doe@example.com', 'Engineering', 85000.00, NOW(), NOW()),
    ('Jane Smith', 'jane.smith@example.com', 'Human Resources', 65000.00, NOW(), NOW()),
    ('Robert Johnson', 'robert.j@example.com', 'Finance', 92000.00, NOW(), NOW()),
    ('Emily Davis', 'emily.davis@example.com', 'Marketing', 71000.00, NOW(), NOW()),
    ('Michael Brown', 'michael.b@example.com', 'Engineering', 98000.00, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;
