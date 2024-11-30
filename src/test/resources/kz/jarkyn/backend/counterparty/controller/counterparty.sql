INSERT INTO warehouse (id, name)
VALUES ('523961a7-696d-4779-8bb0-fd327feaecf3', 'Кенжина'),
       ('d1da1441-6598-4511-bc82-8fc06602e373', 'Барыс');

INSERT INTO counterparty (id, name)
VALUES ('43375a1e-1c91-46e5-9a10-a14200427fe9', 'Оркен Алматы'),
       ('1d468c04-6360-43e5-9d51-7771e9d9dcff', 'Заманбек Жетысай');

INSERT INTO customer (id, phone_number, shipping_address, discount)
VALUES ('43375a1e-1c91-46e5-9a10-a14200427fe9', '+7(775)457-41-17', '', 4),
       ('1d468c04-6360-43e5-9d51-7771e9d9dcff', '+7(707)145-14-75', '', 3);

INSERT INTO account (id, counterparty_id, name, balance, bank, giro)
VALUES ('054935c1-1369-4aa1-9c55-6f76e68a0dfb', '43375a1e-1c91-46e5-9a10-a14200427fe9',
        null, 0, null, null),
       ('9052abdd-7767-4961-a17b-035993f7570b', '1d468c04-6360-43e5-9d51-7771e9d9dcff',
        null, 0, null, null);


