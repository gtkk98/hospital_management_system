-- Drop the existing incorrect check constraint
ALTER TABLE invoices DROP CONSTRAINT IF EXISTS invoices_status_check;

-- Recreate it with the correct uppercase values matching the Java enum
ALTER TABLE invoices ADD CONSTRAINT invoices_status_check
    CHECK (status IN ('UNPAID', 'PAID', 'WAIVED'));
