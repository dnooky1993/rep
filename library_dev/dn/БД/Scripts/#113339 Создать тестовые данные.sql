--- ��� �������� ����� ������ ���������� � ������� �������� "${id}" �� ����� �� ���� ������� �� ��������� ����������
--- �������� ��������

INSERT INTO f_patient (id, area_id, changed_date, birth_date, update_birth_date) VALUES(${id}, NULL, NULL, NULL, NULL);

--- �������� c_dn_justification_check. �� ������ ������� ��� �� dn_document_uid, ��� � � ���������

INSERT INTO c_dn_justification_check (dn_document_uid, justification_document_id, simi_received, think_received) VALUES('1${id}', '1${id}', false, false);