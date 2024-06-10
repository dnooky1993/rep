-- не забыть поменять дату рождения у пациента (исполнилось 17 лет в текущем месяце
--- для создания новых данных достаточно нужно заменить переменную "${id}" на уникальную


INSERT INTO f_patient
(id, area_id, changed_date, birth_date, update_birth_date)
VALUES(${id}, 44, '2020-04-19 10:24:54.000', '2004-08-11', NULL);
 
INSERT INTO f_attachment
(id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor)
VALUES(${id}1, ${id}, 10, '2021-03-03', '2021-05-31', 14382, 128290661, '69', 125676819, false);
INSERT INTO f_attachment
(id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor)
VALUES(${id}2, ${id}, 567, '2021-03-09', '2021-04-12', 14484, 128290661, '602', 125676819, false);
INSERT INTO f_attachment
(id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor)
VALUES(${id}3, ${id}, 567, '2021-03-09', '2021-04-12', 14484, 128290661, '602', 125676819, NULL);
INSERT INTO f_attachment
(id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor)
VALUES(${id}4, ${id}, 567, '2021-03-09', '2021-04-12', 14484, 128290661, '602', 125676819, true);
INSERT INTO f_attachment
(id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor)
VALUES(${id}5, ${id}, 567, '2021-03-09', '2021-04-12', 14484, 12828642711, '602', 125676819, false);
INSERT INTO f_attachment
(id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor)
VALUES(${id}6, ${id}, 567, '2021-03-09', '2021-04-12', 14484, 128287720, '602', 125676819, false);
INSERT INTO f_attachment
(id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor)
VALUES(${id}7, ${id}, 567, '2021-03-09', NULL, 14484, 128290661, '602', 125676819, false);
 
INSERT INTO f_attachment_event
(id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor)
VALUES(${id}1, ${id}1, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128290661, '69', 125676819, 2, 'не обрабатывает из-за reason_id', false);
INSERT INTO f_attachment_event
(id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor)
VALUES(${id}2, ${id}2, '2021-04-12 14:45:33.585', '35c8cf53-4a90-4720-b51d-55ec397c4a38', '14484', 128290661, '602', 125676819, 4, 'обрабатывает для детей, проверка no_doctor', false);
INSERT INTO f_attachment_event
(id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor)
VALUES(${id}3, ${id}3, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128290661, '69', 125676819, 4, 'обрабатывает для детей, проверка no_doctor', NULL);
INSERT INTO f_attachment_event
(id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor)
VALUES(${id}4, ${id}4, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128290661, '69', 125676819, 4, 'обрабатывает для детей, проверка no_doctor', true);
INSERT INTO f_attachment_event
(id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor)
VALUES(${id}5, ${id}5, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 12828642711, '69', 125676819, 4, 'не обрабатывает диагноз в архиве', false);
INSERT INTO f_attachment_event
(id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor)
VALUES(${id}6, ${id}6, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128287720, '69', 125676819, 4, 'взрослый диагноз. Не обрабывает для детей', false);
INSERT INTO f_attachment_event
(id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor)
VALUES(${id}7, ${id}7, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128290661, '69', 125676819, NULL, 'не обрабатывает открыто', false);