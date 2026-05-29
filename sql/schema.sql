-- next-car-dealership schema (idempotent — safe to run multiple times)

DROP TABLE IF EXISTS promotions, service_appts, sales, salespersons,
                     new_cars, used_cars, employees, clients, cars CASCADE;

DROP TYPE IF EXISTS car_type, employee_type, appointment_status CASCADE;

CREATE TYPE car_type AS ENUM ('NEW', 'USED');
CREATE TYPE employee_type AS ENUM ('SALESPERSON');
CREATE TYPE appointment_status AS ENUM ('PENDING', 'DONE');

CREATE TABLE cars (
    id          SERIAL PRIMARY KEY,
    type        car_type       NOT NULL,
    brand       VARCHAR(100)   NOT NULL,
    model       VARCHAR(100)   NOT NULL,
    year        INT            NOT NULL,
    price       DECIMAL(12, 2) NOT NULL,
    available   BOOLEAN        NOT NULL DEFAULT TRUE
);

CREATE TABLE new_cars (
    car_id           INT PRIMARY KEY REFERENCES cars(id) ON DELETE CASCADE,
    warranty_months  INT NOT NULL
);

CREATE TABLE used_cars (
    car_id            INT PRIMARY KEY REFERENCES cars(id) ON DELETE CASCADE,
    km                INT NOT NULL,
    previous_owners   INT NOT NULL
);

CREATE TABLE clients (
    id     SERIAL PRIMARY KEY,
    name   VARCHAR(200) NOT NULL,
    phone  VARCHAR(50),
    email  VARCHAR(200)
);

CREATE TABLE employees (
    id         SERIAL PRIMARY KEY,
    type       employee_type  NOT NULL,
    name       VARCHAR(200)   NOT NULL,
    salary     DECIMAL(12, 2) NOT NULL,
    hire_date  DATE           NOT NULL,
    active     BOOLEAN        NOT NULL DEFAULT TRUE
);

CREATE TABLE salespersons (
    employee_id      INT PRIMARY KEY REFERENCES employees(id) ON DELETE CASCADE,
    commission_rate  DECIMAL(5, 2) NOT NULL
);

CREATE TABLE sales (
    id              SERIAL PRIMARY KEY,
    car_id          INT            NOT NULL REFERENCES cars(id),
    client_id       INT            NOT NULL REFERENCES clients(id),
    salesperson_id  INT            NOT NULL REFERENCES employees(id),
    date            DATE           NOT NULL,
    final_price     DECIMAL(12, 2) NOT NULL,
    cancelled       BOOLEAN        NOT NULL DEFAULT FALSE
);

CREATE TABLE service_appts (
    id           SERIAL PRIMARY KEY,
    car_id       INT              NOT NULL REFERENCES cars(id),
    date         DATE             NOT NULL,
    description  TEXT             NOT NULL,
    status       appointment_status NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE promotions (
    id                SERIAL PRIMARY KEY,
    car_id            INT           NOT NULL REFERENCES cars(id),
    discount_percent  DECIMAL(5, 2) NOT NULL,
    active            BOOLEAN       NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_cars_brand ON cars(brand);
CREATE INDEX idx_cars_available ON cars(available);
CREATE INDEX idx_sales_client ON sales(client_id);
CREATE INDEX idx_sales_date ON sales(date);
CREATE INDEX idx_promotions_car_active ON promotions(car_id, active);
