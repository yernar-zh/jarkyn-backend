CREATE TABLE account
(
    id              UUID NOT NULL,
    counterparty_id UUID,
    name            VARCHAR(255),
    balance         DECIMAL,
    bank            VARCHAR(255),
    giro            VARCHAR(255),
    CONSTRAINT pk_account PRIMARY KEY (id)
);

CREATE TABLE attribute
(
    id       UUID NOT NULL,
    group_id UUID,
    name     VARCHAR(255),
    position INTEGER,
    CONSTRAINT pk_attribute PRIMARY KEY (id)
);

CREATE TABLE attribute_group
(
    id       UUID NOT NULL,
    name     VARCHAR(255),
    position INTEGER,
    CONSTRAINT pk_attribute_group PRIMARY KEY (id)
);

CREATE TABLE audit
(
    id               UUID NOT NULL,
    user_id          UUID,
    moment           TIMESTAMP WITHOUT TIME ZONE,
    entity_id        UUID,
    entity_parent_id UUID,
    field_name       VARCHAR(255),
    field_value      VARCHAR(255),
    CONSTRAINT pk_audit PRIMARY KEY (id)
);

CREATE TABLE cash_flow
(
    id          UUID NOT NULL,
    account_id  UUID,
    document_id UUID,
    amount      DECIMAL,
    CONSTRAINT pk_cash_flow PRIMARY KEY (id)
);

CREATE TABLE counterparty
(
    id   UUID NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_counterparty PRIMARY KEY (id)
);

CREATE TABLE customer
(
    id               UUID NOT NULL,
    phone_number     VARCHAR(255),
    shipping_address VARCHAR(255),
    discount         INTEGER,
    CONSTRAINT pk_customer PRIMARY KEY (id)
);

CREATE TABLE document
(
    id              UUID NOT NULL,
    warehouse_id    UUID,
    counterparty_id UUID,
    name            VARCHAR(255),
    moment          TIMESTAMP WITHOUT TIME ZONE,
    amount          DECIMAL,
    comment         VARCHAR(255),
    deleted         BOOLEAN,
    CONSTRAINT pk_document PRIMARY KEY (id)
);

CREATE TABLE good
(
    id            UUID NOT NULL,
    name          VARCHAR(255),
    group_id      UUID,
    image_id      UUID,
    minimum_price INTEGER,
    archived      BOOLEAN,
    CONSTRAINT pk_good PRIMARY KEY (id)
);

CREATE TABLE good_attribute
(
    id           UUID NOT NULL,
    good_id      UUID,
    attribute_id UUID,
    CONSTRAINT pk_good_attribute PRIMARY KEY (id)
);

CREATE TABLE groups
(
    id        UUID NOT NULL,
    name      VARCHAR(255),
    parent_id UUID,
    position  INTEGER,
    CONSTRAINT pk_groups PRIMARY KEY (id)
);

CREATE TABLE image
(
    id UUID NOT NULL,
    CONSTRAINT pk_image PRIMARY KEY (id)
);

