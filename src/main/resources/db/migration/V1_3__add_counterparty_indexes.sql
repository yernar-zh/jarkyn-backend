CREATE INDEX idx_account_counterparty_id ON
    account (counterparty_id);

CREATE INDEX idx_party_name ON
    party (name);

CREATE INDEX idx_party_archived ON
    party (archived);

CREATE INDEX idx_counterparty_phone_number ON
    counterparty (phone_number);
