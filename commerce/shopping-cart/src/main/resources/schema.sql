CREATE TABLE IF NOT EXISTS carts (
    shopping_cart_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR NOT NULL,
    state VARCHAR
);

CREATE TABLE IF NOT EXISTS cart_products (
    shopping_cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT,
    PRIMARY KEY (shopping_cart_id, product_id),
    FOREIGN KEY (shopping_cart_id) REFERENCES carts (shopping_cart_id)
);