SELECT *
from f_task ft 
join f_document_mkb10 fdm on ft.id = fdm.task_id
join f_document fd on fdm.document_id = fd.id
where ft.patient_id = 278