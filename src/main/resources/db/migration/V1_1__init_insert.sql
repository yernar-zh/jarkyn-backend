INSERT INTO public.party (id, name, archived, created_at, last_modified_at)
VALUES ('c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515', 'ИП Жырқын', false, now(), now());

INSERT INTO public.organization (id)
VALUES ('c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515');

INSERT INTO public.users (id, counterparty_id, phone_number, name, auth_token, role, archived,
                          created_at, last_modified_at)
VALUES ('376d6b4e-746b-4ee6-9e6c-8c8223643a7a', null,
        null, 'Система', null, 'SYSTEM', false,
        now(), now()),
       ('f57382c9-35a1-4b64-b3f0-f172489dc90a', null,
        '+77752166661', 'Ернар', null, 'ADMIN', false,
        now(), now());

INSERT INTO public.currency (id, name, code, archived, created_at, last_modified_at)
VALUES ('559109ea-f824-476d-8fa4-9990e53880ff', 'Тенге', 'KZT', false, now(), now()),
       ('e6a3c207-a358-47bf-ac18-2d09973f3807', 'Юань', 'CNY', false, now(), now()),
       ('24f15639-67da-4df4-aab4-56b85c872c3b', 'Доллар', 'USD', false, now(), now());

INSERT INTO public.document_type (id, name, code, archived, created_at, last_modified_at)
VALUES ('c837fc94-4cd4-4918-a38c-9bd9dd9c3271', 'Продажа', 'SALE', false, now(), now()),
       ('3873fc3e-58da-4ecc-81a8-70728d8d3297', 'Приемка', 'SUPPLY', false, now(), now()),
       ('b112027c-bf9b-45e4-bcff-74f74b27cff0', 'Входящий платеж', 'PAYMENT_IN', false, now(), now()),
       ('9b755b4e-61fa-457d-855d-6b231d3bfed2', 'Исходящий платеж', 'PAYMENT_OUT', false, now(), now());

INSERT INTO public.coverage (id, name, code, archived, created_at, last_modified_at)
VALUES ('6f5d4f59-f10e-42f6-8995-6a67d47e4e3d', 'Полное', 'FULL', false, now(), now()),
       ('c8f52b6a-8a43-4398-9d33-68b2d9b719a4', 'Частичное', 'PARTIAL', false, now(), now()),
       ('a8b1cf92-2894-41b4-94f5-0b9ad8e1d531', 'Отсутствует', 'NONE', false, now(), now());

INSERT INTO public.item_of_expenditure (id, name, code, archived, created_at, last_modified_at)
VALUES ('ac03d34e-2274-4f35-8939-46f17ddc05ed', 'Закупка товаров', 'SUPPLY', false, now(), now()),
       ('f18c785e-9bd9-4446-81f8-c3b64c474cf4', 'Возврат', 'REFUND', false, now(), now()),
       ('3a50a533-63a7-49e8-b1f1-f130b7b16822', 'Операционные расходы', 'OPEX', false, now(), now()),
       ('4bcf1be9-a45b-4585-ab94-39d7452b7e85', 'Дивиденд', 'DIVIDEND', false, now(), now()),
       ('f8ce8a87-947c-4818-b404-9de519f5125e', 'Зарплата', 'SALARY', false, now(), now()),
       ('25fa0cb2-a4d2-4035-9e8c-0c3b639caa4c', 'Хозяйственные расходы', 'HOUSEHOLD', false, now(), now());
