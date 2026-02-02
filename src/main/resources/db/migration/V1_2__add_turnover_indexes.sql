CREATE INDEX idx_turnover_warehouse_good_moment ON
    turnover (warehouse_id, good_id, moment);
CREATE INDEX idx_turnover_good_moment ON
    turnover (good_id, moment);
CREATE INDEX idx_turnover_warehouse_moment ON
    turnover (warehouse_id, moment);
CREATE INDEX idx_turnover_moment ON
    turnover (moment);
