--АВА_14 скрипт для проверки сообщения в топике 
SELECT DISTINCT
    fa.patient_id,
    dm.code,
    fdog.code,
    dm.title,
    fae.diagnosis_date,
    fae.dn_doctor_job_execution_id,
    fa.opened,
    fa.closed,
    dm.code,
    fae.event_date,
    fa.doctor_fio,
    fa.employee_id,
    fa.no_doctor,
    fa.repeat,
    fa.speciality_code,
    fa.speciality_name,
    dms.code,
    dms.title,
    fae.reason_id
FROM
    f_attachment fa
    JOIN f_dispensary_obs_group fdog ON fdog.id = fa.group_id
    JOIN d_area_type dat ON fdog.area_type_id = dat.id
    JOIN d_mkb10 dm ON dm.id = fa.mkb10_id
    JOIN f_attachment_event fae ON fae.attachment_id = fa.id
    JOIN d_medical_specialization dms ON fa.medical_specialization_id = dms.id
WHERE
    fa.patient_id = '8240'
    AND dat.code = '20'
    AND fa.closed IS NOT NULL
    AND fae.reason_id = '3'

--1
SELECT * FROM f_patient fp 
WHERE fp.id = '1180';

--2
SELECT
    *
FROM
    f_patient fp
WHERE
    fp.id = '6555'
    AND fp.policy_status IS NULL;

--3
SELECT
    *
FROM
    f_attachment_event fae
    JOIN f_attachment fa ON fa.id = fae.attachment_id
    JOIN f_dispensary_obs_group fdog ON fdog.id = fa.group_id
    JOIN d_area_type dat ON fdog.area_type_id = dat.id
WHERE
    fa.patient_id = '6555'
    AND fa.closed IS NOT NULL
    AND dat.code = '10'
    AND fae.reason_id = '4';

--4
SELECT
    *
FROM
    f_patient_area fpa
    JOIN d_area_type dat ON fpa.area_type_id = dat.id
WHERE
    fpa.patient_id = '1180'
    AND dat.code = '20';

--Создается если не найдено
SELECT
    *
FROM
    f_patient_area fpa
WHERE
    fpa.patient_id = '1180';

