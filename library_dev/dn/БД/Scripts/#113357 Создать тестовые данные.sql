--- для создания новых данных достаточно в скрипте заменить "${id}" на новое во всех строках по неполному совпадению
 
--- создание пациента для задач


INSERT INTO f_patient (id, area_id, changed_date, birth_date, update_birth_date) VALUES(${id}, 23, '2003-08-29 10:21:54.000', '2002-10-28', NULL);


--- создание прикреплений и событий
--- прикрепление с переданным uid
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(1${id}, ${id}, 430, '2018-01-01', NULL, 10503947963, 128289228, '716', 125676809, false);
--- не забыть указать тот же uid что и в сообщении
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(1${id}, 1${id}, '2021-05-14 15:41:26.473', '1${id}', '10503947963', 128289228, '716', 125676809, NULL, 'Удалено.', false);
--- событие с другой датой и uid
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(1${id}, 1${id}, '2021-05-14 15:41:25.473', '2${id}', '10503947963', 128289228, '716', 125676809, NULL, 'Удалено.', false);


--- создание c_dn_justification_check. Не забыть указать этот же dn_document_uid, что и в сообщении


INSERT INTO c_dn_justification_check (dn_document_uid, justification_document_id, simi_received, think_received) VALUES('1${id}', '1${id}', false, false);