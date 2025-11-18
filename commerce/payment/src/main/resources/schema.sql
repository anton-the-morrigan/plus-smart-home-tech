CREATE TABLE IF NOT EXISTS payments (
    payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    total_payment DOUBLE PRECISION NOT NULL,
    delivery_total DOUBLE PRECISION NOT NULL,
    fee_total DOUBLE PRECISION NOT NULL,
    payment_state VARCHAR NOT NULL
);