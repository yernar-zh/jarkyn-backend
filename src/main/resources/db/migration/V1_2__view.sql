CREATE MATERIALIZED VIEW IF NOT EXISTS customer_summary_view AS
SELECT ctr.id                                            AS id,
       to_tsvector('russian', remove_symbols(
               MIN(cpy.name) || ' ' || MIN(ctr.phone_number)
                              ))                         AS search,
       CAST(COALESCE(SUM(act.balance), 0) as INTEGER)    AS balance,
       MIN(sle_doc.moment)                               AS first_sale_moment,
       MAX(sle_doc.moment)                               AS last_sale_moment,
       CAST(COUNT(sle_doc.id) as INTEGER)                AS total_sale_count,
       CAST(COALESCE(SUM(sle_doc.amount), 0) as INTEGER) AS total_sale_amount
FROM customer ctr
         LEFT JOIN counterparty cpy ON ctr.id = cpy.id
         LEFT JOIN account act ON ctr.id = act.counterparty_id
         LEFT JOIN document sle_doc ON sle_doc.customer_id = sle_doc.id
         LEFT JOIN sale sle ON ctr.id = sle_doc.id
WHERE (sle_doc.id IS NULL OR sle.id IS NOT NULL)
GROUP BY ctr.id;