INSERT INTO customers (first_name, last_name, email, contact_number)
VALUES ('John', 'Doe', 'johndoe@example.com', '1234567890');

INSERT INTO products (name, description, price, quantity_in_stock)
VALUES ('Product 1', 'This is a test product', 10.00, 100);
INSERT INTO products (name, description, price, quantity_in_stock)
VALUES ('Product 2', 'This is a test product', 10.00, 100);
INSERT INTO products (name, description, price, quantity_in_stock)
VALUES ('Product 3', 'This is a test product', 10.00, 100);

INSERT INTO orders (order_date, order_status, shipping_address, total_price, customer_id)
VALUES ('2021-01-01', 'Pending', '123 Main St, Anytown USA', 30.00, 1);

INSERT INTO orders_products (order_id, product_id) VALUES (1, 1);
INSERT INTO orders_products (order_id, product_id) VALUES (1, 2);