--- дл€ создани€ новых данных достаточно в скрипте заменить "${id}" на новое во всех строках по неполному совпадению
--- создание пациента

INSERT INTO f_patient (id, area_id, changed_date, birth_date, update_birth_date) VALUES(${id}, NULL, NULL, NULL, NULL);

--- создание прикреплени€

INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor, pdo_employee_id, is_pdo, doctor_fio, repeat, speciality_name) VALUES(1${id}, ${id}, 58, '2018-02-10', NULL, 10479782, 128286429, '50', 125676831, true, NULL, NULL, 'ћоисеева ћарина ≈вгеньевна', 'диагноз установлен в предыдущ. году или ранее (-)', 'ѕедиатри€');

--- создание c_dn_justification_check. Ќе забыть указать тот же dn_document_uid, что и в сообщении
INSERT INTO c_dn_justification_check (dn_document_uid, justification_document_id, simi_received, think_received) VALUES('1${id}', '1${id}', false, false);

--- создание документа PREVENTIVE_EXAMINATION_DOCUMENT
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(1${id}, 'PREVENTIVE_EXAMINATION_DOCUMENT', ${id}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- проставлено deleted на первом шаге

INSERT INTO f_document_preventive_examination_task (id, uid) VALUES(${id}, '1${id}');

INSERT INTO f_doc_prev_exam_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(1${id}, 1${id}, 128286429, 'NEW');