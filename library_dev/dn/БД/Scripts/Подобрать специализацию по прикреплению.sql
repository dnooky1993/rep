select *
from d_medical_specialization dms 
join f_dispensary_obs_group fdog 
on dms.id = fdog.specialization_id
join f_attachment fa 
on fdog.id = fa.group_id 
where fa.id = 16221