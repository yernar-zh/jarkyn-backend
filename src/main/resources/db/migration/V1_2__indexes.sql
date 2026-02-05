CREATE INDEX idx_turnover_warehouse_good_moment ON
    turnover (warehouse_id, good_id, moment);

CREATE INDEX idx_turnover_good_moment ON
    turnover (good_id, moment);

CREATE INDEX idx_turnover_warehouse_moment ON
    turnover (warehouse_id, moment);

CREATE INDEX idx_turnover_moment ON
    turnover (moment);

CREATE INDEX idx_account_counterparty_id ON
    account (counterparty_id);

CREATE INDEX idx_document_type_id ON
    document (type_id);

CREATE INDEX idx_item_document_id ON
    item (document_id);

CREATE INDEX idx_document_counterparty_id ON
    document (counterparty_id);

CREATE INDEX idx_party_name ON
    party (name);

CREATE INDEX idx_audit_entity_related_field_created_desc ON
    audit (entity_id, related_entity_id, field_name, created_at DESC);
