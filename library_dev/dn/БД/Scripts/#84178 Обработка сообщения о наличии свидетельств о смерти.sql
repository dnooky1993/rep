--- для создания новых данных достаточно в скрипте заменить "${id}" и "${id2}" на новое во всех строках по неполному совпадению
 
INSERT INTO f_patient (id, area_id, changed_date, birth_date, update_birth_date) VALUES(${id}, ${id2}, '2003-07-28 10:21:54.000', '2002-10-28', NULL);

--- создание прикреплений

INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(1${id2}, ${id}, 567, '2021-03-09', NULL, 14484, 128290661, '602', 125676819, false); -- проверка обработки различных статусов no_doctor
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(2${id2}, ${id}, 567, '2021-03-09', NULL, 14484, 128290661, '602', 125676819, NULL); -- проверка обработки различных статусов no_doctor
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(3${id2}, ${id}, 567, '2021-03-09', NULL, 14484, 128290661, '602', 125676819, true); -- проверка обработки различных статусов no_doctor
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(4${id2}, ${id}, 567, '2021-03-09', '2021-03-09', 14484, 128290661, '602', 125676819, false); --- прикрепление не обрабатывается, т.к. закрыто

INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor, dn_doctor_job_execution_id, doctor_fio, repeat, speciality_name, diagnosis_date) VALUES(1${id2}, 1${id2}, '2021-04-12 14:45:33.585', '35c8cf53-4a90-4720-b51d-55ec397c4a38', '14484', 128290661, '602', 125676819, 4, 'комментарий', false, '111', 'Моисеева Марина Евгеньевна', 'диагноз установлен в предыдущ. году или ранее (-)', 'Врач - педиатр участковый', '2018-02-06 00:00:00.000');
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor, dn_doctor_job_execution_id, doctor_fio, repeat, speciality_name, diagnosis_date) VALUES(2${id2}, 2${id2}, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128287720, '69', 125676819, 4, 'комментарий', NULL, '111', 'Моисеева Марина Евгеньевна', 'диагноз установлен в предыдущ. году или ранее (-)', 'Врач - педиатр участковый', '2018-02-06 00:00:00.000');
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor, dn_doctor_job_execution_id, doctor_fio, repeat, speciality_name, diagnosis_date) VALUES(3${id2}, 3${id2}, '2021-04-12 14:45:33.585', '35c8cf53-4a90-4720-b51d-55ec397c4a38', '14484', 128290661, '602', 125676819, 4, 'комментарий', true, '111', 'Моисеева Марина Евгеньевна', 'диагноз установлен в предыдущ. году или ранее (-)', 'Врач - педиатр участковый', '2018-02-06 00:00:00.000');
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor, dn_doctor_job_execution_id, doctor_fio, repeat, speciality_name, diagnosis_date) VALUES(4${id2}, 4${id2}, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128287720, '69', 125676819, 4, 'комментарий', false, '111', 'Моисеева Марина Евгеньевна', 'диагноз установлен в предыдущ. году или ранее (-)', 'Врач - педиатр участковый', '2018-02-06 00:00:00.000');

--- создание тасок и связанных данных
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(1${id2}, 'HOSPITAL_DISCHARGE_DOCUMENT', ${id}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- проставлено deleted
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(2${id2}, 'AREA', ${id}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- проставлено deleted
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(3${id2}, 'DIGITIZED_DOCUMENT', ${id}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- проставлено deleted
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(4${id2}, 'CONTINUED_OBS', ${id}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- проставлено deleted
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(5${id2}, 'HOSPITAL_DISCHARGE_DOCUMENT', ${id}, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false); --- не обработает, т.к. закрыта
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(8${id2}, 'AREA', ${id}, '2021-07-02 15:37:59.000', NULL, NULL, 30, true); --- не обработает, т.к. deleted = true

--- заполнение связанных таблиц
INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(1${id2}, '2edb2286-d140-4857-b2b5-02a2987${id2}');
INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(5${id2}, '2edb2286-d140-4857-b2b5-02a29872${id2}');

INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(1${id2}, 1${id2}, 128291744, 'NEW');
INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(2${id2}, 5${id2}, 128291744, 'NEW');

INSERT INTO f_area_task (id) VALUES(2${id2});
INSERT INTO f_area_task (id) VALUES(8${id2});

INSERT INTO f_document_digitized_task (id, uid) VALUES(3${id2}, '9f133c19-f164-423c-88f0-a9628b1e${id2}');

INSERT INTO f_doc_dig_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(1${id2}, 3${id2}, 128290661, 'NEW');

INSERT INTO f_continued_obs_task (id, attachment_id, mkb10_id, status) VALUES(4${id2}, 1${id2}, 128290661, 'NEW');