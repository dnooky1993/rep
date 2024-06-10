--- дл€ создани€ новых данных достаточно нужно заменить переменную "${id}" на уникальную
 
--- создание пациента


INSERT INTO f_patient (id, area_id, changed_date, birth_date, update_birth_date) VALUES(${id}, 23, '2003-08-29 10:21:54.000', '2002-10-28', NULL);




--- создание открытых задачи с датами. ¬се задачи возвращаютс€ согласно датам
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(1${id}, 'AREA', ${id}, '2020-10-13 00:00:00', null, null, 49, false);
INSERT INTO f_area_task (id) VALUES(1${id});


INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(2${id}, 'HOSPITAL_DISCHARGE_DOCUMENT', ${id}, '2020-10-13 23:59:59', null, null, 50, false);
INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(2${id}, '2${id}');
INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(2${id}, 2${id}, 128291744, 'NEW');


INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(3${id}, 'DIGITIZED_DOCUMENT', ${id}, '2020-10-14 00:00:00', null, null, 51, false);
INSERT INTO f_document_digitized_task (id, uid) VALUES(3${id}, '3${id}');
INSERT INTO f_doc_dig_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(3${id}, 3${id}, 128290661, 'NEW');


INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(4${id}, 'CONTINUED_OBS', ${id}, '2020-10-14 23:59:59', null, null, 30, false);
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(4${id}, ${id}, 567, '2021-03-09', NULL, 14484, 128290661, '602', 125676819, false);
INSERT INTO f_continued_obs_task (id, attachment_id, mkb10_id, status) VALUES(4${id}, 4${id}, 128290661, 'NEW');


INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(5${id}, 'PREVENTIVE_EXAMINATION_DOCUMENT', ${id}, '2020-10-14 23:59:59', null, null, 30, false);
INSERT INTO f_document_preventive_examination_task (id, uid) VALUES(5${id}, '5${id}');
INSERT INTO f_doc_prev_exam_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(5${id}, 5${id}, 128291744, 'NEW');


--- создание закрытых задач
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(7${id}, 'AREA', ${id}, '2021-07-02 15:37:59.000', '2020-10-12 23:59:59', 'test1', 30, false);
INSERT INTO f_area_task (id) VALUES(7${id});


INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(8${id}, 'AREA', ${id}, '2021-07-02 15:37:59.000', '2020-10-12 23:59:59', NULL, 30, true);
INSERT INTO f_area_task (id) VALUES(8${id});


--- создание открытых задач, по которым аннулирован документ основани€


INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(9${id}, 'HOSPITAL_DISCHARGE_DOCUMENT', ${id}, '2020-10-13 23:59:59', null, null, 50, false);
INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(9${id}, '9${id}');
INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(9${id}, 9${id}, 128291744, 'NEW');
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('9${id}', ${id}, '2020-03-18 16:50:56.190', '¬ыписной эпикриз', true, NULL);


INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(10${id}, 'DIGITIZED_DOCUMENT', ${id}, '2020-10-14 00:00:00', null, null, 51, false);
INSERT INTO f_document_digitized_task (id, uid) VALUES(10${id}, '10${id}');
INSERT INTO f_doc_dig_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(10${id}, 10${id}, 128290661, 'NEW');
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('10${id}', ${id}, '2020-03-18 16:50:56.190', 'ќцифрованна€ карта є 030/”', true, NULL);


INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(11${id}, 'PREVENTIVE_EXAMINATION_DOCUMENT', ${id}, '2020-10-14 23:59:59', null, null, 30, false);
INSERT INTO f_document_preventive_examination_task (id, uid) VALUES(11${id}, '11${id}');
INSERT INTO f_doc_prev_exam_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(11${id}, 11${id}, 128291744, 'NEW');
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('11${id}', ${id}, '2020-03-18 16:50:56.190', 'ѕротокол профосмотра/диспансеризации', true, NULL);