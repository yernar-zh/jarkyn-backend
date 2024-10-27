INSERT INTO groups (id, name, position, parent_id)
VALUES ('cdfcf458-7cca-11ef-0a80-152f001b4886',
        'Кикстартер', 100, null),
       ('faeb45ef-cf5a-45e1-af2e-6e875a2d6331',
        'Кикстартер 2', 101, null);

INSERT INTO attribute_group (id, name, position)
VALUES ('c5a95fbd-121e-4f57-a84b-600a9919228a',
        'Транспорт', 100);

INSERT INTO attribute (id, name, position, group_id)
VALUES ('e95420b5-3344-44ce-8d39-699f516ed715',
        'Мотоцикл GN', 100, 'c5a95fbd-121e-4f57-a84b-600a9919228a'),
       ('355785a2-0dd8-49f8-987f-06e3c48bf9a8',
        'Мотоцикл WY', 99, 'c5a95fbd-121e-4f57-a84b-600a9919228a');

INSERT INTO good (id, name, group_id, minimum_price, image_id, archived)
VALUES ('7f316872-1da3-44c8-9293-0fddda859435',
        'Кикстартер L', 'cdfcf458-7cca-11ef-0a80-152f001b4886',
        '800', NULL, FALSE);

INSERT INTO good_attribute (id, good_id, attribute_id)
VALUES ('adc7414d-3985-4a16-8790-d5543f624d7a',
        '7f316872-1da3-44c8-9293-0fddda859435', 'e95420b5-3344-44ce-8d39-699f516ed715');

INSERT INTO selling_price (id, good_id, quantity, val)
VALUES ('fbe70a5c-a0d1-414c-9507-7352739b1243',
        '7f316872-1da3-44c8-9293-0fddda859435', 1, 880),
       ('de14fc67-d461-4094-a79f-ed32e2e50518',
        '7f316872-1da3-44c8-9293-0fddda859435', 20, 850);