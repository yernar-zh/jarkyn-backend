DELETE
FROM good
WHERE TRUE;

DELETE
FROM GROUPS
WHERE TRUE;

INSERT INTO groups (ID, NAME, POSITION, PARENT_ID)
VALUES ('da48c6fa-6739-11ee-0a80-039b000669e2',
        'Педаль', 100, null),
       ('cdfcf458-7cca-11ef-0a80-152f001b4886',
        'Кикстартер', 100, 'da48c6fa-6739-11ee-0a80-039b000669e2'),
       ('6120deea-5b87-11ee-0a80-000c0039b0fd',
        'Педаль переключения передач', 99, 'da48c6fa-6739-11ee-0a80-039b000669e2');

INSERT INTO good (id, name, group_id, minimum_price, image_id, archived)
VALUES ('7f316872-1da3-44c8-9293-0fddda859435',
        'Кикстартер L', 'cdfcf458-7cca-11ef-0a80-152f001b4886',
        '800', NULL, FALSE);