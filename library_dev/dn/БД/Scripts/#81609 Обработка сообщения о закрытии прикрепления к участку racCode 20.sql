--- для создания новых данных достаточно нужно заменить переменную "${id}" на уникальную
 
--- создание пациента не забыть указать дату рождения (исполнилось 18 лет в текущем месяце)


INSERT INTO f_patient (id, area_id, changed_date, birth_date, update_birth_date) VALUES(${id}, 23, '2003-08-29 10:21:54.000', '2002-10-28', NULL);


--- создание прикреплений для проверки их закрытия

INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(1${id}, ${id}, 567, '2021-03-09', NULL, 14484, 128290661, '602', 125676819, false);
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(2${id}, ${id}, 567, '2021-03-09', NULL, 14484, 128290661, '602', 125676819, NULL);
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(3${id}, ${id}, 567, '2021-03-09', NULL, 14484, 128290661, '602', 125676819, true);
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(4${id}, ${id}, 567, '2021-03-09', '2021-03-09', 14484, 128290661, '602', 125676819, false); --- прикрепление не обрабатывается, т.к. закрыто
 
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(1${id}, 1${id}, '2021-04-12 14:45:33.585', '35c8cf53-4a90-4720-b51d-55ec397c4a38', '14484', 128290661, '602', 125676819, 4, 'комментарий', false);
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(2${id}, 2${id}, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128287720, '69', 125676819, 4, 'комментарий', NULL);
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(3${id}, 3${id}, '2021-04-12 14:45:33.585', '35c8cf53-4a90-4720-b51d-55ec397c4a38', '14484', 128290661, '602', 125676819, 4, 'комментарий', true);
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(4${id}, 4${id}, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128287720, '69', 125676819, 4, 'комментарий', false);

--- создание тасок и связанных данных
--- задачи для проверки шага 1
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(1${id}, 'HOSPITAL_DISCHARGE_DOCUMENT', ${id}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- проставлено deleted на первом шаге
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(2${id}, 'AREA', ${id}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- проставлено deleted на первом шаге
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(3${id}, 'DIGITIZED_DOCUMENT', ${id}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- проставлено deleted на первом шаге
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(4${id}, 'CONTINUED_OBS', ${id}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- проставлено deleted на первом шаге
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(5${id}, 'HOSPITAL_DISCHARGE_DOCUMENT', ${id}, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false); --- шаг 1 не обработает, т.к. закрыта
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(8${id}, 'AREA', ${id}, '2021-07-02 15:37:59.000', NULL, NULL, 30, true); --- шаг 1 не обработает, т.к. deleted = true

--- задачи для проверки шага 2
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(6${id}, 'HOSPITAL_DISCHARGE_DOCUMENT', ${id}, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false);
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(7${id}, 'DIGITIZED_DOCUMENT', ${id}, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false);
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(10${id}, 'PREVENTIVE_EXAMINATION_DOCUMENT', ${id}, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false);
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(9${id}, 'HOSPITAL_DISCHARGE_DOCUMENT', ${id}, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false); --- задача, по которой два диагноза и один не обработан
 
--- заполнение связанных таблиц
INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(1${id}, '2edb2286-d140-4857-b2b5-02a2987${id}');
INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(5${id}, '2edb2286-d140-4857-b2b5-02a29872${id}');
INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(6${id}, '2edb2286-d140-4857-b2b5-02a29873${id}');
INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(9${id}, '2edb2286-d140-4857-b2b5-02a29874${id}');

INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(1${id}, 1${id}, 128291744, 'NEW');
INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(2${id}, 5${id}, 128291744, 'NEW');
INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(3${id}, 6${id}, 128291744, 'NEW');
INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(8${id}, 9${id}, 128290661, 'NEW');


INSERT INTO f_area_task (id) VALUES(2${id});
INSERT INTO f_area_task (id) VALUES(8${id});


INSERT INTO f_document_digitized_task (id, uid) VALUES(3${id}, '9f133c19-f164-423c-88f0-a9628b1e${id}');
INSERT INTO f_document_digitized_task (id, uid) VALUES(7${id}, '9f133c19-f164-423c-88f0-a9628b1e2${id}');


INSERT INTO f_doc_dig_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(1${id}, 3${id}, 128290661, 'NEW');
INSERT INTO f_doc_dig_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(2${id}, 7${id}, 128290661, 'NEW');


INSERT INTO f_continued_obs_task (id, attachment_id, mkb10_id, status) VALUES(4${id}, 1${id}, 128290661, 'NEW');

INSERT INTO f_document_preventive_examination_task (id, uid) VALUES(10${id}, '10${id}');
INSERT INTO f_doc_prev_exam_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(10${id}, 10${id}, 128291744, 'NEW');


--- создание документов оснований


INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('2edb2286-d140-4857-b2b5-02a29873${id}', ${id}, '2021-02-24 17:13:49.000', 'Выписной эпикриз', false, NULL); --- документ не обрабатывается на шаге 2, есть задачи
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('9f133c19-f164-423c-88f0-a9628b1e2${id}', ${id}, '2021-02-24 17:13:49.000', 'Оцифрованная карта № 030/У', false, NULL); --- документ не обрабатывается на шаге 2, есть задачи
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('${id}', ${id}, '2021-02-24 17:13:49.000', 'Оцифрованная карта № 030/У', false, NULL); --- документ обрабатывает у пациента определена группа ДН 1023 DNA0149, т.к. нет задач
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('3${id}', ${id}, '2021-02-24 17:13:49.000', 'Выписной эпикриз', false, NULL); --- документ обрабатывает у пациента определена группа ДН 2 DNA0002, т.к. нет задач
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('2${id}', ${id}, '2021-02-24 17:13:49.000', 'Оцифрованная карта № 030/У', true, '2021-02-24 17:13:49.000'); --- документ не обрабатывается на шаге 2, для документа нет задач, но deprecated = true
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('2edb2286-d140-4857-b2b5-02a29874${id}', ${id}, '2021-02-24 17:13:49.000', 'Выписной эпикриз', false, NULL); --- документ обрабатывает у пациента определена только группа ДН 2 DNA0002, т.к. по второму диагнозу есть задачи
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('10${id}', ${id}, '2021-02-24 17:13:49.000', 'Протокол профосмотра/диспансеризации', false, NULL); --- документ не обрабатывается на шаге 2, есть задачи
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('11${id}', ${id}, '2021-02-24 17:13:49.000', 'Протокол профосмотра/диспансеризации', false, NULL); --- документ обрабатывается на шаге 2, нет задачи
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('12${id}', ${id}, '2021-02-24 17:13:49.000', 'Протокол профосмотра/диспансеризации', false, NULL); --- документ обрабатывается на шаге 2, нет задачи


INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('2edb2286-d140-4857-b2b5-02a29873${id}', 128291744);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('9f133c19-f164-423c-88f0-a9628b1e2${id}', 128290661);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('${id}', 128290661); --- есть взрослые и детские группы ДН
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('2${id}', 128290661);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('3${id}', 128287720); --- есть только взрослые группы ДН
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('2edb2286-d140-4857-b2b5-02a29874${id}', 128290661); --- есть взрослые и детские группы ДН
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('2edb2286-d140-4857-b2b5-02a29874${id}', 128287720); --- есть только взрослые группы ДН
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('10${id}', 128291744);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('11${id}', 128290661);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('12${id}', 128290661);