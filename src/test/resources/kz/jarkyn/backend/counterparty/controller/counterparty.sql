INSERT INTO warehouse (id, name)
VALUES ('523961a7-696d-4779-8bb0-fd327feaecf3', 'Кенжина'),
       ('d1da1441-6598-4511-bc82-8fc06602e373', 'Барыс');

INSERT INTO counterparty (id, name)
VALUES ('43375a1e-1c91-46e5-9a10-a14200427fe9', 'Оркен Алматы'),
       ('1d468c04-6360-43e5-9d51-7771e9d9dcff', 'Заманбек Жетысай');

INSERT INTO customer (id, phone_number, shipping_address, discount)
VALUES ('43375a1e-1c91-46e5-9a10-a14200427fe9', '+7(775)457-4117', '', 4),
       ('1d468c04-6360-43e5-9d51-7771e9d9dcff', '+7(707)145-1475', '', 3);

INSERT INTO account (id, counterparty_id, name, balance, bank, giro)
VALUES ('054935c1-1369-4aa1-9c55-6f76e68a0dfb', '43375a1e-1c91-46e5-9a10-a14200427fe9',
        null, 0, null, null),
       ('9052abdd-7767-4961-a17b-035993f7570b', '1d468c04-6360-43e5-9d51-7771e9d9dcff',
        null, 0, null, null),
       ('6057082b-041b-47b7-ba31-9fa693eb2a21', 'c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515',
        'Ернар Ж.', 0, 'Kaspi Bank', '+7(775)216-6661'),
       ('c8190dcc-1cbe-4df6-a582-0f85e9850335', 'c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515',
        'Жайнагүл Қ.', 0, 'Kaspi Bank', '+7(702)445-9711');


