--- для создания новых данных достаточно нужно заменить переменную "${id}" на уникальную


INSERT INTO f_patient
(id, area_id, changed_date, birth_date, update_birth_date)
VALUES(${id}, 44, '2020-04-19 10:24:54.000', '2004-06-16', NULL);




INSERT INTO f_attachment
(id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor)
VALUES(${id}1, ${id}, 27, '2021-03-09', NULL, 14484, 128290661, '602', 125676819, false); -- прикрепление к детской группе ДН
INSERT INTO f_attachment
(id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor)
VALUES(${id}2, ${id}, 27, '2021-03-09', NULL, 14382, 128290661, '69', 125676819, NULL); -- прикрепление к детской группе ДН
INSERT INTO f_attachment
(id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor)
VALUES(${id}3, ${id}, 27, '2021-03-09', NULL, 14382, 128290661, '69', 125676819, true); -- прикрепление к детской группе ДН
INSERT INTO f_attachment
(id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor)
VALUES(${id}4, ${id}, 2, '2021-03-09', NULL, 14382, 128287720, '69', 125676819, false); -- прикрепление к взрослой группе ДН
 
INSERT INTO f_attachment_event
(id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor)
VALUES(${id}1, ${id}1, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128290661, '69', 125676819, NULL, 'обрабатывает проверка no_doctor', false);
INSERT INTO f_attachment_event
(id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor)
VALUES(${id}2, ${id}2, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128290661, '69', 125676819, NULL, 'обрабатывает проверка no_doctor', NULL);
INSERT INTO f_attachment_event
(id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor)
VALUES(${id}3, ${id}3, '2021-04-12 14:45:33.585', '35c8cf53-4a90-4720-b51d-55ec397c4a38', '14484', 128290661, '602', 125676819, NULL, 'обрабатывает проверка no_doctor', true);
INSERT INTO f_attachment_event
(id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor)
VALUES(${id}4, ${id}4, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128287720, '69', 125676819, NULL, 'не обрабатывает, взрослый диагноз', false);