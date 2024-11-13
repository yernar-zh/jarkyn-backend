INSERT INTO USERS (id, name, phone_number, role, auth_token)
VALUES ('fcf0d1be-0679-4f2b-bbb6-a72fb76cfaad', 'Оркен Алматы', '+77026705556',
        'CUSTOMER', null);

INSERT INTO counterparty (id, name)
VALUES ('842f873a-4732-4c0e-842d-e88a5ed5221e',
        'Розничный покупатель'),
       ('85430395-5e44-40c0-81f7-327d15368959',
        'Оркен Алматы');

INSERT INTO customer (id, user_id, discount, phone_number, shipping_address)
VALUES ('842f873a-4732-4c0e-842d-e88a5ed5221e',
        null, 0,
        null, 'Самовывоз'),
       ('85430395-5e44-40c0-81f7-327d15368959',
        'fcf0d1be-0679-4f2b-bbb6-a72fb76cfaad', 10,
        '+77026705556', 'Самовывоз');

INSERT INTO account (id, counterparty_id, balance, bank, giro, name)
VALUES ('0b956c5e-3e9a-4c59-91c1-cbf59a20f60d',
        'e9aedac1-e881-4591-a4df-0fab69deb885', 0, null,
        '+7 775 216 6661', 'Kaspi Ернар'),
       ('8440789c-ebfe-46f7-833f-6253a2f5e571',
        '842f873a-4732-4c0e-842d-e88a5ed5221e', 0, null,
        null, null),
       ('9f5125e0-2352-4c58-bd54-96b86abf37f2',
        '85430395-5e44-40c0-81f7-327d15368959', 0, null,
        null, null);

INSERT INTO warehouse (id, name)
VALUES ('523961a7-696d-4779-8bb0-fd327feaecf3', 'Кенжина');


