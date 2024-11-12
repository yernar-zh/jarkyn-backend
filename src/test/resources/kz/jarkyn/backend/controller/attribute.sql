INSERT INTO ATTRIBUTE_GROUP (ID, NAME, POSITION)
VALUES ('c5a95fbd-121e-4f57-a84b-600a9919228a',
        'Транспорт', 100),
       ('5d82a954-5b87-11ee-0a80-000c0039afd5',
        'Цвет', 101);

INSERT INTO attribute (ID, NAME, POSITION, GROUP_ID)
VALUES ('e95420b5-3344-44ce-8d39-699f516ed715',
        'Мотоцикл GN', 100, 'c5a95fbd-121e-4f57-a84b-600a9919228a'),
       ('355785a2-0dd8-49f8-987f-06e3c48bf9a8',
        'Мотоцикл WY', 99, 'c5a95fbd-121e-4f57-a84b-600a9919228a');

INSERT INTO good (id, name, group_id, minimum_price, image_id, archived)
VALUES ('7f316872-1da3-44c8-9293-0fddda859435',
        'Кикстартер L', NULL,
        '800', NULL, FALSE);

INSERT INTO good_attribute (id, good_id, attribute_id)
VALUES ('adc7414d-3985-4a16-8790-d5543f624d7a',
        '7f316872-1da3-44c8-9293-0fddda859435', '355785a2-0dd8-49f8-987f-06e3c48bf9a8');