class MedOrganizations {

    def mainScripts

    MedOrganizations(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
    }

    def findMedOrganization(int counter=1, int countArchived=0 ){
        if(counter >0){
            String values="false, 'МО автотест', 'Государственное бюджетное учреждение здравоохранения города Москвы \"МО автотест\"', '1037739121056', NULL, '1991-12-25')"
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount ("tableMO",  TableColumns.mo,  values , "moId",counter,  " where archived=false"  )
        }
        if(countArchived >0) {
            String values="true, 'Архивное МО автотест', 'Государственное бюджетное учреждение здравоохранения города Москвы \"Архивное МО автотест\"', '1037739121056', '1991-12-26', '1991-12-25')"
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount ("tableMO",  TableColumns.mo,  values , "archivedMoId",countArchived,  " where archived=true"  )
        }
    }

    def getMedOrganizationIdForPush(){
        String tableName=mainScripts.utils().getProjectPropertyValue("tableMO")
        mainScripts.sql().getDoesntExisted(tableName,"moId")
    }

    def createMedOrganizationForPush(Boolean archived=false){
        mainScripts.sql().createEntitiesInDBwithIndex("tableMO", TableColumns.mo, archived.toString()+",'МО для пуша автотест', 'Государственное бюджетное учреждение здравоохранения города Москвы \"МО для пуша автотест\"', '1037739121056', NULL, '1991-12-25'", "moId" , 1)
    }

    def createMedOrganizations(){
        def counter = mainScripts.utils().returnIntegerTCPropValue("counterMO")
        def counterArchived = mainScripts.utils().returnIntegerTCPropValue("counterArchivedMO")
        if(counter) mainScripts.sql().createEntitiesInDBwithCount("tableMO",TableColumns.mo,"false,'МО автотест', 'Государственное бюджетное учреждение здравоохранения города Москвы \"МО автотест\"', '1037739121056', NULL, '1991-12-25'","moId" , counter)
        if(counterArchived) mainScripts.sql().createEntitiesInDBwithCount("tableMO",TableColumns.mo,"true,'Архивное МО автотест', 'Государственное бюджетное учреждение здравоохранения города Москвы \"Архивное МО автотест\"', '1037739121056', NULL, '1991-12-25'","moId" , counterArchived)
    }

    def createMedOrganizationWithIndex(Boolean archived=false, int index){
        if(!archived) mainScripts.sql().createEntitiesInDBwithIndex("tableMO",TableColumns.mo,"false,'МО автотест', 'Государственное бюджетное учреждение здравоохранения города Москвы \"МО автотест\"', '1037739121056', NULL, '1991-12-25'","moId" , index)
        else mainScripts.sql().createEntitiesInDBwithIndex("tableMO",TableColumns.mo,"true,'Архивное МО автотест', 'Государственное бюджетное учреждение здравоохранения города Москвы \"Архивное МО автотест\"', '1037739121056', NULL, '1991-12-25'","archivedMoId" , index)
    }

    def getMedOrganizationFromDB(Long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tableMO", id)
        return entity
    }

    def getMedOrganizationMap(Long id){
        def entity=getMedOrganizationFromDB(id)
        if(entity){
            def map=[
                    id : entity.id,
                    archived: entity.archived ,
                    name: entity.name ,
                    fullName: entity.name_full ,
                    ogrn: entity.ogrn,
                    closed: entity.closed,
                    created: entity.created
            ]
            return map
        }
        else return
    }

    def clearMedOrganizations() {
        def counter = mainScripts.utils().returnIntegerTCPropValue("counterMO")
        def counterArchived = mainScripts.utils().returnIntegerTCPropValue("counterArchivedMO")
        if(counter) clearMedOrganization(counter,0)
        if(counterArchived) clearMedOrganization(0,counterArchived)
    }

    def clearMedOrganization(int counter=1, int countArchived=0 ){
        if(counter >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableMO", "moId", counter)
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableMO", "archivedMoId", countArchived)
        }
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("moId", "tableMO")
    }
}
