class AgeGroups {

    def mainScripts

    AgeGroups(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
    }

    //найти возрастные группы в указанном количестве для нужной архивности
    def findAgeGroups(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableAG", TableColumns.ag, " 'code_autotest', 'Возрастная группа автотест', false, 300, null", "ageGroupId", count, "where archived <> true ");
        }
        if(countArchived >0) {
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableAG", TableColumns.ag, " 'code_autotest_archived', 'Возрастная архивная группа автотест', true, 300, 4000", "archivedAgeGroupId", countArchived, "where archived = true ");
        }
    }
    //очистить  возрастные группы
    def clearAgeGroups(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableAG", "ageGroupId", count)
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableAG", "archivedAgeGroupId", countArchived)
        }
    }
    // найти несуществующий идентификатор
    def getNonExistedAgeGroups(int counter=1){
        String table = mainScripts.utils().getProjectPropertyValue("tableAG")
        mainScripts.sql().getDoesntExisted(table, "ageGroupId", counter)
    }
    // найти несуществующий идентификатор для пушей
    def getAgeGroupIdForPush(){
        mainScripts.sql().getDoesntExistedForPush("tableAG","ageGroupId")
    }
    // метод создания возрастных групп для пуша
    def createAgeGroupsForPush(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().createEntitiesInDBForPush("tableAG", TableColumns.ag, " 'code_autotest', 'Возрастная группа автотест', false, 300, null", "ageGroupId", count)
        }
        if(countArchived >0) {
            mainScripts.sql().createEntitiesInDBForPush("tableAG", TableColumns.ag, " 'code_autotest_archived', 'Возрастная архивная группа автотест', true, 300, 4000","archivedAgeGroupId",countArchived)
        }
    }
    //получить возрастную группу из БД
    def getAgeGroupsFromDB(Long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tableAG", id)
        return entity
    }
    //получить мапу возрастной группы  из БД
    def getAgeGroupsMap(Long id){
        def entity=getAgeGroupsFromDB(id)
        if(entity){
            def map=[
                    id : entity.id,
                    code: entity.code ,
                    title: entity.title,
                    archived: entity.archived,
                    from: entity.from,
                    to: entity.to
            ]
            return map
        }
        else return
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("ageGroupId", "tableAG")
    }
}
