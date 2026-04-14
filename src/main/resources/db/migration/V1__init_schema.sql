-- Enable the extension needed for the overlap constraint
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- User table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
                   CHECK (role IN ('ADMIN', 'DOCTOR', 'PATIENT')),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Patient table
CREATE TABLE patients (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10)
                      CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    phone VARCHAR(20),
    blood_group VARCHAR(5),
    address TEXT
);

-- Doctors table
CREATE TABLE doctors (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) UNIQUE,
    phone VARCHAR(20),
    consultation_fee INT NOT NULL DEFAULT 0
);

-- Appointments table
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id BIGINT NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    schedule_at TIMESTAMP NOT NULL,
    duration_minutes INT NOT NULL DEFAULT 30,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED'
                      CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELED', 'NO_SHOW')),
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT no_doctor_overlap EXCLUDE USING gist(
        doctor_id WITH =,
        tsrange(schedule_at, schedule_at + (duration_minutes * interval '1 minutes')) WITH &&
    )
);

CREATE INDEX idx_appt_doctor ON appointments(doctor_id);
CREATE INDEX idx_appt_patient ON appointments(patient_id);
CREATE INDEX idx_appt_time ON appointments(schedule_at);

-- Medical Records table
CREATE TABLE medical_records
(
    id             BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT    NOT NULL REFERENCES appointments (id),
    diagnosis      TEXT,
    prescription   TEXT,
    lab_results    TEXT,
    recorded_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Invoice table
CREATE TABLE invoices(
    id             BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT    NOT NULL REFERENCES appointments (id),
    amount         INT       NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'UNPAID'
                      CHECK (status IN ('PENDING', 'PAID', 'CANCELED')),
    issued_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    paid_at        TIMESTAMP
);

-- Audit log table
CREATE TABLE audit_logs
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT      NOT NULL REFERENCES users (id) ON DELETE SET NULL,
    action       VARCHAR(50) NOT NULL,
    entity_type  VARCHAR(50) NOT NULL,
    entity_id    BIGINT,
    performed_at TIMESTAMP   NOT NULL DEFAULT NOW(),
);

CREATE INDEX idx_audit_user   ON audit_logs(user_id);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);









