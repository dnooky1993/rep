class DepNom {

    def mainScripts

    DepNom(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
    }

    //найти отделения в указанном количестве для нужной архивности
    def findDepNom(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableDepNom", TableColumns.depNom, " 'Отделение автотест', '2018-11-02', NULL", "depNomId", count, "where \"end\" is null ");
        }
        if(countArchived >0) {
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableDepNom", TableColumns.depNom, " 'Отделение автотест с датой окончания', '2018-12-02', '2019-12-02'", "archivedDepNomId", countArchived, "where \"end\" is not null ");
        }
    }
    //очистить  отделения
    def clearDepNom(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableDepNom", "depNomId", count)
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableDepNom", "archivedDepNomId", countArchived)
        }
    }
    // найти несуществующий идентификатор
    def getNonExistedDepNom(int counter=1){
        String table = mainScripts.utils().getProjectPropertyValue("tableDepNom")
        mainScripts.sql().getDoesntExisted(table, "depNomId", counter)
    }
    // найти несуществующий идентификатор для пушей
    def getDepNomIdForPush(){
        mainScripts.sql().getDoesntExistedForPush("tableDepNom","depNomId")
    }
    // метод создания отделения для пуша
    def createDepNomForPush(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().createEntitiesInDBForPush("tableDepNom", TableColumns.depNom, " 'Отделение автотест', '2018-11-02', NULL", "depNomId", count)
        }
        if(countArchived >0) {
            mainScripts.sql().createEntitiesInDBForPush("tableDepNom", TableColumns.depNom, " 'Отделение автотест с датой окончания', '2018-12-02', '2019-12-02'", "archivedDepNomId", countArchived)
        }
    }
    //получить отделение из БД
    def getDepNomFromDB(Long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tableDepNom", id)
        return entity
    }
    //получить мапу отделения  из БД
    def getDepNomMap(Long id){
        def entity=getDepNomFromDB(id)
        if(entity){
            def map=[
                    id : entity.id,
                    start: entity.start ,
                    title: entity.title,
                    end: entity.end
            ]
            return map
        }
        else return
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("depNomId", "tableDepNom")
    }
}
