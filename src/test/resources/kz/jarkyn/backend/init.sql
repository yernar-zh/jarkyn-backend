-------------------- Auth -----------------------
UPDATE users
SET auth_token = '<AUTH_TOKEN>'
WHERE id = 'f57382c9-35a1-4b64-b3f0-f172489dc90a';

------------- Organization accounts -------------
INSERT INTO account (id, organization_id, counterparty_id, name, bank, giro, currency_id, archived)
VALUES ('6057082b-041b-47b7-ba31-9fa693eb2a21', 'c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515', null,
        'Ернар Ж.', 'Kaspi Bank', '+7(775)216-6661', '559109ea-f824-476d-8fa4-9990e53880ff', false),
       ('c8190dcc-1cbe-4df6-a582-0f85e9850335', 'c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515', null,
        'Наличный CNY', '', '', 'e6a3c207-a358-47bf-ac18-2d09973f3807', false),
       ('8d1ed49a-6964-4a3e-bc83-8c22601e70f8', 'c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515', null,
        'Наличный USD', '', '', '24f15639-67da-4df4-aab4-56b85c872c3b', false);

------------------- Warehouse -------------------
INSERT INTO warehouse (id, name, archived)
VALUES ('523961a7-696d-4779-8bb0-fd327feaecf3', 'Кенжина', false),
       ('d1da1441-6598-4511-bc82-8fc06602e373', 'Барыс', false);


------------------- Supplier --------------------
-- Урумчи Кытай
INSERT INTO party (id, name, archived)
VALUES ('94fadc9a-83bb-4639-be07-f825ab9eb40e', 'Урумчи Кытай', false);
INSERT INTO counterparty (id, phone_number, shipping_address, discount)
VALUES ('94fadc9a-83bb-4639-be07-f825ab9eb40e', null, '', 0);
-- Шанхай Кытай
INSERT INTO party (id, name, archived)
VALUES ('72b5a4b5-3133-4076-ab5f-84262ebcae65', 'Шанхай Кытай', false);
INSERT INTO counterparty (id, phone_number, shipping_address, discount)
VALUES ('72b5a4b5-3133-4076-ab5f-84262ebcae65', null, '', 0);

-------------------- Customer -------------------
-- Заманбек Жетысай
INSERT INTO party (id, name, archived)
VALUES ('1d468c04-6360-43e5-9d51-7771e9d9dcff', 'Заманбек Жетысай', false);
INSERT INTO counterparty (id, phone_number, shipping_address, discount)
VALUES ('1d468c04-6360-43e5-9d51-7771e9d9dcff', '+7(707)145-1475', '', 3);
-- Оркен Алматы
INSERT INTO party (id, name, archived)
VALUES ('43375a1e-1c91-46e5-9a10-a14200427fe9', 'Оркен Алматы', false);
INSERT INTO counterparty (id, phone_number, shipping_address, discount)
VALUES ('43375a1e-1c91-46e5-9a10-a14200427fe9', '+7(775)457-4117', '', 4);

--------------------- Group ---------------------
INSERT INTO groups (ID, name, position, parent_id, archived)
VALUES ('656e38bc-bbc7-4e25-8b94-a2018783324c',
        'Все товары', 100, null, false),
       ('da48c6fa-6739-11ee-0a80-039b000669e2',
        'Педаль', 100, '656e38bc-bbc7-4e25-8b94-a2018783324c', false),
       ('cdfcf458-7cca-11ef-0a80-152f001b4886',
        'Кикстартер', 100, 'da48c6fa-6739-11ee-0a80-039b000669e2', false),
       ('6120deea-5b87-11ee-0a80-000c0039b0fd',
        'Педаль переключения передач', 99, 'da48c6fa-6739-11ee-0a80-039b000669e2', false),
       ('a590bc3a-9498-4f56-bce6-0e604b00590d',
        'Свеча', 99, '656e38bc-bbc7-4e25-8b94-a2018783324c', false);


------------------ Attribute --------------------
INSERT INTO attribute_group (id, name, position, archived)
VALUES ('c5a95fbd-121e-4f57-a84b-600a9919228a',
        'Транспорт', 100, false);
INSERT INTO attribute (id, name, position, group_id, archived)
VALUES ('e95420b5-3344-44ce-8d39-699f516ed715',
        'Мотоцикл GN', 100, 'c5a95fbd-121e-4f57-a84b-600a9919228a', false),
       ('355785a2-0dd8-49f8-987f-06e3c48bf9a8',
        'Мотоцикл WY', 99, 'c5a95fbd-121e-4f57-a84b-600a9919228a', false),
       ('797e7bc8-fca0-4d2b-b1ce-6975f54b48eb',
        'Alpha', 101, 'c5a95fbd-121e-4f57-a84b-600a9919228a', false);

