-- Auth
UPDATE users
SET auth_token = '<AUTH_TOKEN>'
WHERE id = 'f57382c9-35a1-4b64-b3f0-f172489dc90a';


-- Counterparty
INSERT INTO warehouse (id, name)
VALUES ('523961a7-696d-4779-8bb0-fd327feaecf3', 'Кенжина'),
       ('d1da1441-6598-4511-bc82-8fc06602e373', 'Барыс');

INSERT INTO counterparty (id, name)
VALUES ('1d468c04-6360-43e5-9d51-7771e9d9dcff', 'Заманбек Жетысай'),
       ('43375a1e-1c91-46e5-9a10-a14200427fe9', 'Оркен Алматы');

INSERT INTO customer (id, phone_number, shipping_address, discount)
VALUES ('1d468c04-6360-43e5-9d51-7771e9d9dcff', '+7(707)145-1475', '', 3),
       ('43375a1e-1c91-46e5-9a10-a14200427fe9', '+7(775)457-4117', '', 4);

INSERT INTO account (id, counterparty_id, name, balance, bank, giro)
VALUES ('054935c1-1369-4aa1-9c55-6f76e68a0dfb', '43375a1e-1c91-46e5-9a10-a14200427fe9',
        null, 0, null, null),
       ('9052abdd-7767-4961-a17b-035993f7570b', '1d468c04-6360-43e5-9d51-7771e9d9dcff',
        null, 0, null, null),
       ('6057082b-041b-47b7-ba31-9fa693eb2a21', 'c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515',
        'Ернар Ж.', 0, 'Kaspi Bank', '+7(775)216-6661'),
       ('c8190dcc-1cbe-4df6-a582-0f85e9850335', 'c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515',
        'Жайнагүл Қ.', 0, 'Kaspi Bank', '+7(702)445-9711');


-- Good
INSERT INTO groups (ID, NAME, POSITION, PARENT_ID)
VALUES ('da48c6fa-6739-11ee-0a80-039b000669e2',
        'Педаль', 100, null),
       ('cdfcf458-7cca-11ef-0a80-152f001b4886',
        'Кикстартер', 100, 'da48c6fa-6739-11ee-0a80-039b000669e2'),
       ('6120deea-5b87-11ee-0a80-000c0039b0fd',
        'Педаль переключения передач', 99, 'da48c6fa-6739-11ee-0a80-039b000669e2');

INSERT INTO attribute_group (id, name, position)
VALUES ('c5a95fbd-121e-4f57-a84b-600a9919228a',
        'Транспорт', 100),
       ('5d82a954-5b87-11ee-0a80-000c0039afd5',
        'Цвет', 101);

INSERT INTO attribute (id, name, position, group_id)
VALUES ('e95420b5-3344-44ce-8d39-699f516ed715',
        'Мотоцикл GN', 100, 'c5a95fbd-121e-4f57-a84b-600a9919228a'),
       ('355785a2-0dd8-49f8-987f-06e3c48bf9a8',
        'Мотоцикл WY', 99, 'c5a95fbd-121e-4f57-a84b-600a9919228a');

INSERT INTO good (id, name, group_id, minimum_price, image_id, archived)
VALUES ('7f316872-1da3-44c8-9293-0fddda859435',
        'Кикстартер L', 'cdfcf458-7cca-11ef-0a80-152f001b4886',
        800, NULL, FALSE),
       ('bf6f2ba4-f994-44c1-839f-36a75f07242e',
        'Педаль переключения передач WY (короткий)', '6120deea-5b87-11ee-0a80-000c0039b0fd',
        500, NULL, FALSE);

INSERT INTO good_attribute (id, good_id, attribute_id)
VALUES ('adc7414d-3985-4a16-8790-d5543f624d7a', '7f316872-1da3-44c8-9293-0fddda859435',
        'e95420b5-3344-44ce-8d39-699f516ed715'),
       ('37593a71-7d16-4409-ad41-e74b294d5855', 'bf6f2ba4-f994-44c1-839f-36a75f07242e',
        '355785a2-0dd8-49f8-987f-06e3c48bf9a8');

INSERT INTO selling_price (id, good_id, quantity, val)
VALUES ('fbe70a5c-a0d1-414c-9507-7352739b1243', '7f316872-1da3-44c8-9293-0fddda859435',
        1, 880),
       ('de14fc67-d461-4094-a79f-ed32e2e50518', '7f316872-1da3-44c8-9293-0fddda859435',
        20, 850),
       ('4d4bc769-df3f-4e25-9f23-d01083bdb056', 'bf6f2ba4-f994-44c1-839f-36a75f07242e',
        1, 600),
       ('7b87a987-36a1-4fa9-8f65-04de71bea721', 'bf6f2ba4-f994-44c1-839f-36a75f07242e',
        50, 550);

-- Document
INSERT INTO document (id, warehouse_id, customer_id, name, moment, amount, comment, deleted)
VALUES ('9d56c02e-81e5-47a6-ab0a-fbeca21293af', '523961a7-696d-4779-8bb0-fd327feaecf3',
        '1d468c04-6360-43e5-9d51-7771e9d9dcff', 'SL-00001', '2024-12-03 17:28',
        63800, '', false);

INSERT INTO sale (id, shipment_moment, state)
VALUES ('9d56c02e-81e5-47a6-ab0a-fbeca21293af', null, 'NEW');

INSERT INTO item (id, document_id, good_id, quantity, price, position)
VALUES ('2c7f1951-4ddb-4125-a52b-2183e013e65f', '9d56c02e-81e5-47a6-ab0a-fbeca21293af',
        '7f316872-1da3-44c8-9293-0fddda859435', 10, 880, 0),
       ('49dfee7e-1683-4a53-92f2-bd65be5b045f', '9d56c02e-81e5-47a6-ab0a-fbeca21293af',
        'bf6f2ba4-f994-44c1-839f-36a75f07242e', 100, 550, 1);
