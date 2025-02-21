INSERT INTO public.party (id, name, archived, created_at, last_modified_at)
VALUES ('c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515', 'ИП Жырқын', false, now(), now());
INSERT INTO public.organization (id)
VALUES ('c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515');
INSERT INTO public.users (id, counterparty_id, phone_number, name, auth_token, role, created_at, last_modified_at)
VALUES ('376d6b4e-746b-4ee6-9e6c-8c8223643a7a', null,
        null, 'Система', null, 'SYSTEM',
        now(), now()),
       ('f57382c9-35a1-4b64-b3f0-f172489dc90a', null,
        '+77752166661', 'Ернар', null, 'ADMIN',
        now(), now());
INSERT INTO public.currency (id, name, code, symbol, created_at, last_modified_at)
VALUES ('559109ea-f824-476d-8fa4-9990e53880ff', 'KZT', 'KZT', '₸', now(), now()),
       ('e6a3c207-a358-47bf-ac18-2d09973f3807', 'CNY', 'CNY', '¥', now(), now()),
       ('24f15639-67da-4df4-aab4-56b85c872c3b', 'USD', 'USD', '$', now(), now())