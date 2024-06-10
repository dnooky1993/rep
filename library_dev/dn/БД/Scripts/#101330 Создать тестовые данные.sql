--- дл€ создани€ новых данных достаточно в скрипте заменить "${id}" на новое во всех строках по неполному совпадению
 
--- создание пациента дл€ задач


INSERT INTO f_patient (id, area_id, changed_date, birth_date, update_birth_date) VALUES(${id}, 23, '2003-08-29 10:21:54.000', '2002-10-28', NULL);




--- создание прикреплений и событий
--- прикрепление с переданным uid. Ѕудет удалено алгоритмом
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(1${id}, ${id}, 430, '2018-01-01', NULL, 10503947963, 128289228, '716', 125676809, false);
--- не забыть указать тот же uid что и в сообщении
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(1${id}, 1${id}, '2021-05-14 15:41:26.473', '1${id}', '10503947963', 128289228, '716', 125676809, NULL, '”далено.', false);


--- event с другим uid и более свежей датой. Ќе будет удален алгоритмом
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(2${id}, 1${id}, '2021-05-15 15:41:27.473', '2${id}', '10503947963', 128289228, '716', 125676809, NULL, 'Ќе удалено.', false);


--- другое прикрепление с другим uid
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(3${id}, ${id}, 430, '2018-01-01', NULL, 10503947963, 128289228, '716', 125676809, false);
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(4${id}, 3${id}, '2021-05-14 15:41:26.473', '3${id}', '10503947963', 128289228, '716', 125676809, NULL, 'Ќе удалено.', false);