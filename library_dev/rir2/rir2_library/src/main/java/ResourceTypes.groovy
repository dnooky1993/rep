class ResourceTypes {

    def mainScripts

    ResourceTypes(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
        //this.equipment = new Equipment(context, testRunner, log)
    }

    def findSarResourceTypes(int counter=1, int countArchived=0 ){
        if(counter >0){
            String values="'Тип ресурса для СДР автотест', 'SPECIAL_AVAILABLE_RESOURCE', false"
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount ("tableResType",  TableColumns.resourceType,  values , "sarResTypeId",counter,  " where resource_kind='SPECIAL_AVAILABLE_RESOURCE' and archived=false" )
        }
        if(countArchived >0) {
            String values="'Архивный тип ресурса для СДР автотест', 'SPECIAL_AVAILABLE_RESOURCE', true"
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount ("tableResType",  TableColumns.resourceType,  values , "archivedSarResTypeId",countArchived, " where resource_kind='SPECIAL_AVAILABLE_RESOURCE' and archived=true"  )
        }
    }

    def findAndReturnResType(Boolean sar, boolean archived=false){
        String rtIdPropertyName
        if(sar){
            if(archived){
                findSarResourceTypes()
                rtIdPropertyName="sarResTypeId1"
            }
            else{
                findSarResourceTypes(0,1)
                rtIdPropertyName="archivedSarResTypeId1"
            }
        }
        else{
            if(archived){
                findArResourceTypes()
                rtIdPropertyName="arResTypeId1"
            }
            else{
                findArResourceTypes(0,1)
                rtIdPropertyName="archivedArResTypeId1"
            }
        }
        Long resTypeId= mainScripts.utils().returnLongTCPropValue(rtIdPropertyName)
        return resTypeId
    }

    def findArResourceTypes(int counter=1, int countArchived=0 ){
        if(counter >0){
            String values="'Тип ресурса для ДР автотест', 'AVAILABLE_RESOURCE', false"
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount ("tableResType",  TableColumns.resourceType,  values , "arResTypeId",counter,  " where resource_kind='AVAILABLE_RESOURCE' and archived=false"  )
        }
        if(countArchived >0) {
            String values="'Архивный тип ресурса для ДР автотест', 'AVAILABLE_RESOURCE', true"
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount ("tableResType",  TableColumns.resourceType,  values , "archivedArResTypeId",countArchived,  " where resource_kind='AVAILABLE_RESOURCE' and archived=true"  )
        }
    }

    def clearArResourceTypes(int counter=1, int countArchived=0 ){
        if(counter >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableResType", "arResTypeId", counter)
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableResType", "archivedArResTypeId", countArchived)
        }
    }

    def clearSarResourceTypes(int counter=1, int countArchived=0, int countPush=0 ){
        if(counter >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableResType", "sarResTypeId", counter)
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableResType", "archivedSarResTypeId", countArchived)
        }
        if(countPush >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableResType", "resTypeId", countPush)
        }
    }

    //получить тип ресурса из БД
    def getResourceTypeFromDB(Long id){
        def rt=mainScripts.sql().returnEntityFromDBbyId("tableResType", id)
        return rt
    }

    //получить мапу типа ресурса  из БД
    def getResourceTypeMap(Long id){
        def rt=getResourceTypeFromDB(id)
        if(rt){
            def map=[
                    id : rt.id,
                    archived: rt.archived ,
                    title: rt.title,
                    resKind: rt.resource_kind
            ]
            return map
        }
        else return
    }

    def getResourceTypeIdForPush(){
        mainScripts.sql().getDoesntExistedForPush("tableResType","resTypeId")
    }

    def createSResourceTypeForPush(int count=1, int countArchived=0, Boolean sar=true){
        String resKind= sar ? "\'SPECIAL_AVAILABLE_RESOURCE\'" : "\'AVAILABLE_RESOURCE\'"
        if(count >0){
            mainScripts.sql().createEntitiesInDBForPush("tableResType", TableColumns.resourceType, "'Тип ресурса автотест', "+resKind+", false" , "resTypeId", count)
        }
        if(countArchived >0) {
            mainScripts.sql().createEntitiesInDBForPush("tableResType", TableColumns.resourceType, "'Тип ресурса автотест', "+resKind+", true","resTypeId", countArchived)
        }
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("resTypeId", "tableResType")
    }
}
