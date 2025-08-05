CREATE TABLE account
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    archived         BOOLEAN,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    organization_id  UUID,
    counterparty_id  UUID,
    bank             VARCHAR(255),
    giro             VARCHAR(255),
    currency_id      UUID,
    CONSTRAINT pk_account PRIMARY KEY (id)
);

CREATE TABLE attribute
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    archived         BOOLEAN,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    group_id         UUID,
    position         INTEGER,
    CONSTRAINT pk_attribute PRIMARY KEY (id)
);

CREATE TABLE attribute_group
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    archived         BOOLEAN,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    position         INTEGER,
    CONSTRAINT pk_attribute_group PRIMARY KEY (id)
);

CREATE TABLE audit
(
    id                UUID NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at  TIMESTAMP WITHOUT TIME ZONE,
    moment            TIMESTAMP WITHOUT TIME ZONE,
    user_id           UUID,
    related_entity_id UUID,
    entity_id         UUID,
    entity_name       VARCHAR(255),
    action            VARCHAR(255),
    field_name        VARCHAR(255),
    field_value       VARCHAR(255),
    CONSTRAINT pk_audit PRIMARY KEY (id)
);

CREATE TABLE cash_flow
(
    id               UUID NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    document_id      UUID,
    account_id       UUID,
    moment           TIMESTAMP WITHOUT TIME ZONE,
    balance          DECIMAL,
    amount           DECIMAL,
    CONSTRAINT pk_cash_flow PRIMARY KEY (id)
);

CREATE TABLE counterparty
(
    id               UUID NOT NULL,
    phone_number     VARCHAR(255),
    shipping_address VARCHAR(255),
    discount         INTEGER,
    CONSTRAINT pk_counterparty PRIMARY KEY (id)
);

CREATE TABLE coverage
(
    id               UUID NOT NULL,
    code             VARCHAR(255),
    name             VARCHAR(255),
    archived         BOOLEAN,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_coverage PRIMARY KEY (id)
);

CREATE TABLE currency
(
    id               UUID NOT NULL,
    code             VARCHAR(255),
    name             VARCHAR(255),
    archived         BOOLEAN,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_currency PRIMARY KEY (id)
);

CREATE TABLE document
(
    id               UUID NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    type_id          UUID,
    organization_id  UUID,
    warehouse_id     UUID,
    account_id       UUID,
    counterparty_id  UUID,
    name             VARCHAR(255),
    moment           TIMESTAMP WITHOUT TIME ZONE,
    currency_id      UUID,
    exchange_rate    DECIMAL,
    amount           DECIMAL,
    comment          VARCHAR(255),
    deleted          BOOLEAN,
    commited         BOOLEAN,
    CONSTRAINT pk_document PRIMARY KEY (id)
);

CREATE TABLE document_type
(
    id               UUID NOT NULL,
    code             VARCHAR(255),
    name             VARCHAR(255),
    archived         BOOLEAN,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_document_type PRIMARY KEY (id)
);

CREATE TABLE good
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    archived         BOOLEAN,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    group_id         UUID,
    image_id         UUID,
    weight           DECIMAL,
    minimum_price    INTEGER,
    CONSTRAINT pk_good PRIMARY KEY (id)
);

CREATE TABLE good_attribute
(
    id               UUID NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    good_id          UUID,
    attribute_id     UUID,
    CONSTRAINT pk_good_attribute PRIMARY KEY (id)
);

CREATE TABLE groups
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    archived         BOOLEAN,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    parent_id        UUID,
    position         INTEGER,
    CONSTRAINT pk_groups PRIMARY KEY (id)
);

CREATE TABLE image
(
    id                UUID NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at  TIMESTAMP WITHOUT TIME ZONE,
    original_file_id  UUID,
    medium_file_id    UUID,
    thumbnail_file_id UUID,
    CONSTRAINT pk_image PRIMARY KEY (id)
);

CREATE TABLE item
(
    id               UUID NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    document_id      UUID,
    good_id          UUID,
    quantity         INTEGER,
    price            DECIMAL,
    position         INTEGER,
    CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE item_of_expenditure
(
    id               UUID NOT NULL,
    code             VARCHAR(255),
    name             VARCHAR(255),
    archived         BOOLEAN,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_item_of_expenditure PRIMARY KEY (id)
);

CREATE TABLE organization
(
    id UUID NOT NULL,
    CONSTRAINT pk_organization PRIMARY KEY (id)
);

CREATE TABLE paid_document
(
    id               UUID NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    payment_id       UUID,
    document_id      UUID,
    amount           DECIMAL,
    CONSTRAINT pk_paid_document PRIMARY KEY (id)
);

CREATE TABLE party
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    archived         BOOLEAN,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_party PRIMARY KEY (id)
);

CREATE TABLE payment_in
(
    id             UUID NOT NULL,
    receipt_number VARCHAR(255),
    CONSTRAINT pk_payment_in PRIMARY KEY (id)
);

