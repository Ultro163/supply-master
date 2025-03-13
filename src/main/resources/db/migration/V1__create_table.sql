CREATE TABLE suppliers
(
    id   uuid         NOT NULL,
    name varchar(255) NOT NULL,
    CONSTRAINT suppliers_pk PRIMARY KEY (id),
    CONSTRAINT suppliers_name_unique UNIQUE (name)
);

CREATE TABLE products
(
    id   uuid         NOT NULL,
    name varchar(255) NOT NULL,
    CONSTRAINT products_pk PRIMARY KEY (id),
    CONSTRAINT products_name_unique UNIQUE (name)
);

CREATE TABLE prices
(
    id           uuid           NOT NULL,
    supplier_id  uuid           NOT NULL,
    product_id   uuid           NOT NULL,
    price_per_kg DECIMAL(10, 2) NOT NULL,
    start_date   DATE           NOT NULL,
    end_date     DATE           NOT NULL,
    CONSTRAINT prices_pk PRIMARY KEY (id),
    CONSTRAINT prices_supplier_fk FOREIGN KEY (supplier_id) REFERENCES suppliers (id),
    CONSTRAINT prices_product_fk FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT prices_unique UNIQUE (supplier_id, product_id, start_date, end_date),
    CONSTRAINT prices_price_check CHECK (price_per_kg > 0)
);

CREATE TABLE shipments
(
    id            uuid NOT NULL,
    supplier_id   uuid NOT NULL,
    shipment_date DATE NOT NULL,
    CONSTRAINT shipments_pk PRIMARY KEY (id),
    CONSTRAINT shipments_supplier_fk FOREIGN KEY (supplier_id) REFERENCES suppliers (id),
    CONSTRAINT shipments_date_check CHECK (shipment_date <= CURRENT_DATE)
);

CREATE TABLE shipment_items
(
    shipment_id  uuid           NOT NULL,
    product_id   uuid           NOT NULL,
    weight_kg    DECIMAL(10, 2) NOT NULL,
    price_per_kg DECIMAL(10, 2) NOT NULL,
    total_price  DECIMAL(10, 2) not null,
    CONSTRAINT shipment_items_pk PRIMARY KEY (shipment_id, product_id),
    CONSTRAINT shipment_items_shipment_fk FOREIGN KEY (shipment_id) REFERENCES shipments (id),
    CONSTRAINT shipment_items_product_fk FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT shipment_items_weight_check CHECK (weight_kg > 0),
    CONSTRAINT shipment_items_price_check CHECK (price_per_kg > 0)
);