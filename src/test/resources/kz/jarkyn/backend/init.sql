INSERT INTO public.party (id, name, archived, created_at, last_modified_at)
VALUES ('70167f17-b7b7-4ce2-b2ad-2c8da4976cff', 'ИП Жаркын', false, '2025-09-19 10:30:01.636575',
        '2025-09-19 10:30:01.636575');
INSERT INTO public.party (id, name, archived, created_at, last_modified_at)
VALUES ('530ef41c-4eec-4cff-898e-3fc0ec509f01', 'Урымжы Кытай', false, '2025-09-24 08:00:17.061845',
        '2025-09-24 08:00:17.061845');
INSERT INTO public.party (id, name, archived, created_at, last_modified_at)
VALUES ('7b22e58d-1583-4a29-9694-c772659c8c09', 'Оркен Алматы', false, '2025-09-24 08:01:35.907576',
        '2025-09-24 08:01:35.907576');
INSERT INTO public.organization (id)
VALUES ('70167f17-b7b7-4ce2-b2ad-2c8da4976cff');
INSERT INTO public.counterparty (id, phone_number, shipping_address, discount)
VALUES ('530ef41c-4eec-4cff-898e-3fc0ec509f01', '', '', 0);
INSERT INTO public.counterparty (id, phone_number, shipping_address, discount)
VALUES ('7b22e58d-1583-4a29-9694-c772659c8c09', '+7 702 670 5556', '', 0);

INSERT INTO public.warehouse (id, name, archived, created_at, last_modified_at)
VALUES ('7cfa1b93-b092-4d39-beb3-58eac6a8f800', 'Кенжина', false, '2025-09-19 12:19:23.589798',
        '2025-09-19 12:19:23.589798');
INSERT INTO public.warehouse (id, name, archived, created_at, last_modified_at)
VALUES ('fa48a071-2be2-4055-a60f-31255dc46757', 'Барыс', false, '2025-09-19 12:19:30.196604',
        '2025-09-19 12:19:30.196604');

INSERT INTO public.account (id, name, archived, created_at, last_modified_at, organization_id, counterparty_id, bank,
                            giro, currency_id)
VALUES ('83252f10-d198-441e-9fbc-d225d4a2a2b2', 'Каспи Ернар Ж', false, '2025-09-19 12:20:26.419246',
        '2025-09-19 12:20:26.419246', '70167f17-b7b7-4ce2-b2ad-2c8da4976cff', null, 'Каспи ', 'Ернар Ж +7 775 216 6661',
        '559109ea-f824-476d-8fa4-9990e53880ff');
INSERT INTO public.account (id, name, archived, created_at, last_modified_at, organization_id, counterparty_id, bank,
                            giro, currency_id)
VALUES ('61528e17-1857-45f0-9773-5e99e2fb0bb3', 'Наличными Тенге', false, '2025-09-19 10:30:31.681333',
        '2025-09-19 12:21:25.613073', '70167f17-b7b7-4ce2-b2ad-2c8da4976cff', null, '', '',
        '559109ea-f824-476d-8fa4-9990e53880ff');
INSERT INTO public.account (id, name, archived, created_at, last_modified_at, organization_id, counterparty_id, bank,
                            giro, currency_id)
VALUES ('d83f7353-1ffe-40c8-a4d6-d4b32726e155', 'Наличными Юань', false, '2025-09-19 12:21:45.778511',
        '2025-09-19 12:21:45.778511', '70167f17-b7b7-4ce2-b2ad-2c8da4976cff', null, '', '',
        'e6a3c207-a358-47bf-ac18-2d09973f3807');
INSERT INTO public.account (id, name, archived, created_at, last_modified_at, organization_id, counterparty_id, bank,
                            giro, currency_id)
VALUES ('b2985ddb-d3cb-4b70-88ae-2250c5d64dcd', 'Наличными Доллар', false, '2025-09-19 12:23:03.367596',
        '2025-09-19 12:23:03.367596', '70167f17-b7b7-4ce2-b2ad-2c8da4976cff', null, '', '',
        '24f15639-67da-4df4-aab4-56b85c872c3b');

INSERT INTO public.attribute_group (id, name, archived, created_at, last_modified_at)
VALUES ('968b81dd-985b-43c8-85e8-7385d305d595', 'Объем двигателя', false, '2025-09-19 10:38:38.255200',
        '2025-09-19 10:38:38.255200');
INSERT INTO public.attribute_group (id, name, archived, created_at, last_modified_at)
VALUES ('15b11568-aab1-4447-9160-b78e217b8a82', 'Транспорт', false, '2025-09-19 09:20:20.224826',
        '2025-09-19 11:57:20.924266');
