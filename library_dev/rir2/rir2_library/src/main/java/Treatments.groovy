class Treatments {

    def mainScripts

    Treatments(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
    }

    def createTC(int index=1, String propertyName,String name, String codePropertyName,String categoryCode, Boolean archived ){
        String code=categoryCode+index
        mainScripts.sql().createEntitiesInDBwithIndex("tableTC", TableColumns.tc, archived.toString()+" ,'"+name+"','"+code+"'", propertyName, index);
        mainScripts.utils().setTcPropertyValue(codePropertyName+index, code)
    }

    def createTreatmentCategories(int count=1, int countArchived=0){
        if(count >0){
            String name= "Категория ММ автотест"
            String codePropertyName= "tcCode"
            String categoryCode="autotest"
            String propertyName="tcId"
            for( int i=1; i<=count ; i++){
                createTC(i,propertyName, name, codePropertyName, categoryCode, false )
            }
        }
        if(countArchived >0) {
            String name= "Архивная категория ММ автотест"
            String codePropertyName= "archivedTcCode"
            String categoryCode="archived_category_autotest"
            String propertyName="archivedTcId"
            for( int i=1; i<=countArchived ; i++){
                createTC(i,propertyName, name, codePropertyName, categoryCode, true )
            }
        }
    }

    def clearTreatmentCategories(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableTC", "tcId", count)
            String codePropertyName= "tcCode"
            for (int i = 1; i <= count; i++) {
                mainScripts.utils().removeTcProperty(codePropertyName+i)
            }
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableTC", "archivedTcId", countArchived)
            String codePropertyName="archivedTcCode"
            for (int i = 1; i <= countArchived; i++) {
                mainScripts.utils().removeTcProperty(codePropertyName+i)
            }
        }
    }

    def createTreatmentsFromMap(def mapList , int startIndex=1){
        for(int i = 0; i <= mapList.size(); i++){
            Boolean archived = mapList.archived !=null ? mapList.archived : false
            Integer defDuration = mapList.defDuration !=null ? mapList.defDuration : 4
            Integer commFormId = mapList.commFormId !=null ? mapList.commFormId : 1204888723
            String tcId = mapList.tcId !=null ? mainScripts.utils().getTcPropertyValue(tcId) : mainScripts.utils().getTcPropertyValue("tcId1")
            int index=startIndex+i
            mainScripts.sql().createEntitiesInDBwithIndex("tableT", TableColumns.t, archived.toString()+",'ММ автотест',"+defDuration+","+tcId+",'autotest',"+commFormId, "treatmentId", index)
        }

    }

    def createTreatments(int counter=1, String tcPropertyName="tcId1", String tPropertyName="treatmentId", String communicationFormId="1204888723", String categoryCode="autotest"){
        String tcId=mainScripts.utils().getTcPropertyValue(tcPropertyName)
        mainScripts.sql().createEntitiesInDBwithCount("tableT", TableColumns.t, "false,'ММ автотест',4,"+tcId+",'autotest',"+communicationFormId, tPropertyName, counter);
    }

    def createArchivedTreatments(int counter=1, String tcPropertyName="tcIdForArchivedT", String communicationFormId="1204888723", String tPropertyName="archivedTreatmentId" ){
        createTreatmentCategories(1, tcPropertyName)
        String tcId=mainScripts.utils().getTcPropertyValue(tcPropertyName+1)
        mainScripts.sql().createEntitiesInDBwithCount("tableT", TableColumns.t, "true,'Архивное ММ автотест',4,"+tcId+",'autotest',"+communicationFormId, tPropertyName, counter);
    }

    def clearTreatments(int counter=1, Boolean archived=false ){
        String tPropertyName = archived ? "archivedTreatmentId" : "treatmentId"
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableT", tPropertyName, counter)
    }


    def clearTreatmentsWithCount(String tcPropertyName="tcId", String tPropertyName="treatmentId" ){
        int counter=mainScripts.utils().getTcPropertyValue("countTreatments").toInteger()
        if(counter >0){
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableT", tPropertyName, counter)
            clearTreatmentCategories(1, 0)
        }
    }

    def clearArchivedTreatments(int counter=1, String tcPropertyName="tcIdForArchivedT", String tPropertyName="archivedTreatmentId" ){
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableT", tPropertyName, counter)
        clearTreatmentCategories(0, 1)
    }

    def getNonExistedTC(int counter=1){
        String table = mainScripts.utils().getProjectPropertyValue("tableTC")
        mainScripts.sql().getDoesntExisted(table, "nonExistedTCId", counter)
    }

    def getNonExistedTreatments(int counter=1){
        String table = mainScripts.utils().getProjectPropertyValue("tableT")
        mainScripts.sql().getDoesntExisted(table, "nonExistedTreatmentId", counter)
    }

    def findOrCreateTreatments(int counter=1, String tcPropertyName="tcId", String tPropertyName="treatmentId", String tcCodePropertyName="tcCode1"){
        String tableT = mainScripts.utils().getProjectPropertyValue("tableT")
        String tableTC = mainScripts.utils().getProjectPropertyValue("tableTC")
        String query="select t.treatment_category_id as treatmentCategoryId, tc.code as treatmentCategoryCode , count(t.id) as treatmentCount from "+tableT+" t "+
        "join "+tableTC+" tc on tc.id=t.treatment_category_id "+
        "where t.archived =false and tc.archived =false "+
        "group by treatment_category_id,treatmentCategoryCode "+
        "order by treatmentCount desc "
        def result= mainScripts.sql().returnQueryFirstRow(query)
        String tcId=""
        String tcCode=""
        if (result){
            tcId=result.treatmentCategoryId.toString()
            tcCode=result.treatmentCategoryCode
            mainScripts.utils().loger(" В бд есть подходящая категория мм с id ="+tcId)
            mainScripts.utils().createPropertyWithCreatedByTCparameter(tcPropertyName, tcId, '0')
            mainScripts.utils().setTcPropertyValue(tcCodePropertyName,tcCode)
            def treatments=mainScripts.sql().returnQueryResult("select * from "+tableT+" where archived=false and treatment_category_id="+tcId)
            int tSize= treatments.size()
            int difference  =  counter - tSize
            int countExisted = difference >0 ? tSize : counter
            //log.info("difference "+difference+" countExisted "+countExisted)
            mainScripts.utils().loger("В бд есть "+countExisted+" подходящих ММ. Записываю их")
            for(int i=0; i<countExisted; i++){
                int n=i+1
                mainScripts.utils().createPropertyWithCreatedByTCparameter(tPropertyName, treatments[i].id.toString() , '0',n)
            }
            if (difference > 0){
                int n= countExisted+1
                //log.info("difference "+difference+" n "+n+" counter "+counter)
                for(int i=n; i<=counter; i++){
                    mainScripts.sql().createEntitiesInDBwithIndex("tableT", TableColumns.t,"false, 'ММ автотест', 9, "+tcId+", 'autotest_treat',1204888723",tPropertyName,i)
                }
            }
        }
        else{
            mainScripts.utils().loger(" В бд нет подходящей категории ММ. Создам ее и сами ММ")
            tcCode="autotest_code"
            createTreatments(1,tcPropertyName,tPropertyName,tcCode)
        }
    }

    def findTreatmentsWithTreatmentCount( String tcPropertyName="tcId", String tPropertyName="treatmentId", String tcCodePropertyName="tcCode1"){
        int count = mainScripts.utils().getTcPropertyValue("countTreatments").toInteger()
        findOrCreateTreatments(count,tcPropertyName,tPropertyName,tcCodePropertyName)
    }

    def findOrCreateTreatmentsByCategoryCode(int counter=1, String tcPropertyName="tcId", String tPropertyName="treatmentId", String categoryCode="autotest", String tcCodePropertyName="tcCode1"){
        String tableT = mainScripts.utils().getProjectPropertyValue("tableT")
        String tableTC = mainScripts.utils().getProjectPropertyValue("tableTC")
        def tc=mainScripts.sql().returnRows("tableTC"," where archived=false and code='"+categoryCode+"'")
        String tcId=""
        if (tc.size()){
            tcId=tc[0].id.toString()
            mainScripts.utils().loger(" В бд есть подходящая категория мм с id ="+tcId)
            mainScripts.utils().createPropertyWithCreatedByTCparameter(tcPropertyName, tcId, '0')
            def treatments=mainScripts.sql().returnRows("tableT"," where archived=false and treatment_category_id="+tcId)
            int tSize= treatments.size()
            if(tSize > 0){
                int difference  =  counter - tSize
                int countExisted = difference >0 ? tSize : counter
                //log.info("difference "+difference+" countExisted "+countExisted)
                mainScripts.utils().loger("В бд есть "+countExisted+" подходящих ММ. Записываю их")
                for(int i=0; i<countExisted; i++){
                    int n=i+1
                    mainScripts.utils().createPropertyWithCreatedByTCparameter(tPropertyName, treatments[i].id.toString() , '0',n)
                }
                if (difference > 0){
                    int n= countExisted+1
                    //log.info("difference "+difference+" n "+n+" counter "+counter)
                    for(int i=n; i<=counter; i++){
                        mainScripts.sql().createEntitiesInDBwithIndex("tableT", TableColumns.t,"false, 'ММ автотест', 9, "+tcId+", 'autotest_treat',1204888723",tPropertyName,i)
                    }
                }

            }
            else{
                mainScripts.sql().createEntitiesInDBwithCount("tableT", TableColumns.t,"false, 'ММ автотест', 9, "+tcId+", 'autotest_treat',1204888723",tPropertyName,counter)
            }
        }
        else{
            mainScripts.utils().loger(" В бд нет подходящей категории ММ. Создам ее и сами ММ")
            mainScripts.sql().createEntitiesInDBwithIndex("tableTC", TableColumns.tc, "false,'Категория ММ автотест','"+categoryCode+"'" , tcPropertyName)
            tcId=mainScripts.utils().getTcPropertyValue(tcPropertyName+"1")
            mainScripts.sql().createEntitiesInDBwithCount("tableT", TableColumns.t,"false, 'ММ автотест', 9, "+tcId+", 'autotest_treat',1204888723",tPropertyName,counter)
        }
        mainScripts.utils().setTcPropertyValue(tcCodePropertyName,categoryCode)
    }

    def updateDefaultCommunicationFormId(int count=1, int startIndex=1, Boolean archived=false){
        String tPropertyName = archived ? "archivedTreatmentId" : "treatmentId"
        String treatmentsToUpdate = getTreatmentsIdStringList(count,startIndex,tPropertyName)
        String table=mainScripts.utils().getProjectPropertyValue("tableT")
        String query = "update "+table+" set communication_form_id = 1204888724 where id in ("+treatmentsToUpdate+")"
        mainScripts.sql().executeQuery(query)
    }

    def getTreatmentsIdList(int count, int startIndex=1, String tPropertyName="treatmentId"){
        mainScripts.utils().getListFromProperties(count,startIndex,tPropertyName)
    }

    def getTreatmentsIdStringList(int count, int startIndex=1, String tPropertyName="treatmentId"){
        mainScripts.utils().getStringListFromProperties(count,startIndex,tPropertyName)
    }

    def getTCIdForPush(){
        mainScripts.sql().getDoesntExistedForPush("tableTC","tcId")
    }

    def createTCForPush(Boolean archived=false){
        mainScripts.sql().createEntitiesInDBForPush("tableTC", TableColumns.tc,  archived.toString()+" ,'Категория ММ для пушей автотест','code_autotest_push'" , "tcId", 1)
    }

    def getTCFromDB(Long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tableTC", id)
        return entity
    }

    def getTCMap(Long id){
        def entity=getTCFromDB(id)
        if(entity){
            def map=[
                    id : entity.id,
                    archived: entity.archived ,
                    title: entity.title,
                    code: entity.code
            ]
            return map
        }
        else return
    }

    def getTreatmentIdForPush(){
        createTreatmentCategories(1)
        mainScripts.sql().getDoesntExistedForPush("tableT","treatmentId")
    }

    def getTreatmentFromDB(Long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tableT", id)
        return entity
    }

    def createTreatmentForPush(Boolean archived=false){
        createTCForPush()
        String tcId=mainScripts.utils().getTcPropertyValue("tcId1")
        String values=archived.toString()+" ,'Медицинская манипуляция для пушей автотест',590, "+tcId+",'code_autotest_push', 12345"
        mainScripts.sql().createEntitiesInDBForPush("tableT", TableColumns.t, values, "treatmentId", 1)
    }
    def getTreatmentMap(Long id){
        def entity=getTreatmentFromDB(id)
        if(entity){
            def map=[
                    id : entity.id,
                    archived: entity.archived ,
                    title: entity.title,
                    code: entity.code,
                    tcId: entity.treatment_category_id ,
                    defDuration: entity.default_duration ,
                    commFormId: entity.communication_form_id

            ]
            return map
        }
        else return
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("treatmentId", "tableT")
        mainScripts.sql().clearPropertiesByPropertyName("tcId", "tableTC")
        mainScripts.utils().deleteTcPropertiesByNames("tcCode")
    }
}
