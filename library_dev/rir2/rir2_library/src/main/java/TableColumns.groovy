class TableColumns {
    public static String ag="id,code,title,archived,\"from\",\"to\"";
    public static String mo="ID,ARCHIVED,NAME,NAME_FULL,OGRN,CLOSED,CREATED";
    public static String mf="ID, ARCHIVED, MEDICAL_ORGANIZATION_ID, NAME_SHORT, NAME, NAME_FULL, IS_HEAD, ADDRESS_OBJECT_ID, CLOSED, CREATED";
    public static String loc="ID, ARCHIVED, ROOM_TITLE, STAGE, MEDICAL_FACILITY_ID, SPECIALIZATION_ID";
    public static String ao="ID, ARCHIVE, GLOBAL_ID, ADDRESS_STRING, UPDATED";
    public static String spec="ID,ARCHIVED,TITLE";
    public static String diagnosis="id, archived, title, code";
    public static String tc="id, archived, title, code";
    public static String t="id, archived, title, default_duration, treatment_category_id, code,communication_form_id";
    public static String posProf="id,archived,title,code,specialization_id,profile_of_primary_position,gender_id"
    public static String posProfTreatment="id, archived, position_profile_id, treatment_id,self_appointment_allowed_to_area_type"
    public static String posNom="id,title,\"start\",\"end\",resource_type_id"
    public static String resourceType="id, title, resource_kind, archived"
    public static String equipment="id,equipment_type_id,personalized_service,capacity_for_room,equipment_profile_id,medical_organization_id,medical_facility_id,department_id,department_title,department_nom_id,location_id,archived,equipment_id,equipment_model"
    public static String equipmentProfile="id,archived,resource_type_id"
    public static String equipmentTreatment="id,equipment_id,treatment_id,use_by_default"
    public static String jobExecution="id,archived,medical_organization_id,employee_id,employee_snils,employee_last_name,employee_first_name,employee_middle_name,job_execution_volume,\"start\",finish,position_nom_id,position_title,department_id,department_title,department_nom_id,specialization_id,maternity_leave,default_medical_facility_id,is_resource"
    public static String jobExecutionPosProf="job_execution_id,position_profile_id"
    public static String depNom="id,title,\"start\",\"end\""
    public static String res="id,archived,title,resource_kind,parent_id,medical_organization_id,resource_type_id,specialization_id,department_id,department_title,department_nom_id,function_kind,function_id,function_title,function_volume,medical_facility_id,location_id,sync_status,sync_status_changed,parent_type_id,scheduling_medical_facility_id"
    public static String route="id, active, need_referral, \"number\", \"document\", created, updated, receiving_treatment_category, receiving_medical_organization, properties_hash"
    public static String routeDiagnosis="route_id, diagnosis_id"
    public static String routePosProf="route_id, referral_position_profile_id"
    public static String routeRecMF="route_id, receiving_medical_facility_id"
    public static String routeRefMO="route_id, referral_medical_organization_id"
    public static String routeTreatment="route_id, receiving_treatment_id"
    public static String productionCalendar="id,\"date\",particularity,archived"
    public static String queueType="id,archived,title,short_name,code"

    private TableColumns(){}
}
