--А_ВА_15
----rac: 70
----racMG: 2
--1
SELECT
    *
FROM
    f_patient fp
WHERE
    fp.id = '434';

--2
SELECT
    *
FROM
    d_observation_cancel_reason docr;

-- reasonId == 4
--3
SELECT
    *
FROM
    f_patient_area fpa
WHERE
    fpa.patient_id = '434';

-- удаляется + в areaTypeId записывается значение area_type_id
--4
SELECT
    *
FROM
    f_task ft
WHERE
    ft.patient_id = '434'
    AND ft.closed IS NULL
    AND ft.deleted IS FALSE --алгорит изменяет это поле на true
    AND ft.area_type_id = '125694882';

--5
SELECT
    *
FROM
    f_task ft
WHERE
    ft.patient_id = '434'
    AND ft.closed IS NULL
    AND ft.deleted IS TRUE;

--должно вернуться 5 задач
--7
SELECT
    *
FROM
    f_attachment fa
WHERE
    fa.patient_id = '1180'
    AND fa.closed IS NULL;

SELECT
    *
FROM
    f_dispensary_obs_group fdog
    JOIN f_attachment fa ON fdog.id = fa.group_id
WHERE
    fa.patient_id = '1180'
    AND fa.closed IS NULL
    --8 
SELECT
    *
FROM
    f_dispensary_obs_group fdog
    JOIN f_attachment fa ON fdog.id = fa.group_id
WHERE
    fa.patient_id = '1180'
    AND fa.closed IS NOT NULL -- изменяется на closed = текущая дата. возвращается 4 задачи 
    --9 
SELECT
    *
FROM
    f_attachment_event fae
    JOIN f_attachment fa ON fa.id = fae.attachment_id
WHERE
    fa.patient_id = '1180'
    -----------------------------------------------------------------------------------------------------------------------
    --А_ВА_15
    ----rac: 20
    ----racMG: 3
    --1
SELECT
    *
FROM
    f_patient fp
WHERE
    fp.id = '1433';

--2
SELECT
    *
FROM
    d_observation_cancel_reason docr;

-- reasonId == 4
--3
SELECT
    *
FROM
    f_patient_area fpa
WHERE
    fpa.patient_id = '1433';

-- удаляется + в areaTypeId записывается значение area_type_id
--4
SELECT
    *
FROM
    f_task ft
WHERE
    ft.patient_id = '1180'
    AND ft.closed IS NULL
    AND ft.deleted IS TRUE --алгорит изменяет это поле
;

--7 
SELECT
    *
FROM
    f_attachment fa
    JOIN f_dispensary_obs_group fdog ON fdog.id = fa.group_id
WHERE
    fa.patient_id = '1433'
    AND fdog.area_type_id = '125694882'
    AND fa.closed IS NOT NULL;

--9 
SELECT
    *
FROM
    f_attachment_event fae
    JOIN f_attachment fa ON fae.attachment_id = fa.id
WHERE
    fa.patient_id = '1433'
    --10 
SELECT
    *
FROM
    f_attachment fa
WHERE
    fa.patient_id = '1433'
    AND fa.closed IS NULL;

--выполняется переход на ава07 т.к reasonId == 4
--А_ВА_07 
--2
SELECT
    *
FROM
    f_document fd
WHERE
    fd.patient_id = '2764'
    AND fd.deprecated IS FALSE
SELECT
    dtt.document_code
FROM
    d_task_type dtt
    JOIN f_document fd ON dtt.id = fd.type_id
WHERE
    fd.patient_id = '2764'
    --3.1
SELECT
    *
FROM
    f_document_mkb10 fdm
    JOIN f_document fd ON fdm.document_id = fd.id
WHERE
    fd.patient_id = '2764'
    AND fd.deprecated IS FALSE
    AND fdm.task_id IS NOT NULL
    --3.1.1b
SELECT
    *
FROM
    f_task ft
    JOIN f_document_mkb10 fdm ON ft.id = fdm.task_id
WHERE
    ft.patient_id = '4783'
    AND ft.closed IS NULL
    AND ft.deleted IS FALSE
SELECT
    ft.id AS task_id,
    ft.opened,
    ft.closed,
    ft.type_id,
    ft.area_type_id,
    fdm.status_id,
    fdm.mkb10_id AS diagnisis,
    fdm.document_id,
    fd.uid
FROM
    f_document_mkb10 fdm
    JOIN f_document fd ON fd.id = fdm.document_id
    JOIN f_task ft ON ft.id = fdm.task_id
    JOIN d_task_type dtt ON ft.type_id = dtt.id
WHERE
    fd.patient_id = 3581
    AND fd.uid = '10615'
SELECT
    *
FROM
    f_document fd
WHERE
    fd.uid = '113979'
SELECT
    *
from
    f_task
    left join f_document fd on f_task.patient_id = fd.patient_id
where
    f_task.patient_id = ""
    and fd.uid = '9f133c19-f164-423c-88f0-a9628b1e278'
    --выполняется переход на ава02 
    --А_ВА_07 
    --2, 3
SELECT
    *
FROM
    f_patient fp
WHERE
    fp.id = '2764'
    AND fp.birth_date IS NOT NULL
    AND fp.policy_status IS NULL;

--4
SELECT
    ft.id,
    dtt.id,
    dtt.document_code
FROM
    f_task ft
    JOIN f_document_mkb10 fdm ON ft.id = fdm.task_id
    JOIN d_task_type dtt ON ft.type_id = dtt.id
WHERE
    ft.patient_id = '2764'
    AND ft.closed IS NULL
    AND ft.deleted IS FALSE
    --6 
SELECT
    fdogm.mkb10_id,
    fdog.area_type_id
FROM
    f_disp_obs_group_mkb10 fdogm
    JOIN f_dispensary_obs_group fdog ON fdogm.dispensary_obs_group_id = fdog.id
SELECT
    *
FROM
    f_task ft
WHERE
    ft.closed IS NULL
    AND ft.deleted IS FALSE
    AND ft.patient_id = '3885';

SELECT
    *
from
    f_task ft
    join f_document_mkb10 fdm on ft.id = fdm.task_id
    join f_document fd on fdm.document_id = fd.id
where
    ft.patient_id = ""
    and ft.type_id = 4
    and fd.uid = '2edb2286-d140-4857-b2b5-02a298733899'