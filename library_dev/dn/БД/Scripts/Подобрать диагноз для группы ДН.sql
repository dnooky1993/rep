select fdog.id, fdog.code, fdogm.dispensary_obs_group_id, fdogm.mkb10_id, dm.code, dms.code, dms.id, dms.title, dat.code, dat.age_min, dat.age_max  
from f_dispensary_obs_group fdog
join f_disp_obs_group_mkb10 fdogm 
on fdog.id = fdogm.dispensary_obs_group_id
join d_mkb10 dm
on dm.id = fdogm.mkb10_id
join l_disp_obs_group_watched_by ldogwb 
on fdogm.id = ldogwb.disp_obs_group_mkb10_id 
join d_medical_specialization dms 
on ldogwb.medical_specialization_id = dms.id 
join d_area_type dat 
on fdog.area_type_id = dat.id 
where fdog.archived is false