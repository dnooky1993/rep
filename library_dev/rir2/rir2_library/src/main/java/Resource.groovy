import groovy.sql.Sql

class Resource {
    
    def mainScripts
    MedicalFacility mf
    def e
    def je

    Resource(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
        this.mf = new MedicalFacility(context, testRunner, log)
        this.e = new Equipment(context, testRunner, log)
        this.je = new JobExecution(context, testRunner, log)
    }

    def  getResourceIdByParent(Long parentId , String rKind){
        def resource = getResourceByParent(parentId,rKind)
        if(resource) {
            return resource.id
        }
        else{
            return null
        }
    }

    def getResourceByParent(Long parentId , String rKind){
        ResourceKinds resourceKind = ResourceKinds.valueOf(rKind)
        String tableRes =mainScripts.utils().getProjectPropertyValue("tableRes");
        String getResource="select * from "+tableRes+" where RESOURCE_KIND ='"+resourceKind.toString()+"' AND PARENT_ID="+parentId
        def resource = mainScripts.sql().returnQueryFirstRow(getResource)
        if(resource) {
            buildResourceMap(resource)
        }
        else{
            return null
        }
    }

    def getResourceById(Long id){
        String tableRes =mainScripts.utils().getProjectPropertyValue("tableRes");
        String getResource="select * from "+tableRes+" where ID="+id
        def resource = mainScripts.sql().returnQueryFirstRow(getResource)
        if(resource) {
            buildResourceMap(resource)
        }
        else{
            return null
        }
    }

    def buildResourceMap(def resource){
        def formattedDate=mainScripts.dates().formatDBdate(resource.sync_status_changed.toString())
        def map=[
                id : resource.id,
                archived:resource.archived,
                title : resource.title,
                resourceKind : resource.resource_kind,
                parentId : resource.parent_id,
                moId : resource.medical_organization_id,
                resTypeId : resource.resource_type_id,
                specId : resource.specialization_id,
                depId : resource.department_id,
                depTitle : resource.department_title,
                depNomId : resource.department_nom_id,
                functionKind : resource.function_kind,
                functionId : resource.function_id,
                functionTitle : resource.function_title,
                functionVolume : resource.function_volume,
                mfId : resource.medical_facility_id,
                locId : resource.location_id,
                syncStatus : resource.sync_status,
                syncStatusChanged : formattedDate,
                parentTypeId : resource.parent_type_id,
                schedulingMedicalFacilityId : resource.scheduling_medical_facility_id
        ]
        return map
    }

    // очищает в БД записи об ошибках синхронизации ресурса
    def clearResourceSyncErrors(long resId){
        mainScripts.sql().executeQuery("delete from RESOURCE_SYNC_ERROR_MSG_PR where RESOURCE_SYNC_ERROR_MSG_ID in "+
                "( select rsem.id from RESOURCE_SYNC_ERROR rse "+
                "JOIN RESOURCE_SYNC_ERROR_MSG rsem ON RSE.ID=RSEM.RESOURCE_SYNC_ERROR_ID "+
                "WHERE rse.RESOURCE_ID = "+resId+")")
        mainScripts.sql().executeQuery("delete from RESOURCE_SYNC_ERROR_MSG where RESOURCE_SYNC_ERROR_ID in "+
                "(select id from RESOURCE_SYNC_ERROR rse WHERE rse.RESOURCE_ID ="+resId+")")
        mainScripts.sql().executeQuery("delete from RESOURCE_SYNC_ERROR rse WHERE rse.RESOURCE_ID ="+resId)
    };

    def clearResourceById(long resId){
        clearResourceSyncErrors(resId)
        String table = mainScripts.utils().getProjectPropertyValue("tableRes")
        mainScripts.sql().executeQuery("DELETE FROM "+table+ " WHERE ID =" +resId)
    }

    def clearResourceByIdPropertyName(String propertyName) {
        Long resId = mainScripts.utils().getTcPropertyValue(propertyName).toLong()
        clearResourceSyncErrors(resId)
        mainScripts.deleteCreatedByTestInTestCase("tableRes", propertyName)
    }