INSERT INTO public.attribute (id, name, archived, created_at, last_modified_at, group_id, search_keywords)
VALUES ('9cc4f1b5-c84d-4de1-b1a4-3c55759b465b', 'Эндуро', false, '2025-09-19 10:17:24.208204',
        '2025-09-19 10:28:49.425213', '15b11568-aab1-4447-9160-b78e217b8a82', 'enduro');
INSERT INTO public.attribute (id, name, archived, created_at, last_modified_at, group_id, search_keywords)
VALUES ('4bded9ea-ed43-416a-b336-d70486d57ee2', '150 куб', false, '2025-09-19 10:36:30.711845',
        '2025-09-19 10:40:18.563232', '968b81dd-985b-43c8-85e8-7385d305d595', '');
INSERT INTO public.attribute (id, name, archived, created_at, last_modified_at, group_id, search_keywords)
VALUES ('7084aa04-4dc3-49d5-8e28-a5fa02fce81e', 'Классика WY', false, '2025-09-19 11:39:25.282847',
        '2025-09-19 11:45:23.049498', '15b11568-aab1-4447-9160-b78e217b8a82', 'klassika уян');
INSERT INTO public.attribute (id, name, archived, created_at, last_modified_at, group_id, search_keywords)
VALUES ('b5381472-fa2f-4455-8b73-3403f97c2fdd', 'Скутер Самурай', false, '2025-09-19 10:17:46.968131',
        '2025-09-19 11:46:16.133536', '15b11568-aab1-4447-9160-b78e217b8a82', 'skuter samurai');
INSERT INTO public.attribute (id, name, archived, created_at, last_modified_at, group_id, search_keywords)
VALUES ('193bb168-8adc-4cbe-bd56-61dfa0ca6238', 'Классика GN', false, '2025-09-19 11:56:47.791254',
        '2025-09-19 11:56:47.791254', '15b11568-aab1-4447-9160-b78e217b8a82', 'klassika жн');
INSERT INTO public.attribute (id, name, archived, created_at, last_modified_at, group_id, search_keywords)
VALUES ('c4bcf529-93c8-42d5-9947-9a0d898dc49f', '200 куб', false, '2025-09-19 11:57:39.293201',
        '2025-09-19 11:57:39.293201', '968b81dd-985b-43c8-85e8-7385d305d595', '');
INSERT INTO public.attribute (id, name, archived, created_at, last_modified_at, group_id, search_keywords)
VALUES ('a173763d-0386-4d22-a7cd-c9baa680f492', '250 куб', false, '2025-09-19 11:57:50.634890',
        '2025-09-19 11:57:50.634890', '968b81dd-985b-43c8-85e8-7385d305d595', '');
INSERT INTO public.groups (id, name, archived, created_at, last_modified_at, search_keywords, minimum_markup,
                           selling_markup, parent_id, position)
VALUES ('35b31b30-898a-4b4c-b772-0644d42ba108', 'Мототехника', false, '2025-09-19 10:47:51.657569',
        '2025-09-19 10:48:06.915985', 'moto', 25, 50, null, 1000);
INSERT INTO public.groups (id, name, archived, created_at, last_modified_at, search_keywords, minimum_markup,
                           selling_markup, parent_id, position)
VALUES ('5ca4b6fd-283f-4ab2-a194-87e843402ce0', 'Педаль', false, '2025-09-19 10:49:13.471571',
        '2025-09-19 11:13:12.549307', 'pedal', null, null, '35b31b30-898a-4b4c-b772-0644d42ba108', 1);
INSERT INTO public.groups (id, name, archived, created_at, last_modified_at, search_keywords, minimum_markup,
                           selling_markup, parent_id, position)
VALUES ('afa39f9e-27db-4287-84a4-70ac56342a7b', 'Аккумулятор', false, '2025-09-19 11:12:43.985050',
        '2025-09-19 11:15:32.547315', 'akkumulator', null, 30, '35b31b30-898a-4b4c-b772-0644d42ba108', 0);
INSERT INTO public.groups (id, name, archived, created_at, last_modified_at, search_keywords, minimum_markup,
                           selling_markup, parent_id, position)
VALUES ('3492be06-33c6-44b5-9f66-bdcbd1094036', 'Масло', false, '2025-09-19 12:18:30.457906',
        '2025-09-19 12:18:30.457906', 'maslo масла май', null, null, '35b31b30-898a-4b4c-b772-0644d42ba108', 1000);
INSERT INTO public.good (id, name, archived, created_at, last_modified_at, group_id, image_id, weight, minimum_price,
                         search_keywords)
