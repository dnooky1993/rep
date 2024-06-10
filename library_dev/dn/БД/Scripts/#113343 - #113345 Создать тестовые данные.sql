--- ��� �������� ����� ������ ���������� � ������� �������� "${id}" �� ����� �� ���� ������� �� ��������� ����������
 
--- �������� �������� ��� �����

INSERT INTO f_patient (id, area_id, changed_date, birth_date, update_birth_date) VALUES(${id}, 23, '2003-08-29 10:21:54.000', '2002-10-28', NULL);

--- �������� ������������ � �������
--- ������������ � ���������� uid. ��������� ��� ������
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(1${id}, ${id}, 430, '2018-01-01', NULL, 10503947963, 128289228, '716', 125676809, false);
--- �� ������ ������� ��� �� uid ��� � � ���������
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(1${id}, 1${id}, '2021-05-14 15:41:26.473', '1${id}', '10503947963', 128289228, '716', 125676809, NULL, '�������.', false);

--- event � ������ uid � ����� ������ �����. �������� ������ �� ���� ������
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(2${id}, 1${id}, '2021-05-14 15:41:25.473', '2${id}', '10503947964', 128289228, '716', 125676809, NULL, '�������� ������.', true);

--- ������ ������������ � ������ uid
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(3${id}, ${id}, 430, '2018-01-01', NULL, 10503947963, 128289228, '716', 125676809, false);
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(4${id}, 3${id}, '2021-05-14 15:41:24.473', '2c932b2e-f426-438d-9395-9aadd9fe1b41', '10503947963', 128289228, '716', 125676809, NULL, '���������.', false);

--- �������� c_dn_justification_check. �� ������ ������� ��� �� dn_document_uid, ��� � � ���������

INSERT INTO c_dn_justification_check (dn_document_uid, justification_document_id, simi_received, think_received) VALUES('1${id}', '1${id}', false, false);

--- �������� ��������� DIGITIZED_DOCUMENT
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(115411, 'DIGITIZED_DOCUMENT', 15411, '2021-07-02 15:37:59.000', NULL, NULL, 30, false);

INSERT INTO f_document_digitized_task (id, uid) VALUES(${id}, '${id}');

INSERT INTO f_doc_dig_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(1${id}, ${id}, 128286429, 'NEW');


--- �������� ��������� HOSPITAL_DISCHARGE_DOCUMENT
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(115411, 'HOSPITAL_DISCHARGE_DOCUMENT', 15411, '2021-07-02 15:37:59.000', NULL, NULL, 30, false);

INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(${id}, '${id}');

INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(1${id}, ${id}, 128286429, 'NEW');

--- �������� ��������� PREVENTIVE_EXAMINATION_DOCUMENT
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(115411, 'PREVENTIVE_EXAMINATION_DOCUMENT', 15411, '2021-07-02 15:37:59.000', NULL, NULL, 30, false);

INSERT INTO f_document_preventive_examination_task (id, uid) VALUES(${id}, '${id}');

INSERT INTO f_doc_prev_exam_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(1${id}, ${id}, 128286429, 'NEW');