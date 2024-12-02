INSERT INTO public.counterparty (id, name)
VALUES ('c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515', 'ИП Жырқын');
INSERT INTO public.organization (id)
VALUES ('c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515');
INSERT INTO public.users (id, counterparty_id, phone_number, name, auth_token, role)
VALUES ('376d6b4e-746b-4ee6-9e6c-8c8223643a7a', null,
        null, 'Система', null, 'SYSTEM'),
       ('f57382c9-35a1-4b64-b3f0-f172489dc90a', null,
        '+77752166661', 'Ернар', null, 'ADMIN');