VALUES ('a0d9d430-1018-4e53-a6b4-833bd05e098a', 'Кикстартер S', false, '2025-09-19 11:31:09.303482',
        '2025-09-19 11:31:09.303482', '5ca4b6fd-283f-4ab2-a194-87e843402ce0', null, 0, 850, 'kikstarter с');
INSERT INTO public.good (id, name, archived, created_at, last_modified_at, group_id, image_id, weight, minimum_price,
                         search_keywords)
VALUES ('f3289a44-d343-4953-84e9-cc2c564f277a', 'Кикстартер L', false, '2025-09-19 12:11:02.399838',
        '2025-09-19 12:11:02.399838', '5ca4b6fd-283f-4ab2-a194-87e843402ce0', null, 0, 770, 'kikstarter л');
INSERT INTO public.good (id, name, archived, created_at, last_modified_at, group_id, image_id, weight, minimum_price,
                         search_keywords)
VALUES ('6ddbd771-c468-4086-b550-5c79554b382b', 'Аккумулятор WY 7Ah', false, '2025-09-19 12:14:27.541258',
        '2025-09-19 12:14:27.541258', 'afa39f9e-27db-4287-84a4-70ac56342a7b', null, 0, 5330, '');
INSERT INTO public.good (id, name, archived, created_at, last_modified_at, group_id, image_id, weight, minimum_price,
                         search_keywords)
VALUES ('d3a6e8e1-8a8c-4940-b800-308df85889c0', 'Аккумулятор GN 9Ah', false, '2025-09-19 12:17:14.186396',
        '2025-09-19 12:17:14.186396', 'afa39f9e-27db-4287-84a4-70ac56342a7b', null, 0, 5330, '');
INSERT INTO public.good_attribute (id, created_at, last_modified_at, good_id, attribute_id)
VALUES ('1764ee8e-aeba-45fb-a078-b93a615bb318', '2025-09-19 12:11:02.409633', '2025-09-19 12:11:02.409633',
        'f3289a44-d343-4953-84e9-cc2c564f277a', '193bb168-8adc-4cbe-bd56-61dfa0ca6238');
INSERT INTO public.good_attribute (id, created_at, last_modified_at, good_id, attribute_id)
VALUES ('e6e5def9-7f3b-4e85-8148-bef0d813056a', '2025-09-19 12:12:17.837779', '2025-09-19 12:12:17.837779',
        'a0d9d430-1018-4e53-a6b4-833bd05e098a', '7084aa04-4dc3-49d5-8e28-a5fa02fce81e');
INSERT INTO public.good_attribute (id, created_at, last_modified_at, good_id, attribute_id)
VALUES ('63706050-3c39-4d0f-80ed-db23573e7ba7', '2025-09-19 12:14:27.543405', '2025-09-19 12:14:27.543405',
        '6ddbd771-c468-4086-b550-5c79554b382b', '7084aa04-4dc3-49d5-8e28-a5fa02fce81e');
INSERT INTO public.good_attribute (id, created_at, last_modified_at, good_id, attribute_id)
VALUES ('46019fcb-09b7-430e-8414-2b43037e619b', '2025-09-19 12:17:14.188601', '2025-09-19 12:17:14.188601',
        'd3a6e8e1-8a8c-4940-b800-308df85889c0', '193bb168-8adc-4cbe-bd56-61dfa0ca6238');
INSERT INTO public.selling_price (id, created_at, last_modified_at, good_id, quantity, val)
VALUES ('467ce251-4b67-485f-aa2e-73553a12ea1e', '2025-09-19 11:31:09.304018', '2025-09-19 11:31:09.304018',
        'a0d9d430-1018-4e53-a6b4-833bd05e098a', 1, 960);
INSERT INTO public.selling_price (id, created_at, last_modified_at, good_id, quantity, val)
VALUES ('0bd0ee3f-5aa2-4897-a6c2-7b4a28f2af85', '2025-09-19 12:11:02.432570', '2025-09-19 12:11:02.432570',
        'f3289a44-d343-4953-84e9-cc2c564f277a', 1, 850);
INSERT INTO public.selling_price (id, created_at, last_modified_at, good_id, quantity, val)
VALUES ('d4662acf-a096-468b-ab6e-694ec492946e', '2025-09-19 12:14:27.551507', '2025-09-19 12:14:27.551507',
        '6ddbd771-c468-4086-b550-5c79554b382b', 1, 5330);
INSERT INTO public.selling_price (id, created_at, last_modified_at, good_id, quantity, val)
VALUES ('66987583-878f-4aeb-924a-f1134f254e41', '2025-09-19 12:17:14.197470', '2025-09-19 12:17:14.197470',
        'd3a6e8e1-8a8c-4940-b800-308df85889c0', 1, 5330);
