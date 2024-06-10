select fae.id as event_id, fae.attachment_id, fae.document_uid, fp.id as patient_id
from f_attachment_event fae 
join f_attachment fa on fae.attachment_id = fa.id 
join f_patient fp on fa.patient_id = fp.id 
where fae.attachment_id not in (select attachment_id from f_continued_obs_task fcot)
order by fp.id desc 