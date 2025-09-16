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