CREATE TABLE payment_out
(
    id                     UUID NOT NULL,
    receipt_number         VARCHAR(255),
    item_of_expenditure_id UUID,
    purpose                VARCHAR(255),
    CONSTRAINT pk_payment_out PRIMARY KEY (id)
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
    id               UUID NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    good_id          UUID,
    quantity         INTEGER,
    val              DECIMAL,
    CONSTRAINT pk_selling_price PRIMARY KEY (id)
);

CREATE TABLE supply
(
    id UUID NOT NULL,
    CONSTRAINT pk_supply PRIMARY KEY (id)
);

CREATE TABLE turnover
(
    id                        UUID NOT NULL,
    created_at                TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at          TIMESTAMP WITHOUT TIME ZONE,
    document_id               UUID,
    good_id                   UUID,
    warehouse_id              UUID,
    moment                    TIMESTAMP WITHOUT TIME ZONE,
    quantity                  INTEGER,
    cost_price_per_unit       DECIMAL,
    remain                    INTEGER,
    last_inflow_id            UUID,
    last_inflow_used_quantity INTEGER,
    CONSTRAINT pk_turnover PRIMARY KEY (id)
);

CREATE TABLE users
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    archived         BOOLEAN,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    counterparty_id  UUID,
    phone_number     VARCHAR(255),
    auth_token       VARCHAR(255),
    role             VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE warehouse
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    archived         BOOLEAN,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_warehouse PRIMARY KEY (id)
);

ALTER TABLE account
    ADD CONSTRAINT FK_ACCOUNT_ON_COUNTERPARTY FOREIGN KEY (counterparty_id) REFERENCES counterparty (id);

ALTER TABLE account
    ADD CONSTRAINT FK_ACCOUNT_ON_CURRENCY FOREIGN KEY (currency_id) REFERENCES currency (id);

ALTER TABLE account
    ADD CONSTRAINT FK_ACCOUNT_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id);

ALTER TABLE attribute
    ADD CONSTRAINT FK_ATTRIBUTE_ON_GROUP FOREIGN KEY (group_id) REFERENCES attribute_group (id);

ALTER TABLE audit
    ADD CONSTRAINT FK_AUDIT_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE cash_flow
    ADD CONSTRAINT FK_CASH_FLOW_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE cash_flow
    ADD CONSTRAINT FK_CASH_FLOW_ON_DOCUMENT FOREIGN KEY (document_id) REFERENCES document (id);

ALTER TABLE counterparty
    ADD CONSTRAINT FK_COUNTERPARTY_ON_ID FOREIGN KEY (id) REFERENCES party (id);

ALTER TABLE document
    ADD CONSTRAINT FK_DOCUMENT_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE document
    ADD CONSTRAINT FK_DOCUMENT_ON_COUNTERPARTY FOREIGN KEY (counterparty_id) REFERENCES counterparty (id);

ALTER TABLE document
    ADD CONSTRAINT FK_DOCUMENT_ON_CURRENCY FOREIGN KEY (currency_id) REFERENCES currency (id);

ALTER TABLE document
    ADD CONSTRAINT FK_DOCUMENT_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id);

ALTER TABLE document
    ADD CONSTRAINT FK_DOCUMENT_ON_TYPE FOREIGN KEY (type_id) REFERENCES document_type (id);

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
    ADD CONSTRAINT FK_ORGANIZATION_ON_ID FOREIGN KEY (id) REFERENCES party (id);

ALTER TABLE paid_document
    ADD CONSTRAINT FK_PAID_DOCUMENT_ON_DOCUMENT FOREIGN KEY (document_id) REFERENCES document (id);

ALTER TABLE paid_document
    ADD CONSTRAINT FK_PAID_DOCUMENT_ON_PAYMENT FOREIGN KEY (payment_id) REFERENCES document (id);

ALTER TABLE payment_in
    ADD CONSTRAINT FK_PAYMENT_IN_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE payment_out
    ADD CONSTRAINT FK_PAYMENT_OUT_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE payment_out
    ADD CONSTRAINT FK_PAYMENT_OUT_ON_ITEM_OF_EXPENDITURE FOREIGN KEY (item_of_expenditure_id) REFERENCES item_of_expenditure (id);

ALTER TABLE sale
    ADD CONSTRAINT FK_SALE_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE selling_price
    ADD CONSTRAINT FK_SELLING_PRICE_ON_GOOD FOREIGN KEY (good_id) REFERENCES good (id);

ALTER TABLE supply
    ADD CONSTRAINT FK_SUPPLY_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE turnover
    ADD CONSTRAINT FK_TURNOVER_ON_DOCUMENT FOREIGN KEY (document_id) REFERENCES document (id);

ALTER TABLE turnover
    ADD CONSTRAINT FK_TURNOVER_ON_GOOD FOREIGN KEY (good_id) REFERENCES good (id);

ALTER TABLE turnover
    ADD CONSTRAINT FK_TURNOVER_ON_LAST_INFLOW FOREIGN KEY (last_inflow_id) REFERENCES turnover (id);

ALTER TABLE turnover
    ADD CONSTRAINT FK_TURNOVER_ON_WAREHOUSE FOREIGN KEY (warehouse_id) REFERENCES warehouse (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_COUNTERPARTY FOREIGN KEY (counterparty_id) REFERENCES party (id);