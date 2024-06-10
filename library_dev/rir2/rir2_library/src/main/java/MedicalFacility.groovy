class MedicalFacility {

    def mainScripts
    MedOrganizations mo

    MedicalFacility(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
        this.mo = new MedOrganizations( context, testRunner, log)
    }

    def getNonexistentAOId(){
        String tableName=mainScripts.utils().getProjectPropertyValue("tableAO")
        mainScripts.sql().getDoesntExisted(tableName,"aoId")
    }

    def createAO(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().createEntitiesInDBwithCount("tableAO", TableColumns.ao, " false, 73086779, 'город Москва, улица Родионовская, дом 10, корпус 2', NULL", "aoId", count)
        }
        if(countArchived >0) {
            mainScripts.sql().createEntitiesInDBwithCount("tableAO", TableColumns.ao, " true, 73086779, 'город Москва, улица Родионовская, дом 10, корпус 2', NULL", "archivedAoId", countArchived)
        }
    }

    def getAOFromDB(Long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tableAO", id)
        return entity
    }

    def getAOMap(Long id){
        def entity=getAOFromDB(id)
        if(entity){
            def map=[
                    id : entity.id,
                    archived: entity.archive ,
                    globalId: entity.global_id ,
                    address: entity.address_string ,
                    updated: entity.updated
            ]
            return map
        }
        else return
    }

    def compareAOMaps(Map mapReq, Map mapDb, Boolean checkUpdatedDate=true){
        if(checkUpdatedDate){
            def dbDate=mapDb.updated
            def reqDate=mapReq.updated
            mapReq.remove("updated")
            mapDb.remove("updated")
            if(reqDate > dbDate){
                mainScripts.utils().fail("Значение параметра updated не соответствует ожидаемому")
                mainScripts.utils().loger("Значение в запросе = "+reqDate+", значение в БД = "+dbDate)
            }
        }
        mainScripts.utils().compareMaps(mapReq,mapDb)
    }

    def clearAO(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableAO", "aoId", count)
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableAO", "archivedAoId", countArchived)
        }
    }

    def getMedicalFacilityIdForPush(boolean needAO=false){
        mo.createMedOrganizations()
        if(needAO) createAO()
        else getNonexistentAOId()
        String tableName=mainScripts.utils().getProjectPropertyValue("tableMF")
        mainScripts.sql().getDoesntExisted(tableName,"mfId")
    }

    def createMedicalFacilityForEsu(Boolean archived=false, Boolean isHead=false, Boolean needAo=false, int index=1 ){
        mo.createMedOrganizations()
        Integer count = needAo ? 2 : 1
        createAO(count)
        Long aoId= needAo ? mainScripts.utils().returnLongTCPropValue("aoId1") : null
        Long moId=  mainScripts.utils().returnLongTCPropValue("moId1")
        mainScripts.utils().loger("aoId "+aoId.getClass()+" moId "+moId.getClass())
        createMF(moId,archived,isHead,aoId,index)
    }

    def createMF(Long moId, Boolean archived=false, Boolean isHead=false, Long aoId=null, int index=1 , String paramName=""){
        mainScripts.sql().createEntitiesInDBwithIndex("tableMF", TableColumns.mf, archived.toString()+", "+moId.toString()+", 'короткое имя МУ автотест ', 'обычное имя МУ автотест ', 'полное имя МУ автотест ',  "+isHead.toString()+",  "+aoId+", NULL, '1991-12-25'", paramName+"mfId" , index)
    }

    def createMedFacilities(def options){
        options.eachWithIndex{it,index->
            int i=index+1
            createMF( it.moId, it.archived, it.isHead, it.aoId, i, it.paramName )
        }
    }

    def createMfAndMo(def mfOptions,int moIndex=1, Boolean moArchived=false){
        mo.createMedOrganizationWithIndex(moArchived,moIndex)
        String moPropertyName = moArchived ? "moId"+moIndex.toString() : "archivedMoId".toString()
        Long moId= mainScripts.utils().returnLongTCPropValue(moPropertyName)
        mfOptions.eachWithIndex{it,index->
            int i=index+1
            createMF( moId, it.archived, it.isHead, it.aoId, i, it.paramName )
        }
    }

    def createDefaultMf(Long moId, int counter=1, String paramName=""){
        createAO()
        Integer aoId =  mainScripts.utils().returnLongTCPropValue("aoId1")
        for(int i = 1; i <= counter; i++){
            Boolean isHead = i == 1
            createMF( moId, false, isHead, aoId, i, paramName )
        }
    }

    def createDefaultMfAndMo(int counterMF=1, String mfParamName=""){
        mo.createMedOrganizationWithIndex(false,1)
        Long moId= mainScripts.utils().returnLongTCPropValue("moId1")
        createDefaultMf(moId,counterMF,mfParamName)
    }

    def createDefaultMfAndMoWithPropertyCounter(String mfParamName=""){
        int counter = mainScripts.utils().returnIntegerTCPropValue("counterMF")
        createDefaultMfAndMo(counter,mfParamName)
    }

    def getMedicalFacilityFromDB(Long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tableMF", id)
        return entity
    }

    def getMedicalFacilityMap(Long id){
        def entity=getMedicalFacilityFromDB(id)
        if(entity){
            def map=[
                    id : entity.id,
                    archived: entity.archived ,
                    moId: entity.medical_organization_id ,
                    name: entity.name ,
                    shortName: entity.name_short ,
                    fullName: entity.name_full ,
                    isHead: entity.is_head,
                    aoId: entity.address_object_id,
                    closed: entity.closed,
                    created: entity.created
            ]
            return map
        }
        else return
    }

    def clearMedicalFacility(int counter=1, int countArchived=0, int countAO=0 ){
        if(counter >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableMF", "mfId", counter)
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableMF", "archivedMfId", countArchived)
        }
        if(countAO >0) clearAO(countAO)
        mo.clearMedOrganizations()
    }

    def clearMedicalFacilities(int counterAO=0, String paramName="") {
        def counter = mainScripts.utils().returnIntegerTCPropValue("counterMF")
        def counterArchived = mainScripts.utils().returnIntegerTCPropValue("counterArchivedMF")
        if(counter) mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableMF", paramName+"mfId", counter)
        if(counterArchived) mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableMF", paramName+"archivedMfId", counterArchived)
        if(counterAO >0) clearAO(counterAO)
        mo.clearMedOrganizations()
    }

    def clearDefaultMfAndMo(int counterMf=0, String mfParamName=""){
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableMF", mfParamName+"mfId", counterMf)
        clearAO(1)
        mo.clearMedOrganization(1)
    }

    def clearDefaultMfAndMoWithPropertyCounter(String mfParamName="") {
        int counter = mainScripts.utils().returnIntegerTCPropValue("counterMF")
        clearDefaultMfAndMo(counter, mfParamName)
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("mfId", "tableMF")
        mainScripts.sql().clearPropertiesByPropertyName("aoId", "tableAO")
        mo.clearAllByPropertyName()
    }
}
