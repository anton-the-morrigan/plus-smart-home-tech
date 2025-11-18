CREATE TABLE IF NOT EXISTS warehouse_products (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN,
    width DOUBLE PRECISION,
    height DOUBLE PRECISION,
    depth DOUBLE PRECISION,
    weight DOUBLE PRECISION,
    quantity BIGINT
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fragile BOOLEAN,
    delivery_volume DOUBLE PRECISION NOT NULL,
    delivery_weight DOUBLE PRECISION NOT NULL,
    delivery_id UUID,
    order_id UUID NOT NULL
);

CREATE TABLE IF NOT EXISTS booking_products (
    booking_id UUID REFERENCES bookings(booking_id) ON DELETE CASCADE,
    product_id UUID REFERENCES warehouse_product(product_id) ON DELETE CASCADE,
    quantity BIGINT NOT NULL
);