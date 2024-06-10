select count(*)
from f_task ft
where ft.patient_id
          in (select fpa.patient_id from f_patient_area fpa where fpa.area_id = '5393401')
      and ft.area_type_id in (select distinct area_type_id from f_patient_area where area_id = '5393401')
  and ft.closed is null and ft.deleted = false ;