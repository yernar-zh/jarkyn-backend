DELETE
FROM attribute
WHERE TRUE;

DELETE
FROM attribute_group
WHERE TRUE;

INSERT INTO ATTRIBUTE_GROUP (ID, NAME, POSITION, CREATED_AT, LAST_MODIFIED_AT)
VALUES ('c5a95fbd-121e-4f57-a84b-600a9919228a',
        'Транспорт', 100,
        now(), now()),
       ('5d82a954-5b87-11ee-0a80-000c0039afd5',
        'Цвет', 101,
        now(), now());

INSERT INTO attribute (ID, NAME, POSITION, GROUP_ID, CREATED_AT, LAST_MODIFIED_AT)
VALUES ('e95420b5-3344-44ce-8d39-699f516ed715',
        'Мотоцикл GN', 100, 'c5a95fbd-121e-4f57-a84b-600a9919228a',
        now(), now()),
       ('355785a2-0dd8-49f8-987f-06e3c48bf9a8',
        'Мотоцикл WY', 99, 'c5a95fbd-121e-4f57-a84b-600a9919228a',
        now(), now());