
INSERT INTO f_patient (id, area_id, changed_date, birth_date, update_birth_date) VALUES(${#TestCase#patientId}, ${#TestCase#areaId}, '2003-08-29 10:21:54.000', '${#TestCase#bDate}', NULL);


--- �������� ������������ ��� �������� �� ��������

INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(1${#TestCase#areaId}, ${#TestCase#patientId}, 567, '2021-03-09', NULL, 14484, 128290661, '602', 125676819, false);
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(2${#TestCase#areaId}, ${#TestCase#patientId}, 567, '2021-03-09', NULL, 14484, 128290661, '602', 125676819, NULL);
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(3${#TestCase#areaId}, ${#TestCase#patientId}, 567, '2021-03-09', NULL, 14484, 128290661, '602', 125676819, true);
INSERT INTO f_attachment (id, patient_id, group_id, opened, closed, employee_id, mkb10_id, speciality_code, medical_specialization_id, no_doctor) VALUES(4${#TestCase#areaId}, ${#TestCase#patientId}, 567, '2021-03-09', '2021-03-09', 14484, 128290661, '602', 125676819, false); --- ������������ �� ��������������, �.�. �������
 
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(1${#TestCase#areaId}, 1${#TestCase#areaId}, '2021-04-12 14:45:33.585', '35c8cf53-4a90-4720-b51d-55ec397c4a38', '14484', 128290661, '602', 125676819, 4, '�����������', false);
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(2${#TestCase#areaId}, 2${#TestCase#areaId}, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128287720, '69', 125676819, 4, '�����������', NULL);
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(3${#TestCase#areaId}, 3${#TestCase#areaId}, '2021-04-12 14:45:33.585', '35c8cf53-4a90-4720-b51d-55ec397c4a38', '14484', 128290661, '602', 125676819, 4, '�����������', true);
INSERT INTO f_attachment_event (id, attachment_id, event_date, document_uid, employee_id, mkb10_id, speciality_code, medical_specialization_id, reason_id, diagnosis, no_doctor) VALUES(4${#TestCase#areaId}, 4${#TestCase#areaId}, '2021-05-31 09:41:00.646', 'c5564c1c-2d94-460b-b36b-9e040d147ac2', '14382', 128287720, '69', 125676819, 4, '�����������', false);

--- �������� ����� � ��������� ������
--- ������ ��� �������� ���� 1
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(1${#TestCase#areaId}, 'HOSPITAL_DISCHARGE_DOCUMENT', ${#TestCase#patientId}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- ����������� deleted �� ������ ����
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(2${#TestCase#areaId}, 'AREA', ${#TestCase#patientId}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- ����������� deleted �� ������ ����
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(3${#TestCase#areaId}, 'DIGITIZED_DOCUMENT', ${#TestCase#patientId}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- ����������� deleted �� ������ ����
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(4${#TestCase#areaId}, 'CONTINUED_OBS', ${#TestCase#patientId}, '2021-07-02 15:37:59.000', NULL, NULL, 30, false); -- ����������� deleted �� ������ ����
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(5${#TestCase#areaId}, 'HOSPITAL_DISCHARGE_DOCUMENT', ${#TestCase#patientId}, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false); --- ��� 1 �� ����������, �.�. �������
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(8${#TestCase#areaId}, 'AREA', ${#TestCase#patientId}, '2021-07-02 15:37:59.000', NULL, NULL, 30, true); --- ��� 1 �� ����������, �.�. deleted = true

--- ������ ��� �������� ���� 2
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(6${#TestCase#areaId}, 'HOSPITAL_DISCHARGE_DOCUMENT', ${#TestCase#patientId}, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false);
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(7${#TestCase#areaId}, 'DIGITIZED_DOCUMENT', ${#TestCase#patientId}, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false);
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(10${#TestCase#areaId}, 'PREVENTIVE_EXAMINATION_DOCUMENT', ${#TestCase#patientId}, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false);
INSERT INTO f_task (id, "type", patient_id, opened, closed, assignee, priority, deleted) VALUES(9${#TestCase#areaId}, 'HOSPITAL_DISCHARGE_DOCUMENT', ${#TestCase#patientId}, '2021-07-02 15:37:59.000', '2021-07-02 15:37:59.000', NULL, 30, false); --- ������, �� ������� ��� �������� � ���� �� ���������
 
--- ���������� ��������� ������
INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(1${#TestCase#areaId}, '2edb2286-d140-4857-b2b5-02a2987${#TestCase#areaId}');
INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(5${#TestCase#areaId}, '2edb2286-d140-4857-b2b5-02a29872${#TestCase#areaId}');
INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(6${#TestCase#areaId}, '2edb2286-d140-4857-b2b5-02a29873${#TestCase#areaId}');
INSERT INTO f_document_hospital_discharge_task (id, uid) VALUES(9${#TestCase#areaId}, '2edb2286-d140-4857-b2b5-02a29874${#TestCase#areaId}');

INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(1${#TestCase#areaId}, 1${#TestCase#areaId}, 128291744, 'NEW');
INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(2${#TestCase#areaId}, 5${#TestCase#areaId}, 128291744, 'NEW');
INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(3${#TestCase#areaId}, 6${#TestCase#areaId}, 128291744, 'NEW');
INSERT INTO f_doc_hosp_dis_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(8${#TestCase#areaId}, 9${#TestCase#areaId}, 128290661, 'NEW');


INSERT INTO f_area_task (id) VALUES(2${#TestCase#areaId});
INSERT INTO f_area_task (id) VALUES(8${#TestCase#areaId});


INSERT INTO f_document_digitized_task (id, uid) VALUES(3${#TestCase#areaId}, '9f133c19-f164-423c-88f0-a9628b1e${#TestCase#areaId}');
INSERT INTO f_document_digitized_task (id, uid) VALUES(7${#TestCase#areaId}, '9f133c19-f164-423c-88f0-a9628b1e2${#TestCase#areaId}');


INSERT INTO f_doc_dig_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(1${#TestCase#areaId}, 3${#TestCase#areaId}, 128290661, 'NEW');
INSERT INTO f_doc_dig_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(2${#TestCase#areaId}, 7${#TestCase#areaId}, 128290661, 'NEW');


INSERT INTO f_continued_obs_task (id, attachment_id, mkb10_id, status) VALUES(4${#TestCase#areaId}, 1${#TestCase#areaId}, 128290661, 'NEW');

INSERT INTO f_document_preventive_examination_task (id, uid) VALUES(10${#TestCase#areaId}, '10${#TestCase#areaId}');
INSERT INTO f_doc_prev_exam_diagnosis_processing (id, doc_task_id, mkb10_id, status) VALUES(10${#TestCase#areaId}, 10${#TestCase#areaId}, 128291744, 'NEW');


--- �������� ���������� ���������


INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('2edb2286-d140-4857-b2b5-02a29873${#TestCase#areaId}', ${#TestCase#patientId}, '2021-02-24 17:13:49.000', '�������� �������', false, NULL); --- �������� �� �������������� �� ���� 2, ���� ������
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('9f133c19-f164-423c-88f0-a9628b1e2${#TestCase#areaId}', ${#TestCase#patientId}, '2021-02-24 17:13:49.000', '������������ ����� � 030/�', false, NULL); --- �������� �� �������������� �� ���� 2, ���� ������
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('13${#TestCase#areaId}', ${#TestCase#patientId}, '2021-02-24 17:13:49.000', '������������ ����� � 030/�', false, NULL); --- �������� ������������ � �������� ���������� ������ �� 1023 DNA0149, �.�. ��� �����
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('3${#TestCase#areaId}', ${#TestCase#patientId}, '2021-02-24 17:13:49.000', '�������� �������', false, NULL); --- �������� ������������ � �������� ���������� ������ �� 2 DNA0002, �.�. ��� �����
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('2${#TestCase#areaId}', ${#TestCase#patientId}, '2021-02-24 17:13:49.000', '������������ ����� � 030/�', true, '2021-02-24 17:13:49.000'); --- �������� �� �������������� �� ���� 2, ��� ��������� ��� �����, �� deprecated = true
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('2edb2286-d140-4857-b2b5-02a29874${#TestCase#areaId}', ${#TestCase#patientId}, '2021-02-24 17:13:49.000', '�������� �������', false, NULL); --- �������� ������������ � �������� ���������� ������ ������ �� 2 DNA0002, �.�. �� ������� �������� ���� ������
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('10${#TestCase#areaId}', ${#TestCase#patientId}, '2021-02-24 17:13:49.000', '�������� �����������/���������������', false, NULL); --- �������� �� �������������� �� ���� 2, ���� ������
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('11${#TestCase#areaId}', ${#TestCase#patientId}, '2021-02-24 17:13:49.000', '�������� �����������/���������������', false, NULL); --- �������� �������������� �� ���� 2, ��� ������
INSERT INTO f_document (document_id, patient_id, signed_date, "type", deprecated, deprecated_date) VALUES('12${#TestCase#areaId}', ${#TestCase#patientId}, '2021-02-24 17:13:49.000', '�������� �����������/���������������', false, NULL); --- �������� �������������� �� ���� 2, ��� ������


INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('2edb2286-d140-4857-b2b5-02a29873${#TestCase#areaId}', 128291744);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('9f133c19-f164-423c-88f0-a9628b1e2${#TestCase#areaId}', 128290661);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('13${#TestCase#areaId}', 128290661); --- ���� �������� � ������� ������ ��
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('2${#TestCase#areaId}', 128290661);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('3${#TestCase#areaId}', 128287720); --- ���� ������ �������� ������ ��
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('2edb2286-d140-4857-b2b5-02a29874${#TestCase#areaId}', 128290661); --- ���� �������� � ������� ������ ��
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('2edb2286-d140-4857-b2b5-02a29874${#TestCase#areaId}', 128287720); --- ���� ������ �������� ������ ��
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('10${#TestCase#areaId}', 128291744);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('11${#TestCase#areaId}', 128290661);
INSERT INTO l_document_mkb10 (document_id, mkb10_id) VALUES('12${#TestCase#areaId}', 128290661);