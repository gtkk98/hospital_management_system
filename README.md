# Hospital Management System

A production-grade REST API built with Spring Boot, demonstrating enterprise 
software patterns used in ERP and healthcare systems.

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21 · Spring Boot 4 |
| Database | PostgreSQL 18 · Flyway migrations |
| Security | Spring Security · JWT · BCrypt |
| ORM | Hibernate JPA |
| Build | Maven · Docker |

## Architecture

The project follows a strict 4-layer architecture:

## Key Features

**Authentication & Authorization**
- JWT token-based authentication with BCrypt password hashing
- Role-Based Access Control with three roles: ADMIN, DOCTOR, PATIENT
- Endpoint-level security via Spring Security and @PreAuthorize

**Appointment System**
- Double-booking prevention enforced at two levels:
  - Service layer query check (catches 99% of cases)
  - PostgreSQL EXCLUDE constraint (catches race conditions Java missed)
- Business hour validation — no weekends, 8 AM–6 PM only
- Appointment status machine: SCHEDULED → COMPLETED / CANCELLED / NO_SHOW

**Data Integrity**
- @Transactional ensures appointment + invoice are created atomically
- Cancellation automatically waives the linked invoice in the same transaction
- Database-level CHECK constraints on all status and role enums

**Database Design**
- Fully normalized schema across 7 tables
- Foreign key relationships with appropriate CASCADE rules
- Flyway versioned migrations for safe schema evolution

## API Endpoints

| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | /api/auth/register | PUBLIC | Register new user |
| POST | /api/auth/login | PUBLIC | Login, receive JWT |
| GET | /api/doctors | AUTH | List / search doctors |
| GET | /api/doctors/available | AUTH | Find available doctors by slot |
| POST | /api/appointments | PATIENT | Book appointment |
| PATCH | /api/appointments/{id}/cancel | AUTH | Cancel appointment |
| PATCH | /api/appointments/{id}/complete | DOCTOR | Complete appointment |
| GET | /api/patients/{id} | AUTH | Get patient profile |
