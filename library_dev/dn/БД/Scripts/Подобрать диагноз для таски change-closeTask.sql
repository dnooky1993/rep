select doc_task_id, code from f_doc_diagnosis_processing
left join d_mkb10 d on f_doc_diagnosis_processing.mkb10_id = d.id
where doc_task_id = 102