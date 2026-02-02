INSERT INTO public.party (id, name, archived, created_at, last_modified_at)
VALUES ('70167f17-b7b7-4ce2-b2ad-2c8da4976cff', 'ИП Жаркын', false, '2025-09-19 10:30:01.636575',
        '2025-09-19 10:30:01.636575');
INSERT INTO public.organization (id)
VALUES ('70167f17-b7b7-4ce2-b2ad-2c8da4976cff');

INSERT INTO public.warehouse (id, name, archived, created_at, last_modified_at)
VALUES ('7cfa1b93-b092-4d39-beb3-58eac6a8f800', 'Кенжина', false, '2025-09-19 12:19:23.589798',
        '2025-09-19 12:19:23.589798');
INSERT INTO public.warehouse (id, name, archived, created_at, last_modified_at)
VALUES ('fa48a071-2be2-4055-a60f-31255dc46757', 'Барыс', false, '2025-09-19 12:19:30.196604',
        '2025-09-19 12:19:30.196604');

INSERT INTO public.account (id, name, archived, created_at, last_modified_at, organization_id, counterparty_id, bank,
                            giro, currency_id)
VALUES ('83252f10-d198-441e-9fbc-d225d4a2a2b2', 'Каспи Ернар Ж', false, '2025-09-19 12:20:26.419246',
        '2025-09-19 12:20:26.419246', '70167f17-b7b7-4ce2-b2ad-2c8da4976cff', null, 'Каспи ', 'Ернар Ж +7 775 216 6661',
        '559109ea-f824-476d-8fa4-9990e53880ff');
INSERT INTO public.account (id, name, archived, created_at, last_modified_at, organization_id, counterparty_id, bank,
                            giro, currency_id)
VALUES ('61528e17-1857-45f0-9773-5e99e2fb0bb3', 'Наличными Тенге', false, '2025-09-19 10:30:31.681333',
        '2025-09-19 12:21:25.613073', '70167f17-b7b7-4ce2-b2ad-2c8da4976cff', null, '', '',
        '559109ea-f824-476d-8fa4-9990e53880ff');
INSERT INTO public.account (id, name, archived, created_at, last_modified_at, organization_id, counterparty_id, bank,
                            giro, currency_id)
VALUES ('d83f7353-1ffe-40c8-a4d6-d4b32726e155', 'Наличными Юань', false, '2025-09-19 12:21:45.778511',
        '2025-09-19 12:21:45.778511', '70167f17-b7b7-4ce2-b2ad-2c8da4976cff', null, '', '',
        'e6a3c207-a358-47bf-ac18-2d09973f3807');
INSERT INTO public.account (id, name, archived, created_at, last_modified_at, organization_id, counterparty_id, bank,
                            giro, currency_id)
VALUES ('b2985ddb-d3cb-4b70-88ae-2250c5d64dcd', 'Наличными Доллар', false, '2025-09-19 12:23:03.367596',
        '2025-09-19 12:23:03.367596', '70167f17-b7b7-4ce2-b2ad-2c8da4976cff', null, '', '',
        '24f15639-67da-4df4-aab4-56b85c872c3b');