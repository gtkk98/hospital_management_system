-- Step 1: Drop the overlap constraint (it depends on the old column)
ALTER TABLE appointments DROP CONSTRAINT IF EXISTS no_doctor_overlap;

-- Step 2: Now drop the old misnamed column safely
ALTER TABLE appointments DROP COLUMN IF EXISTS schedule_at;

-- Step 3: Recreate the constraint using the correct column name
ALTER TABLE appointments ADD CONSTRAINT no_doctor_overlap
    EXCLUDE USING gist (
        doctor_id WITH =,
        tsrange(scheduled_at,
                scheduled_at + (duration_minutes * interval '1 minute')) WITH &&
    );