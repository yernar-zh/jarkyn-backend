CREATE TABLE account
(
    id              UUID PRIMARY KEY,
    counterparty_id UUID,
    name            VARCHAR(255),
    balance         INT,
    bank            VARCHAR(255),
    giro            VARCHAR(255)
);

CREATE TABLE attribute
(
    id       UUID PRIMARY KEY,
    group_id UUID,
    name     VARCHAR(255),
    position INT
);

CREATE TABLE attribute_group
(
    id       UUID PRIMARY KEY,
    name     VARCHAR(255),
    position INT
);

CREATE TABLE audit
(
    id               UUID PRIMARY KEY,
    user_id          UUID,
    moment           TIMESTAMP,
    entity_id        UUID,
    entity_parent_id UUID,
    field_name       VARCHAR(255),
    field_value      VARCHAR(255)
);

CREATE TABLE cash_flow
(
    id          UUID PRIMARY KEY,
    account_id  UUID,
    document_id UUID,
    amount      INT
);

CREATE TABLE counterparty
(
    id   UUID PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE customer
(
    id               UUID PRIMARY KEY,
    phone_number     VARCHAR(255),
    shipping_address VARCHAR(255),
    discount         INT
);

CREATE TABLE document
(
    id           UUID PRIMARY KEY,
    warehouse_id UUID,
    customer_id  UUID,
    name         VARCHAR(255),
    moment       TIMESTAMP,
    amount       INT,
    comment      VARCHAR(255),
    deleted      BOOLEAN
);

CREATE TABLE good
(
    id            UUID PRIMARY KEY,
    name          VARCHAR(255),
    group_id      UUID,
    image_id      UUID,
    minimum_price INT,
    archived      BOOLEAN
);

CREATE TABLE good_attribute
(
    id           UUID PRIMARY KEY,
    good_id      UUID,
    attribute_id UUID
);

CREATE TABLE groups
(
    id        UUID PRIMARY KEY,
    name      VARCHAR(255),
    parent_id UUID,
    position  INT
);

CREATE TABLE image
(
    id UUID PRIMARY KEY
);

CREATE TABLE item
(
    id          UUID PRIMARY KEY,
    document_id UUID,
    good_id     UUID,
    quantity    INT,
    price       INT,
    position    INT
);

CREATE TABLE organization
(
    id UUID PRIMARY KEY
);

CREATE TABLE payment_in
(
    id         UUID PRIMARY KEY,
    account_id UUID,
    state      VARCHAR(255)
);

CREATE TABLE payment_purpose
(
    id         UUID PRIMARY KEY,
    payment_id UUID,
    sale_id    UUID,
    amount     INT
);


CREATE TABLE sale
(
    id              UUID PRIMARY KEY,
    shipment_moment TIMESTAMP,
    state           VARCHAR(255)
);

CREATE TABLE selling_price
(
    id       UUID PRIMARY KEY,
    good_id  UUID,
    quantity INT,
    val      INT
);

CREATE TABLE supplier
(
    id UUID PRIMARY KEY
);

CREATE TABLE turnover
(
    id          UUID PRIMARY KEY,
    document_id UUID,
    good_id     UUID,
    moment      TIMESTAMP,
    quantity    INT,
    cost_price  INT,
    remain      INT
);

CREATE TABLE users
(
    id              UUID PRIMARY KEY,
    counterparty_id UUID,
    phone_number    VARCHAR(255),
    name            VARCHAR(255),
    auth_token      VARCHAR(255),
    role            VARCHAR(255)
);

CREATE TABLE warehouse
(
    id   UUID PRIMARY KEY,
    name VARCHAR(255)
);

-- ADDING FOREIGN KEYS

ALTER TABLE account
    ADD CONSTRAINT FK_ACCOUNT_ON_COUNTERPARTY FOREIGN KEY (counterparty_id) REFERENCES counterparty (id);

ALTER TABLE attribute
    ADD CONSTRAINT FK_ATTRIBUTE_ON_GROUP FOREIGN KEY (group_id) REFERENCES attribute_group (id);

ALTER TABLE cash_flow
    ADD CONSTRAINT FK_CASH_FLOW_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE cash_flow
    ADD CONSTRAINT FK_CASH_FLOW_ON_DOCUMENT FOREIGN KEY (document_id) REFERENCES document (id);

ALTER TABLE customer
    ADD CONSTRAINT FK_CUSTOMER_ON_ID FOREIGN KEY (id) REFERENCES counterparty (id);

ALTER TABLE document
    ADD CONSTRAINT FK_DOCUMENT_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES counterparty (id);

ALTER TABLE document
    ADD CONSTRAINT FK_DOCUMENT_ON_WAREHOUSE FOREIGN KEY (warehouse_id) REFERENCES warehouse (id);

ALTER TABLE good_attribute
    ADD CONSTRAINT FK_GOOD_ATTRIBUTE_ON_ATTRIBUTE FOREIGN KEY (attribute_id) REFERENCES attribute (id);

ALTER TABLE good_attribute
    ADD CONSTRAINT FK_GOOD_ATTRIBUTE_ON_GOOD FOREIGN KEY (good_id) REFERENCES good (id);

ALTER TABLE good
    ADD CONSTRAINT FK_GOOD_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

ALTER TABLE good
    ADD CONSTRAINT FK_GOOD_ON_IMAGE FOREIGN KEY (image_id) REFERENCES image (id);

ALTER TABLE groups
    ADD CONSTRAINT FK_GROUPS_ON_PARENT FOREIGN KEY (parent_id) REFERENCES groups (id);

ALTER TABLE item
    ADD CONSTRAINT FK_ITEM_ON_DOCUMENT FOREIGN KEY (document_id) REFERENCES document (id);

ALTER TABLE item
    ADD CONSTRAINT FK_ITEM_ON_GOOD FOREIGN KEY (good_id) REFERENCES good (id);

ALTER TABLE organization
    ADD CONSTRAINT FK_ORGANIZATION_ON_ID FOREIGN KEY (id) REFERENCES counterparty (id);

ALTER TABLE payment_in
    ADD CONSTRAINT FK_PAYMENT_IN_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE payment_in
    ADD CONSTRAINT FK_PAYMENT_IN_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE payment_purpose
    ADD CONSTRAINT FK_PAYMENT_PURPOSE_ON_PAYMENT FOREIGN KEY (payment_id) REFERENCES payment_in (id);

ALTER TABLE payment_purpose
    ADD CONSTRAINT FK_PAYMENT_PURPOSE_ON_SALE FOREIGN KEY (sale_id) REFERENCES sale (id);

ALTER TABLE sale
    ADD CONSTRAINT FK_SALE_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE selling_price
    ADD CONSTRAINT FK_SELLING_PRICE_ON_GOOD FOREIGN KEY (good_id) REFERENCES good (id);

ALTER TABLE supplier
    ADD CONSTRAINT FK_SUPPLIER_ON_ID FOREIGN KEY (id) REFERENCES counterparty (id);

ALTER TABLE turnover
    ADD CONSTRAINT FK_TURNOVER_ON_DOCUMENT FOREIGN KEY (document_id) REFERENCES document (id);

ALTER TABLE turnover
    ADD CONSTRAINT FK_TURNOVER_ON_GOOD FOREIGN KEY (good_id) REFERENCES good (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_COUNTERPARTY FOREIGN KEY (counterparty_id) REFERENCES counterparty (id);
