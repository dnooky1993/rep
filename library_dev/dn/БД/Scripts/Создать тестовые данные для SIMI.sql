--- для создания новых данных достаточно в скрипте заменить "${id}" на новое во всех строках по неполному совпадению

--- HOSPITAL_DISCHARGE_DOCUMENT
--- задача по документу. Указать ID пациента

INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(${id}, 'HOSPITAL_DISCHARGE_DOCUMENT', 30000007874947, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false);

--- связанные таблицы по документу. Указать UID из getDocument и ID пациента

INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(${id}, '79c1f73b-04d6-4099-8258-a60ce65de4ad');
INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(${id}, ${id}, 128290661, 'NEW');
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('79c1f73b-04d6-4099-8258-a60ce65de4ad', 30000007874947, '2021-02-24 17:13:49.000', 'Выписной эпикриз', false, NULL);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('79c1f73b-04d6-4099-8258-a60ce65de4ad', 128290661);

--- создание c_dn_justification_check. Не забыть указать тот же dn_document_uid, что и в сообщении и justification_document_id = UID из getDocument
INSERT INTO c_dn_justification_check (dn_document_uid, justification_document_id, simi_received, think_received) VALUES('eb3319f1-8104-4590-beeb-025212125909', '${id}', false, false);



--- PREVENTIVE_EXAMINATION_DOCUMENT
--- задача по документу. Указать ID пациента

INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(${id}, 'PREVENTIVE_EXAMINATION_DOCUMENT', 30000000804111, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false);

--- связанные таблицы по документу. Указать UID из getDocument и ID пациента

INSERT INTO f_document_preventive_examination_task (id, uid) VALUES(${id}, '3c58f65b-9ef5-4cc5-a974-baba777ed863');
INSERT INTO f_doc_prev_exam_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(${id}, ${id}, 128290661, 'NEW');
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('3c58f65b-9ef5-4cc5-a974-baba777ed863', 30000000804111, '2021-02-24 17:13:49.000', 'Протокол профосмотра/диспансеризации', false, NULL);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('3c58f65b-9ef5-4cc5-a974-baba777ed863', 128290661);

--- создание c_dn_justification_check. Не забыть указать тот же dn_document_uid, что и в сообщении и justification_document_id = UID из getDocument
INSERT INTO c_dn_justification_check (dn_document_uid, justification_document_id, simi_received, think_received) VALUES('608187f8-c59a-4dd5-9a31-345f84249b0a', '3c58f65b-9ef5-4cc5-a974-baba777ed863', false, false);


--- DIGITIZED_DOCUMENT
--- задача по документу. Указать ID пациента

INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(${id}, 'DIGITIZED_DOCUMENT', 30000000804111, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false);

--- связанные таблицы по документу. Указать UID из getDocument и ID пациента

INSERT INTO f_document_digitized_task (id, uid) VALUES(${id}, '3c58f65b-9ef5-4cc5-a974-baba777ed863');
INSERT INTO f_doc_dig_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(${id}, ${id}, 128290661, 'NEW');
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('3c58f65b-9ef5-4cc5-a974-baba777ed863', 30000000804111, '2021-02-24 17:13:49.000', 'Оцифрованная карта № 030/У', false, NULL);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('3c58f65b-9ef5-4cc5-a974-baba777ed863', 128290661);

--- создание c_dn_justification_check. Не забыть указать тот же dn_document_uid, что и в сообщении и justification_document_id = UID из getDocument
INSERT INTO c_dn_justification_check (dn_document_uid, justification_document_id, simi_received, think_received) VALUES('608187f8-c59a-4dd5-9a31-345f84249b0a', '3c58f65b-9ef5-4cc5-a974-baba777ed863', false, false);