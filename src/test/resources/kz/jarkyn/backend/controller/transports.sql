DELETE
FROM transport
WHERE TRUE;

INSERT INTO transport (ID, NAME, POSITION, PARENT_ID, CREATED_AT, LAST_MODIFIED_AT)
VALUES ('c5a95fbd-121e-4f57-a84b-600a9919228a',
        'Мотоцикл', 100, null,
        now(), now()),
       ('e95420b5-3344-44ce-8d39-699f516ed715',
        'Кикстартер', 100, 'da48c6fa-6739-11ee-0a80-039b000669e2',
        now(), now()),
       ('355785a2-0dd8-49f8-987f-06e3c48bf9a8',
        'Педаль переключения передач', 99, 'da48c6fa-6739-11ee-0a80-039b000669e2',
        now(), now());