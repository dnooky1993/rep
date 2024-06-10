class PosNom {

    def mainScripts
    ResourceTypes rt

    PosNom(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
        this.rt = new ResourceTypes( context, testRunner, log)
    }

    def findAllPosNomsForJE(){
        rt.findArResourceTypes(1,1)
        rt.findSarResourceTypes(1)
        String arResTypeId = mainScripts.utils().getTcPropertyValue("arResTypeId1")
        String archivedArResTypeId = mainScripts.utils().getTcPropertyValue("archivedArResTypeId1")
        String sarResTypeId = mainScripts.utils().getTcPropertyValue("sarResTypeId1")
        mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tablePosNom", TableColumns.posNom, " 'Номенклатура должности автотест', null , null,"+arResTypeId, "posNomId", 2, "where \"end\" IS NULL and \"start\" IS NULL ")
        mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tablePosNom", TableColumns.posNom, " 'Номенклатура должности автотест', null , null,"+sarResTypeId, "posNomIdWithSarId", 1, "where \"end\" IS NULL and \"start\" IS NULL ")
        mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tablePosNom", TableColumns.posNom, " 'Номенклатура должности автотест', null , null,"+archivedArResTypeId, "posNomIdWithArchivedRT", 1, "where \"end\" IS NULL and \"start\" IS NULL ")
    }
    //найти номенклауры должности в указанном количестве для нужной архивности
    def findPosNom(int count=1, int countArchived=0, int countRT=1, Boolean ar=true, Boolean archived=false){
        List counts = archived ? [0,countRT] : [countRT,0]
        String resTypeId
        if(ar){
            rt.findArResourceTypes(counts)
            String resTypeName= archived ? "archivedArResTypeId" : "arResTypeId"
            resTypeId = mainScripts.utils().getTcPropertyValue(resTypeName)
        }
        else{
            rt.findSarResourceTypes(counts)
            String resTypeName= archived ? "archivedSarResTypeId" : "sarResTypeId"
            resTypeId = mainScripts.utils().getTcPropertyValue(resTypeName)
        }
        if(count >0){
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tablePosNom", TableColumns.posNom, " 'Номенклатура должности автотест', null , null,"+resTypeId, "posNomId", count, "where \"end\" IS NULL and \"start\" IS NULL ");
        }
        if(countArchived >0) {
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tablePosNom", TableColumns.posNom, " 'Номенклатура должности автотест',  '2019-04-19 00:00:00.000' ,  '2020-04-19 00:00:00.000',"+resTypeId, "posNomWithDatesId", countArchived, "where \"end\" IS NULL and \"start\" IS NULL ");
        }
    }
    //очистить  номенклатуры должности
    def clearPosNom(int count=1, int countArchived=0, int countRT=1, Boolean ar=true, Boolean archived=false){
        if(count >0){
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tablePosNom", "posNomId", count)
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tablePosNom", "posNomWithDatesId", countArchived)
        }
        List counts = archived ? [0,countRT] : [countRT,0]
        if(ar) rt.clearArResourceTypes(counts)
        else rt.clearSarResourceTypes(counts)
    }
    // найти несуществующий идентификатор
    def getNonExistedPosNom(int counter=1){
        String table = mainScripts.utils().getProjectPropertyValue("tablePosNom")
        mainScripts.sql().getDoesntExisted(table, "posNomId", counter)
    }
    // найти несуществующий идентификатор для пушей
    def getPosNomIdForPush(){
        rt.findArResourceTypes()
        mainScripts.sql().getDoesntExistedForPush("tablePosNom","posNomId")
    }
    // метод создания возрастных групп для пуша
    def createPosNomForPush(int count=1, int countArchived=0){
        rt.findArResourceTypes(2)
        String resTypeId = mainScripts.utils().getTcPropertyValue("arResTypeId1")
        if(count >0){
            mainScripts.sql().createEntitiesInDBForPush("tablePosNom", TableColumns.posNom, " 'Номенклатура должности автотест', null , null,"+resTypeId, "posNomId", count)
        }
        if(countArchived >0) {
            mainScripts.sql().createEntitiesInDBForPush("tablePosNom", TableColumns.posNom, " 'Номенклатура должности автотест',  '2019-04-19 00:00:00.000' ,  '2020-04-19 00:00:00.000',"+resTypeId, "posNomWithDatesId",countArchived)
        }
    }
    //получить возрастную группу из БД
    def getPosNomFromDB(Long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tablePosNom", id)
        return entity
    }
    //получить мапу возрастной группы  из БД
    def getPosNomMap(Long id){
        def entity=getPosNomFromDB(id)
        if(entity){
            def map=[
                    id : entity.id,
                    title: entity.title,
                    start: entity.start,
                    end: entity.end,
                    resTypeId: entity.resource_type_id
            ]
            return map
        }
        else return
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("posNomId", "tablePosNom")
        rt.clearAllByPropertyName()
    }
}
