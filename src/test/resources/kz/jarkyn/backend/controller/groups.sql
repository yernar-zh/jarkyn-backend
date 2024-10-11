DELETE
FROM GROUPS
WHERE TRUE;

INSERT INTO groups (ID, NAME, POSITION, PARENT_ID, CREATED_AT, LAST_MODIFIED_AT)
VALUES ('da48c6fa-6739-11ee-0a80-039b000669e2',
        'Педаль', 100, null,
        now(), now()),
       ('cdfcf458-7cca-11ef-0a80-152f001b4886',
        'Кикстартер', 100, 'da48c6fa-6739-11ee-0a80-039b000669e2',
        now(), now()),
       ('6120deea-5b87-11ee-0a80-000c0039b0fd',
        'Педаль переключения передач', 99, 'da48c6fa-6739-11ee-0a80-039b000669e2',
        now(), now());