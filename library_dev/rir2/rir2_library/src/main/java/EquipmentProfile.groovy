class EquipmentProfile {

    def mainScripts
    ResourceTypes rt

    EquipmentProfile(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
        this.rt = new ResourceTypes( context, testRunner, log)
    }

    def getEquipmentProfileIdForPush(){
        rt.findSarResourceTypes(1)
        mainScripts.sql().getDoesntExistedForPush("tableEP","equipmentProfileId")
    }

    def createEquipmentProfileForPush(Boolean archived=false){
        rt.findSarResourceTypes(2)
        Long rtId= mainScripts.utils().returnLongTCPropValue("sarResTypeId1")
        createEPWithIndex(rtId,archived, 1)
    }

    def createEPWithIndex(Long rtId, Boolean archived=false, int index=1, String propertyName="equipmentProfileId"){
        mainScripts.sql().createEntitiesInDBwithIndex("tableEP", TableColumns.equipmentProfile, archived.toString()+","+rtId.toString(), propertyName , index)
    }

    def createEquipmentProfiles(Map params ){
        params.eachWithIndex{it,index->
            int i=index+1
            Long rtId=rt.findAndReturnResType(it.kind, it.rtArchived)
            createEPWithIndex( rtId, it.archived, i)
        }
    }

    def findEquipmentProfile(int counter=1, Boolean archived=false, String resTypePropertyName="sarResTypeId1", String propertyName="equipmentProfileId"){
        String resTypeId  = mainScripts.utils().getTcPropertyValue(resTypePropertyName)
        mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableEP", TableColumns.equipmentProfile, archived.toString()+","+resTypeId, propertyName, counter, "where archived = "+archived.toString()+" and resource_type_id="+resTypeId)
    }

    def findEquipmentProfileWithRT(int counter=1, Boolean archived=false, Boolean sar=true, String propertyName="equipmentProfileId"){
        String resTypePropertyName="sarResTypeId1"
        if(sar) rt.findSarResourceTypes()
        else{
            rt.findArResourceTypes()
            resTypePropertyName="arResTypeId1"
        }
        findEquipmentProfile(counter,archived, resTypePropertyName, propertyName)
    }

    def getEquipmentProfileFromDB(Long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tableEP", id)
        return entity
    }

    //получить мапу типа ресурса  из БД
    def getEquipmentProfileMap(Long id){
        def entity=getEquipmentProfileFromDB(id)
        if(entity){
            def map=[
                    id : entity.id,
                    archived: entity.archived ,
                    rtId: entity.resource_type_id
            ]
            return map
        }
        else return
    }

    def clearResTypeByEP( String propertyName="equipmentProfileId", int index=1){
        Long epId= mainScripts.utils().returnLongTCPropValue(propertyName+index)
        def ep=getEquipmentProfileMap(epId)
        def resType= rt.getResourceTypeMap(ep.rtId)

    }

    def clearEquipmentProfile(int count=1, List resType, String propertyName="equipmentProfileId"){
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableEP", propertyName, count)
        resType.each{
            if(it.sar){
                if(it.archived) rt.clearSarResourceTypes(0, it.count)
                else rt.clearSarResourceTypes(it.count, 0)
            }
            else{
                if(it.archived) rt.clearArResourceTypes(0, it.count)
                else rt.clearArResourceTypes(it.count, 0)
            }
        }
    }

    def clearAllByPropertyName(String propertyName="equipmentProfileId"){
        mainScripts.sql().clearPropertiesByPropertyName(propertyName, "tableEP")
        rt.clearAllByPropertyName()
    }
}