    def clearResourceByParent(Long parentId , String rKind){
        Long id=getResourceIdByParent(parentId , rKind)
        clearResourceById(id)
    }

    def clearSarResourceByParentId(Long parentId){
        clearResourceByParent(parentId, "SAR")
    }

    def clearArResourceByParentId(Long parentId){
        clearResourceByParent(parentId, "AR")
    }

    def createSarResourceByParentEntity(long id, String syncStatus="OK",String propertyName="resourceId",int index=1){
        def equipment=e.getEquipmentMapFromDB(id)
        String resTypeId=mainScripts.utils().getTcPropertyValue("sarResTypeId1")
        String values=equipment.archived.toString()+",'"+
                equipment.model+"',"+
                "'SPECIAL_AVAILABLE_RESOURCE',"+
                equipment.id+","+
                equipment.moId+","+
                resTypeId+","+
                "null,"+
                equipment.depId+",'"+
                equipment.depTitle+"',"+
                equipment.depNomId+","+
                "'EQUIPMENT_FUNCTION',"+
                equipment.equipmentProfileId+",'"+
                equipment.model+"',"+
                "null,"+
                equipment.mfId+","+
                equipment.locId+",'"+
                syncStatus+"',"+
                "current_timestamp,"+
                equipment.equipmentTypeId+","+
                "null"
        createResource(values, propertyName,index)
    }
    def createArResourceByParentEntity(long id, String syncStatus="OK",String propertyName="resourceId",int index=1){
        def jobExecution=je.getJobExecutionMapFromDB(id)
        String resTypeId=mainScripts.utils().getTcPropertyValue("arResTypeId1")
        String title=jobExecution.lastName.trim()+" "+jobExecution.firstName.trim()+" "+jobExecution.middleName.trim()
        String values=jobExecution.archived.toString()+",'"+
                title+"',"+
                "'AVAILABLE_RESOURCE',"+
                jobExecution.id+","+
                jobExecution.moId+","+
                resTypeId+","+
                +jobExecution.specId+","+
                jobExecution.departmentId+",'"+
                jobExecution.departmentTitle+"',"+
                jobExecution.departmentNomId+","+
                "'JOB_EXECUTION_FUNCTION',"+
                jobExecution.posNomId+",'"+
                title+"',"+
                jobExecution.volume+","+
                jobExecution.defaultMfId+","+
                "null,'"+
                syncStatus+"',"+
                "current_timestamp,"+
                jobExecution.posNomId+","+
                "null"
        createResource(values, propertyName,index)
    }

    def createResource(String values, String propertyName="resourceId",int index=1){
        mainScripts.sql().createEntitiesInDBwithIndex("tableRes", TableColumns.res,values,propertyName,index)
    }

    def checkExistenceResourceByParent(Long parentId , String rKind, boolean mustBeCreated=false){
        def res=getResourceByParent(parentId,rKind)
        Boolean exist = res ? true : false
       if (exist != mustBeCreated){
           String should = mustBeCreated ? "не" : ""
           mainScripts.utils().fail("В таблице resource "+should+" найдена запись с job_execution_id="+parentId)
       }
    }

    def clearResourcesWithErrors(String propertyName="resourceId"){
        List resIds=mainScripts.utils().collectPropertyNames(propertyName)
        resIds.each{
            Long id=mainScripts.utils().returnLongTCPropValue(it)
            clearResourceSyncErrors(id)
        }
        mainScripts.sql().clearPropertiesByPropertyName(propertyName, "tableRes")
    }

    def clearSarResourcesAndParents(String propertyName="resourceId"){
        clearResourcesWithErrors(propertyName)
        e.clearAllByPropertyName()
    }

    def clearArResourcesAndParents(String propertyName="resourceId"){
        clearResourcesWithErrors(propertyName)
        je.clearAllByPropertyName()
    }

    def clearArResourcesAndParentsByParentId(String propertyName="jeId1"){
        Long jeId=mainScripts.utils().returnLongTCPropValue(propertyName)
        clearArResourceByParentId(jeId)
        je.clearAllByPropertyName()
    }
}
