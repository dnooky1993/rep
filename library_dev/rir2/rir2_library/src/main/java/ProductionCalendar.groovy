class ProductionCalendar {
    def mainScripts

    ProductionCalendar(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
    }

    def getProdCalIdForPush(){
        mainScripts.sql().getDoesntExistedForPush("tableProdCal","prodCalId")
    }

    def createProdCalForPush(Boolean archived=false){
        mainScripts.sql().createEntitiesInDBForPush("tableProdCal", TableColumns.productionCalendar, "'2018-01-02', 'HOLIDAY',"+archived.toString(),"prodCalId", 1)
    }

    def clearProdCal(){
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableProdCal", "prodCalId", 1)
    }

    def getProdCalFromDb(Long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tableProdCal", id)
        return entity
    }

    def getProdCalMap(Long id){
        def entity=getProdCalFromDb(id)
        if(entity){
            def map=[
                    id : entity.id,
                    archived: entity.archived ,
                    date: entity."date",
                    particularity: entity.particularity
            ]
            return map
        }
        else return
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("prodCalId", "tableProdCal")
    }
}
