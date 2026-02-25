-- Saga Coordinator Database

CREATE TABLE saga_state (
    saga_id VARCHAR(36) PRIMARY KEY,
    from_account_number VARCHAR(20) NOT NULL,
    to_account_number VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    saga_type VARCHAR(20) NOT NULL DEFAULT 'ORCHESTRATION',
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
