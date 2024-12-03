CREATE TABLE IF NOT EXISTS customers (customer_id SERIAL PRIMARY KEY, first_name VARCHAR(255), email VARCHAR(255),
    last_name VARCHAR(255), contact_number VARCHAR(255));

CREATE TABLE IF NOT EXISTS products (product_id SERIAL PRIMARY KEY, name VARCHAR(255), description VARCHAR(255),
                       price int, quantity_in_stock int);

CREATE TABLE IF NOT EXISTS orders (order_id SERIAL PRIMARY KEY, order_date DATE, order_status VARCHAR(255),
    shipping_address VARCHAR(255), total_price int, customer_id int,
      FOREIGN KEY (customer_id) REFERENCES customers(customer_id));

CREATE TABLE IF NOT EXISTS orders_products (order_id int , product_id int, FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id));