INSERT INTO attribute_group (id, name, position, archived)
VALUES ('5d82a954-5b87-11ee-0a80-000c0039afd5',
        'Цвет', 101, false);

--------------------- Good ----------------------
-- Кикстартер L
INSERT INTO good (id, name, group_id, weight, minimum_price, image_id, archived)
VALUES ('7f316872-1da3-44c8-9293-0fddda859435',
        'Кикстартер L', 'cdfcf458-7cca-11ef-0a80-152f001b4886',
        254, 800, NULL, FALSE);
INSERT INTO good_attribute (id, good_id, attribute_id)
VALUES ('adc7414d-3985-4a16-8790-d5543f624d7a', '7f316872-1da3-44c8-9293-0fddda859435',
        'e95420b5-3344-44ce-8d39-699f516ed715');
INSERT INTO selling_price (id, good_id, quantity, val)
VALUES ('fbe70a5c-a0d1-414c-9507-7352739b1243', '7f316872-1da3-44c8-9293-0fddda859435',
        1, 880),
       ('de14fc67-d461-4094-a79f-ed32e2e50518', '7f316872-1da3-44c8-9293-0fddda859435',
        20, 850);
-- Педаль переключения передач WY (короткий)
INSERT INTO good (id, name, group_id, weight, minimum_price, image_id, archived)
VALUES ('bf6f2ba4-f994-44c1-839f-36a75f07242e',
        'Педаль переключения передач WY (короткий)', '6120deea-5b87-11ee-0a80-000c0039b0fd',
        120, 500, NULL, FALSE);
INSERT INTO good_attribute (id, good_id, attribute_id)
VALUES ('37593a71-7d16-4409-ad41-e74b294d5855', 'bf6f2ba4-f994-44c1-839f-36a75f07242e',
        '355785a2-0dd8-49f8-987f-06e3c48bf9a8');
INSERT INTO selling_price (id, good_id, quantity, val)
VALUES ('4d4bc769-df3f-4e25-9f23-d01083bdb056', 'bf6f2ba4-f994-44c1-839f-36a75f07242e',
        1, 600),
       ('7b87a987-36a1-4fa9-8f65-04de71bea721', 'bf6f2ba4-f994-44c1-839f-36a75f07242e',
        50, 550);

------------------- Supply ----------------------
INSERT INTO document (id, type_id, organization_id, warehouse_id, counterparty_id, name, moment,
                      currency_id, exchange_rate, amount, comment, deleted, commited)
VALUES ('17c1285b-6514-45d5-88a2-3b9f673dc5e3', '3873fc3e-58da-4ecc-81a8-70728d8d3297', 'c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515',
        '523961a7-696d-4779-8bb0-fd327feaecf3', '94fadc9a-83bb-4639-be07-f825ab9eb40e',
        'SP-00001', '2024-12-07 21:47',
        'e6a3c207-a358-47bf-ac18-2d09973f3807', 68, 710, '', false, false);
INSERT INTO supply (id)
VALUES ('17c1285b-6514-45d5-88a2-3b9f673dc5e3');
INSERT INTO item (id, document_id, good_id, quantity, price, position)
VALUES ('0098b2bc-da73-4451-bdb5-35f42f756f10', '17c1285b-6514-45d5-88a2-3b9f673dc5e3',
        '7f316872-1da3-44c8-9293-0fddda859435', 50, 6.2, 0),
       ('986378a0-271f-4293-96db-218f608599cd', '17c1285b-6514-45d5-88a2-3b9f673dc5e3',
        'bf6f2ba4-f994-44c1-839f-36a75f07242e', 100, 4, 1);
-- Supply supplier payment
INSERT INTO document (id, type_id, organization_id, warehouse_id, counterparty_id, name, moment,
                      currency_id, exchange_rate, amount, comment, deleted, commited)
VALUES ('5c799431-3bc3-400f-b9a3-209f27b935a0', '9b755b4e-61fa-457d-855d-6b231d3bfed2', 'c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515',
        null, '94fadc9a-83bb-4639-be07-f825ab9eb40e',
        'PO-00001', '2024-12-07 22:47',
        'e6a3c207-a358-47bf-ac18-2d09973f3807', 68, 710, '', false, false);
INSERT INTO payment_out (id, account_id, item_of_expenditure_id)
VALUES ('5c799431-3bc3-400f-b9a3-209f27b935a0', 'c8190dcc-1cbe-4df6-a582-0f85e9850335',
        'ac03d34e-2274-4f35-8939-46f17ddc05ed');
INSERT INTO paid_document (id, payment_id, document_id, amount)
VALUES ('538c3271-7398-4fab-ad05-0a886188de11', '5c799431-3bc3-400f-b9a3-209f27b935a0',
        '17c1285b-6514-45d5-88a2-3b9f673dc5e3', 710);