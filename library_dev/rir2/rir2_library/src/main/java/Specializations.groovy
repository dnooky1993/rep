class Specializations {

    def mainScripts

    Specializations(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
    }

    //метод поиска специализаций в указанном количестве
    def findSpecializations(int counter=1, int countArchived=0 ){
        if(counter >0){
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableSpec", TableColumns.spec, "false,'Специализация автотест РиР.2'", "specId", counter, "where archived = false")
        }
        if(countArchived >0) {
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableSpec", TableColumns.spec, "true,'Архивная специализация автотест РиР.2'", "archivedSpecId", countArchived, "where archived = true")
        }
    }

    // метод получения списка идентификаторов не архивных специализаций
    def getSpecIdList(){
        String table=mainScripts.utils().getProjectPropertyValue("tableSpec")
        List specialities = mainScripts.sql().returnQueryResult("select id from "+table+" where archived = false")
        return specialities.id
    }

    // метод очистки архивных специализаций
    def clearSpecializations(int counter=1, int countArchived=0){
        if(counter >0){
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableSpec", "specId", counter)
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableSpec", "archivedSpecId", countArchived)
        }
    }

    def getSpecializationIdForPush(){
        mainScripts.sql().getDoesntExistedForPush("tableSpec","specId")
    }
    // метод создания специализации для пуша
    def createSpecializationsForPush(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().createEntitiesInDBForPush("tableSpec", TableColumns.spec, "false,'Специализация автотест РиР.2'" , "specId", count)
        }
        if(countArchived >0) {
            mainScripts.sql().createEntitiesInDBForPush("tableSpec", TableColumns.spec, "true,'Архивная специализация автотест РиР.2'" , "archivedSpecId", countArchived)
        }
    }
    //получить специализацию из БД
    def getSpecializationFromDB(Long id){
        def spec=mainScripts.sql().returnEntityFromDBbyId("tableSpec", id)
        return spec
    }
    //получить мапу специализации из БД
    def getSpecializationMap(Long id){
            def spec=getSpecializationFromDB(id)
            if(spec){
                def map=[
                        id : spec.id,
                        archived: spec.archived ,
                        title: spec.title
                ]
                return map
            }
            else return
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("specId", "tableSpec")
    }
}