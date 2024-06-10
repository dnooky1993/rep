class QueueType {

    def mainScripts

    QueueType(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
    }

    def findQueueType(int counter=1, int countArchived=0 ){
        if(counter >0){
            String values="false,'Тип очереди автотест ','кор назв','code_auto'"
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount ("tableQT",  TableColumns.queueType,  values , "queueTypeId",counter,  " where archived=false"  )
        }
        if(countArchived >0) {
            String values="true,'Архивный тип очереди автотест ','кор назв','code_auto'"
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount ("tableQT",  TableColumns.queueType,  values , "archivedQueueTypeId",countArchived,  " where archived=true"  )
        }
    }

    def getQueueTypeIdForPush(){
        mainScripts.sql().getDoesntExistedForPush("tableQT","queueTypeId")
    }

    def createQueueTypeForPush(Boolean archived=false){
        mainScripts.sql().createEntitiesInDBForPush("tableQT", TableColumns.queueType, archived.toString()+",'Тип очереди автотест для пушей', 'кор назв','code_auto'", "queueTypeId" , 1)
    }

    def getQueueTypeFromDB(Long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tableQT", id)
        return entity
    }

    def getQueueTypeMap(Long id){
        def entity=getQueueTypeFromDB(id)
        if(entity){
            def map=[
                    id : entity.id,
                    archived: entity.archived ,
                    title: entity.title ,
                    shortName: entity.short_name ,
                    code: entity.code
            ]
            return map
        }
        else return
    }

    def clearQueueType(int counter=1, int countArchived=0 ){
        if(counter >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableQT", "queueTypeId", counter)
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableQT", "archivedQueueTypeId", countArchived)
        }
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("queueTypeId", "tableQT")
    }
}
