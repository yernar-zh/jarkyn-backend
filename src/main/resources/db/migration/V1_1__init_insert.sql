INSERT INTO public.role (id, name, code, archived, created_at, last_modified_at)
VALUES ('82435749-721a-49ee-8402-579d507b21ff', 'Система', 'SYSTEM', false, now(), now()),
       ('c54cf84d-230d-48bf-87fa-172c0cc7e4d5', 'Владелец', 'OWNER', false, now(), now()),
       ('123b4cb7-d57d-48fd-8596-143bf994826e', 'Администратор', 'ADMINISTRATOR', false, now(), now()),
       ('60969353-3986-4d85-bfa1-04f91d375b19', 'Кладовщик ', 'STOREKEEPER', false, now(), now());

INSERT INTO public.users (id, name, phone_number, role_id, password_hash, archived, created_at, last_modified_at)
VALUES ('d8427d9f-4836-449f-8a6d-90d5d718c83f', 'Система', '+7 700 000 0000',
        '82435749-721a-49ee-8402-579d507b21ff', '', false, now(), now());

INSERT INTO public.session(id, user_id, refresh_token_hash, ip, user_agent, expires_at, revoked_at, version, created_at,
                           last_modified_at)
VALUES ('a1e286f1-f820-4cf9-a9cc-4f43687681fe', 'd8427d9f-4836-449f-8a6d-90d5d718c83f', '',
        '', '', now(), now(), 0, now(), now());

INSERT INTO public.currency (id, name, code, archived, created_at, last_modified_at)
VALUES ('559109ea-f824-476d-8fa4-9990e53880ff', 'Тенге', 'KZT', false, now(), now()),
       ('e6a3c207-a358-47bf-ac18-2d09973f3807', 'Юань', 'CNY', false, now(), now()),
       ('24f15639-67da-4df4-aab4-56b85c872c3b', 'Доллар', 'USD', false, now(), now());

INSERT INTO public.document_type (id, name, code, archived, created_at, last_modified_at)
VALUES ('c837fc94-4cd4-4918-a38c-9bd9dd9c3271', 'Продажа', 'SALE', false, now(), now()),
       ('3873fc3e-58da-4ecc-81a8-70728d8d3297', 'Приемка', 'SUPPLY', false, now(), now()),
       ('b112027c-bf9b-45e4-bcff-74f74b27cff0', 'Входящий платеж', 'PAYMENT_IN', false, now(), now()),
       ('9b755b4e-61fa-457d-855d-6b231d3bfed2', 'Исходящий платеж', 'PAYMENT_OUT', false, now(), now()),
       ('b61820c8-6e47-46c9-b2d3-790e7d8c8653', 'Расходы', 'EXPENSE', false, now(), now());


INSERT INTO public.coverage (id, name, code, archived, created_at, last_modified_at)
VALUES ('6f5d4f59-f10e-42f6-8995-6a67d47e4e3d', 'Полное', 'FULL', false, now(), now()),
       ('c8f52b6a-8a43-4398-9d33-68b2d9b719a4', 'Частичное', 'PARTIAL', false, now(), now()),
       ('a8b1cf92-2894-41b4-94f5-0b9ad8e1d531', 'Отсутствует', 'NONE', false, now(), now());

INSERT INTO public.item_of_expenditure (id, name, code, archived, created_at, last_modified_at)
VALUES ('ac03d34e-2274-4f35-8939-46f17ddc05ed', 'Таможенные расходы', 'CUSTOMS', false, now(), now()),
       ('f18c785e-9bd9-4446-81f8-c3b64c474cf4', 'Расходы на доставку', 'DELIVERY', false, now(), now()),
       ('f8ce8a87-947c-4818-b404-9de519f5125e', 'Зарплата', 'SALARY', false, now(), now()),
       ('25fa0cb2-a4d2-4035-9e8c-0c3b639caa4c', 'Хозяйственные расходы', 'HOUSEHOLD', false, now(), now());
