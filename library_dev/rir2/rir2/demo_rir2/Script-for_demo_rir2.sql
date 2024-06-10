--2.1.3.8 ������������� ���������� ���������� ���������� � �������� ���2
SELECT * FROM TREATMENT_ROOM_KIND ORDER BY id desc

SELECT * FROM TREATMENT_CATEGORY ORDER BY id DESC

SELECT * FROM TREATMENT ORDER BY id DESC

SELECT * FROM SPECIALIZATION ORDER BY id DESC

SELECT * FROM PRODUCTION_CALENDAR ORDER BY id DESC

SELECT * FROM DEPARTMENT_NOM ORDER BY id DESC

SELECT * FROM POSITION_NOM ORDER BY id DESC

SELECT * FROM AGE_GROUP ORDER BY id DESC

--2.1.3.7 ��������� ����������� ��������� �� ��������� ����������� ������� ��� ������������ ������ ����������� ����� ������������ ������������ �����
SELECT * FROM MEDICAL_ORGANIZATION WHERE ID= (SELECT MAX(ID) FROM MEDICAL_ORGANIZATION)

SELECT * FROM MEDICAL_FACILITY WHERE ID= (SELECT MAX(ID) FROM MEDICAL_FACILITY)

SELECT * FROM LOCATION WHERE ID= (SELECT MAX(ID) FROM LOCATION)

SELECT * FROM JOB_EXECUTION WHERE ID= (SELECT MAX(ID) FROM JOB_EXECUTION)

SELECT * FROM "RESOURCE" WHERE PARENT_ID= (SELECT MAX(ID) FROM JOB_EXECUTION)

SELECT * FROM EQUIPMENT WHERE ID= (SELECT MAX(ID) FROM EQUIPMENT)

SELECT * FROM EQUIPMENT_TREATMENT WHERE EQUIPMENT_ID = (SELECT MAX(ID) FROM EQUIPMENT)

SELECT * FROM "RESOURCE" WHERE PARENT_ID= (SELECT MAX(ID) FROM EQUIPMENT)

--2.1.3.7 ��������� ����������� ��������� �� ��������� ����������� ������� ��� ������������ ������ ����������� ����� ������������ ������������ �����
SELECT * FROM MEDICAL_ORGANIZATION WHERE ID= 200001798717

SELECT * FROM MEDICAL_FACILITY WHERE ID= 200001798717

SELECT * FROM LOCATION WHERE ID= 6788

SELECT * FROM JOB_EXECUTION WHERE ID= 66399669

SELECT * FROM "RESOURCE" WHERE PARENT_ID= 66399669

SELECT * FROM EQUIPMENT WHERE ID=2002

SELECT * FROM EQUIPMENT_TREATMENT WHERE EQUIPMENT_ID = 2002

SELECT * FROM "RESOURCE" WHERE PARENT_ID= 2002

---2.1.3.3 ���������� ����������� �� � ��, ������������ �������� � ����������� ��
SELECT
	*
FROM
	SCHEDULE s
WHERE
	MEDICAL_FACILITY_ID IN (
	SELECT
		id
	FROM
		MEDICAL_FACILITY mf
	WHERE
		MEDICAL_ORGANIZATION_ID = 141 )
ORDER BY
	START_DATE DESC, MEDICAL_FACILITY_ID 