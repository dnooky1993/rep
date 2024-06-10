--- для создания новых данных достаточно в скрипте заменить "${id}" на новое во всех строках по неполному совпадению
 
--- создание пациента для задач


INSERT INTO f_patient (id, area_id, changed_date, birth_date, update_birth_date) VALUES(${id}, 23, '2003-08-29 10:21:54.000', '2002-10-28', NULL);




--- создание прикреплений и событий
--- прикрепление с переданным uid. Обновляет эту запись
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor, pdo_employee_id, is_pdo, doctor_fio, repeat, speciality_name) VALUES(1${id}, ${id}, 430, '2018-01-01', NULL, 10503947963, 128289228, '716', 125676809, false, NULL, NULL, 'Фамилия Имя Отчество', 'Повторно', 'тест');
--- не забыть указать тот же uid что и в сообщении
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor, dn_doctor_job_execution_id, doctor_fio, repeat, speciality_name) VALUES(1${id}, 1${id}, '2021-05-14 15:41:26.473', '1${id}', '10503947963', 128289228, '716', 125676809, NULL, 'Удаляет.', false, NULL, 'Имя отчество', 'вервые', 'тест');




--- event с другим uid и более старой датой. Забирает данные из этой записи
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor, dn_doctor_job_execution_id, doctor_fio, repeat, speciality_name) VALUES(2${id}, 1${id}, '2021-05-14 15:41:25.473', '2${id}', '10503947964', 128289228, '716', 125676809, NULL, 'Исходные данные.', true, NULL, 'Имя отчество', 'вервые', 'тест2');




--- другое прикрепление с другим uid
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor, pdo_employee_id, is_pdo, doctor_fio, repeat, speciality_name) VALUES(3${id}, ${id}, 430, '2018-01-01', NULL, 10503947963, 128289228, '716', 125676809, false, NULL, NULL, 'Фамилия Имя Отчество2', 'Повторно2', 'тест2');
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor, dn_doctor_job_execution_id, doctor_fio, repeat, speciality_name) VALUES(4${id}, 3${id}, '2021-05-14 15:41:24.473', '2c932b2e-f426-438d-9395-9aadd9fe1b41', '10503947963', 128289228, '716', 125676809, NULL, 'Оставляет.', false, NULL, 'Имя отчество', 'вервые', 'тест');


--- создание c_dn_justification_check. Не забыть указать тот же dn_document_uid, что и в сообщении


INSERT INTO c_dn_justification_check (dn_document_uid, justification_document_id, simi_received, think_received) VALUES('1${id}', '1${id}', false, false);