UPDATE f_patient_area
SET patient_id=${#TestCase#patientId}, area_type_id=125694882, changed_date='2021-10-20 09:56:13', area_id=12${#TestCase#areaId}, is_checked=0
WHERE area_id=1${#TestCase#areaId};

UPDATE f_patient_area
SET patient_id=${#TestCase#patientId}, area_type_id=125694880, changed_date='2020-04-19 10:24:54.000', area_id=${#TestCase#areaId}, is_checked=0
WHERE patient_id=${#TestCase#patientId};

UPDATE f_patient SET area_id=${#TestCase#areaId}, changed_date='2020-04-19 10:24:54.000', birth_date='${#TestCase#bdate}', update_birth_date=NULL WHERE id=${#TestCase#patientId};
UPDATE f_patient SET area_id=${#TestCase#areaId}, changed_date='2020-04-19 10:24:54.000', birth_date='${#TestCase#bdate18}', update_birth_date=NULL WHERE id=${#TestCase#patientId};



--7.3
SELECT
    *
FROM
    f_attachment fa
    JOIN f_dispensary_obs_group fdog ON fdog.id = fa.group_id
    JOIN d_area_type dat ON dat.id = fdog.area_type_id
WHERE
    fa.patient_id = '1180'
    AND fa.closed IS NULL
    AND dat.code = '20';

--7.4.3
SELECT
    *
FROM
    f_attachment_event fae
    JOIN f_attachment fa ON fa.id = fae.attachment_id
WHERE
    fa.patient_id = '1180'
    AND reason_id = '4';

--SELECT * FROM f_attachment fa 
--JOIN f_dispensary_obs_group fdog ON fdog.id = fa.group_id 
--JOIN d_area_type dat ON dat.id = fdog.area_type_id
--JOIN f_attachment_event fae ON fae.attachment_id = fa.id 
--WHERE fa.patient_id = '10725628';
--7.5
SELECT
    fa.patient_id,
    fa.closed,
    dat.code,
    fdog.area_type_id,
    fae.reason_id,
    dm.archived
FROM
    f_attachment_event fae
    JOIN f_attachment fa ON fa.id = fae.attachment_id
    JOIN f_dispensary_obs_group fdog ON fa.group_id = fdog.id
    JOIN d_mkb10 dm ON dm.id = fae.mkb10_id
    JOIN d_area_type dat ON dat.id = fdog.area_type_id
WHERE
    fa.patient_id = '6555'
    AND fa.closed IS NOT NULL
    AND dat.code = '20'
    AND fae.reason_id = '4'
    AND dm.archived IS FALSE
    --7.6
WITH
    attachment AS (
        SELECT
            fa.patient_id,
            fa.closed,
            fae.attachment_id,
            dat.code,
            fdog.area_type_id,
            fae.reason_id,
            dm.archived
        FROM
            f_attachment_event fae
            JOIN f_attachment fa ON fa.id = fae.attachment_id
            JOIN f_dispensary_obs_group fdog ON fa.group_id = fdog.id
            JOIN d_mkb10 dm ON dm.id = fae.mkb10_id
            JOIN d_area_type dat ON dat.id = fdog.area_type_id
        WHERE
            fa.patient_id = '6555'
            AND fae.reason_id = '4'
            AND fa.closed IS NOT NULL
            AND dm.archived IS FALSE
    )
SELECT
    *
FROM
    f_continued_obs_task fcot
    JOIN attachment a ON fcot.attachment_id = a.attachment_id;

--7.7
WITH 
    attachment 
AS 
    (
    SELECT 
        fa.patient_id,
        fa.closed,
        fae.attachment_id,
        dat.code,
        fdog.area_type_id, 
        fae.reason_id,
        dm.archived, 
        fae.mkb10_id 
    FROM 
        f_attachment_event fae 
        JOIN 
            f_attachment fa ON fa.id = fae.attachment_id 
        JOIN 
            f_dispensary_obs_group fdog ON fa.group_id = fdog.id 
        JOIN 
            d_mkb10 dm ON dm.id = fae.mkb10_id
        JOIN    
            d_area_type dat ON dat.id = fdog.area_type_id 
    WHERE 
        fa.patient_id = '6555'
            AND fa.closed IS NOT NULL 
            AND dat.code = '20'
            AND fae.reason_id = '4'
            AND dm.archived IS FALSE
    ) 
    SELECT 
        * 
    FROM 
        f_dispensary_obs_group fdog 
            JOIN d_area_type dat ON fdog.area_type_id = dat.id
            JOIN f_disp_obs_group_mkb10 fdogm ON fdogm.dispensary_obs_group_id = fdog.id
            JOIN attachment a ON fdogm.mkb10_id = a.mkb10_id
    WHERE 
        dat.code = '10'
            AND fdog.archived IS FALSE;

--7.8
SELECT
    *
FROM
    f_task ft
    JOIN d_task_type dtt ON dtt.id = ft.type_id
WHERE
    ft.patient_id = '1149'
    AND ft.closed IS NULL
    AND ft.assignee IS NULL
    AND ft.deleted IS FALSE;

SELECT
    fcot.id,
    fcot.attachment_id,
    fcot.mkb10_id,
    fcot.status_id
FROM
    f_continued_obs_task fcot
    LEFT JOIN f_task ft ON fcot.id = ft.id
WHERE
    ft.patient_id = '1520'
    --8 
SELECT
    *
FROM
    f_task ft
    JOIN d_task_type dtt ON dtt.id = ft.type_id
    JOIN d_area_type dat ON dat.id = ft.area_type_id
WHERE
    dtt.task_code = 'AREA'
    AND dat.code = '10'
    AND ft.patient_id = '26396014'
    AND ft.closed IS NULL
    AND ft.deleted IS FALSE;

--10
SELECT
    *
FROM
    f_attachment fa
    JOIN f_dispensary_obs_group fdog ON fdog.id = fa.group_id
    JOIN d_area_type dat ON fdog.area_type_id = dat.id
WHERE
    fa.patient_id = '28196115'
    AND fa.closed IS NULL
    AND dat.code = '20';

--11
SELECT
    *
FROM
    f_task ft
WHERE
    ft.patient_id = '10725628'
    AND ft.type_id = '3'
    AND ft.closed IS NULL
    AND ft.assignee IS NULL
    AND ft.deleted IS FALSE;

SELECT
    *
FROM
    f_continued_obs_task fcot
WHERE
    fcot.id = '6875886'

--Узнаем группу взрослая/детская 
select
    fdog.id,
    fdog.code,
    fdogm.dispensary_obs_group_id,
    fdogm.mkb10_id,
    dm.code,
    dms.code,
    dms.id,
    dms.title,
    dat.code,
    dat.age_min,
    dat.age_max
from
    f_dispensary_obs_group fdog
    join f_disp_obs_group_mkb10 fdogm on fdog.id = fdogm.dispensary_obs_group_id
    join d_mkb10 dm on dm.id = fdogm.mkb10_id
    join l_disp_obs_group_watched_by ldogwb on fdogm.id = ldogwb.disp_obs_group_mkb10_id
    join d_medical_specialization dms on ldogwb.medical_specialization_id = dms.id
    join d_area_type dat on fdog.area_type_id = dat.id
where
    fdog.archived is FALSE