CREATE TABLE item
(
    id          UUID NOT NULL,
    document_id UUID,
    good_id     UUID,
    quantity    INTEGER,
    price       DECIMAL,
    position    INTEGER,
    CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE organization
(
    id UUID NOT NULL,
    CONSTRAINT pk_organization PRIMARY KEY (id)
);

CREATE TABLE payment_in
(
    id         UUID NOT NULL,
    account_id UUID,
    state      VARCHAR(255),
    CONSTRAINT pk_payment_in PRIMARY KEY (id)
);

CREATE TABLE payment_in_purpose
(
    id          UUID NOT NULL,
    payment_id  UUID,
    document_id UUID,
    amount      DECIMAL,
    CONSTRAINT pk_payment_in_purpose PRIMARY KEY (id)
);

CREATE TABLE payment_out
(
    id         UUID NOT NULL,
    account_id UUID,
    state      VARCHAR(255),
    CONSTRAINT pk_payment_out PRIMARY KEY (id)
);

CREATE TABLE payment_out_purpose
(
    id          UUID NOT NULL,
    payment_out UUID,
    document_id UUID,
    amount      DECIMAL,
    CONSTRAINT pk_payment_out_purpose PRIMARY KEY (id)
);

CREATE TABLE sale
(
    id              UUID NOT NULL,
    shipment_moment TIMESTAMP WITHOUT TIME ZONE,
    state           VARCHAR(255),
    CONSTRAINT pk_sale PRIMARY KEY (id)
);

CREATE TABLE selling_price
(
    id       UUID NOT NULL,
    good_id  UUID,
    quantity INTEGER,
    val      DECIMAL,
    CONSTRAINT pk_selling_price PRIMARY KEY (id)
);

CREATE TABLE supplier
(
    id UUID NOT NULL,
    CONSTRAINT pk_supplier PRIMARY KEY (id)
);

CREATE TABLE supply
(
    id            UUID NOT NULL,
    exchange_rate INTEGER,
    state         VARCHAR(255),
    CONSTRAINT pk_supply PRIMARY KEY (id)
);

CREATE TABLE turnover
(
    id          UUID NOT NULL,
    document_id UUID,
    good_id     UUID,
    moment      TIMESTAMP WITHOUT TIME ZONE,
    quantity    INTEGER,
    remain      INTEGER,
    cost_price  DECIMAL,
    CONSTRAINT pk_turnover PRIMARY KEY (id)
);

CREATE TABLE users
(
    id              UUID NOT NULL,
    counterparty_id UUID,
    phone_number    VARCHAR(255),
    name            VARCHAR(255),
    auth_token      VARCHAR(255),
    role            VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE warehouse
(
    id   UUID NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_warehouse PRIMARY KEY (id)
);

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
    ADD CONSTRAINT FK_DOCUMENT_ON_COUNTERPARTY FOREIGN KEY (counterparty_id) REFERENCES counterparty (id);

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

ALTER TABLE payment_in_purpose
    ADD CONSTRAINT FK_PAYMENT_IN_PURPOSE_ON_DOCUMENT FOREIGN KEY (document_id) REFERENCES document (id);

ALTER TABLE payment_in_purpose
    ADD CONSTRAINT FK_PAYMENT_IN_PURPOSE_ON_PAYMENT FOREIGN KEY (payment_id) REFERENCES payment_in (id);

ALTER TABLE payment_out
    ADD CONSTRAINT FK_PAYMENT_OUT_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE payment_out
    ADD CONSTRAINT FK_PAYMENT_OUT_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE payment_out_purpose
    ADD CONSTRAINT FK_PAYMENT_OUT_PURPOSE_ON_DOCUMENT FOREIGN KEY (document_id) REFERENCES document (id);

ALTER TABLE payment_out_purpose
    ADD CONSTRAINT FK_PAYMENT_OUT_PURPOSE_ON_PAYMENT_OUT FOREIGN KEY (payment_out) REFERENCES payment_out (id);

ALTER TABLE sale
    ADD CONSTRAINT FK_SALE_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE selling_price
    ADD CONSTRAINT FK_SELLING_PRICE_ON_GOOD FOREIGN KEY (good_id) REFERENCES good (id);

ALTER TABLE supplier
    ADD CONSTRAINT FK_SUPPLIER_ON_ID FOREIGN KEY (id) REFERENCES counterparty (id);

ALTER TABLE supply
    ADD CONSTRAINT FK_SUPPLY_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE turnover
    ADD CONSTRAINT FK_TURNOVER_ON_DOCUMENT FOREIGN KEY (document_id) REFERENCES document (id);

ALTER TABLE turnover
    ADD CONSTRAINT FK_TURNOVER_ON_GOOD FOREIGN KEY (good_id) REFERENCES good (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_COUNTERPARTY FOREIGN KEY (counterparty_id) REFERENCES counterparty (id);