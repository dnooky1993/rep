import groovy.sql.Sql

class PosProf {

    def mainScripts
    Specializations spec
    Treatments t

    PosProf(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
        this.spec = new Specializations(context, testRunner, log)
        this.t = new Treatments(context, testRunner, log)
    }

    def getSpecForPosProf(int specCount=1, int archivedSpecCount=0){
        spec.findSpecializations(specCount,archivedSpecCount)
    }

    def getSpecForPosProfWithCounter(){
        int counter=mainScripts.utils().returnIntegerTCPropValue("counterSpec")
        getSpecForPosProf(counter)
    }

    def clearSpecForPosProf(int specCount=1, int archivedSpecCount=0){
        spec.clearSpecializations(specCount,archivedSpecCount)
    }

    def createPosProfiles(def params,int count=1 ,int startIndex=1, boolean push =false){
        for(int i=startIndex; i<=count; i++){
            createPosProfileWithIndex(params, i,push)
        }
    }

    def createPosProfileWithIndex(def params,int index=1, boolean push =false){
        String propertyName
        String code
        String title
        if(params.archived){
            propertyName = "archivedPosProf"
            code="archivedPosProfCode "+index.toString()
            title="Архивный профиль должности автотест "+index.toString()
        }
        else{
            propertyName = "posProf"
            code="posProfCode "+index.toString()
            title="Профиль должности автотест "+index.toString()
        }
        String archived=params.archived.toString()
        String profileOPP=params.profileOPP.toString()
        String genderId=params.genderId.toString()
        String specId=mainScripts.utils().getTcPropertyValue(params.specParamName)
        String posProfValues=archived+",'"+title+"','"+code+"',"+specId+","+profileOPP+","+genderId
        if(push){
            mainScripts.sql().createEntitiesInDBForPush("tablePosProf", TableColumns.posProf, posProfValues, propertyName + "Id", index)
        }
    	else {
            mainScripts.sql().createEntitiesInDBwithIndex("tablePosProf", TableColumns.posProf, posProfValues, propertyName + "Id", index)
            mainScripts.utils().setTcPropertyValue(propertyName + "Code" + index.toString(), code)
            mainScripts.utils().setTcPropertyValue(propertyName + "Title" + index.toString(), title)
        }
    }


    def clearPosProfiles(int count=1, int startIndex=1, String propertyName="posProfId"){
        String tablePosProfTreatment = mainScripts.utils().getProjectPropertyValue("tablePosProfTreatment")
        String posProfIds = getPosProfIdStringList(count, startIndex, propertyName+"Id")
        String query="delete from "+tablePosProfTreatment+" where position_profile_id in ("+posProfIds+")"
        mainScripts.sql().executeQuery(query)
        for(int i=startIndex; i<=count; i++){
            mainScripts.sql().clearCreatedByTestInTestCaseWithIndex("tablePosProf", propertyName+"Id", i)
            mainScripts.utils().removeTcProperty(propertyName+"Title"+i)
            mainScripts.utils().removeTcProperty(propertyName+"Code"+i)
        }
    }

    def clearPosProfWithCount(int count=1, int archivedCount=0){
        if(count >0 ) clearPosProfiles(count, 1, "posProf")
        if(archivedCount >0 ) clearPosProfiles(archivedCount, 1, "archivedPosProf")
    }

    //собрать из записанных в проперти профилей структуру для сообщения о создании исполнения должности
    def collectPositionProfiles(def posProfMap){
        String posProfiles="<positionProfiles>"
        for(int i=0; i<posProfMap.size(); i++){
            for(int g=1; g<=posProfMap[i].count; g++){
                String propertyName=posProfMap[i].name
                String posProfId=mainScripts.utils().getTcPropertyValue(propertyName+"Id"+g)
                String posProfCode=mainScripts.utils().getTcPropertyValue(propertyName+"Code"+g)
                String posProfTitle=mainScripts.utils().getTcPropertyValue(propertyName+"Title"+g)
                posProfiles=posProfiles+"""<positionProfile id=\""""+posProfId+"""\"><code>"""+posProfCode+"""</code><title>"""+posProfTitle+"""</title></positionProfile>"""
            }
        }
        posProfiles=posProfiles+"</positionProfiles>"
        return posProfiles
    }

    def getNonExistedPositionProfiles(int counter=1){
        String table = mainScripts.utils().getProjectPropertyValue("tablePosProf")
        mainScripts.sql().getDoesntExisted(table, "nonExistedPosProfId", counter)
    }

    def createPosProfTreatments(List posProfIds, List  treatmentIds, Boolean archived=false, Boolean saveId=false ){
        String table =mainScripts.utils().getProjectPropertyValue("tablePosProfTreatment")
        Long firstId=mainScripts.sql().getNonExistedIdInDb(table)
        if(saveId){
            mainScripts.utils().setTcPropertyValue("posProfTreatId",firstId.toString())
        }
        String values =""
        for(int i=0; i<posProfIds.size(); i++){
            for(int k=0; k<treatmentIds.size();k++){
                String end =","
                if(i == (posProfIds.size()-1) &&  k == (treatmentIds.size()-1)) end=""
                String value="("+firstId.toString()+","+archived.toString()+","+posProfIds[i]+","+treatmentIds[k]+", null)"+end
                firstId=firstId+1
                values=values+value
            }
        }
        String query="insert into "+table+" values "+values
        mainScripts.sql().executeQuery(query)
    }

    def createPosProfTreatmentsForPosProfList(String treatments,int count, int startIndex=1, String propertyName="posProfId"){
        def posProfList=getPosProfIdList(count,startIndex,propertyName)
        for(int i=0; i< posProfList.size(); i++){
            createPosProfTreatments(treatments,posProfList[i].toString())
        }
    }

    //возвращает список идентификаторов posProf для использования в запросах
    //значения собирает из свойств ТК с названием propertyName+индекс
    def getPosProfIdStringList(int count, int startIndex=1, String propertyName="posProfId"){
        mainScripts.utils().getStringListFromProperties(count, startIndex, propertyName)
    }

    def getPosProfIdList(int count, int startIndex=1, String propertyName="posProfId"){
        mainScripts.utils().getListFromProperties(count, startIndex, propertyName)
    }

    def updateDefaultSAAT(String posProfPropertyName, String tPropertyName, Boolean saat){
        String posProfId=mainScripts.utils().getTcPropertyValue(posProfPropertyName)
        String tId=mainScripts.utils().getTcPropertyValue(tPropertyName)
        String table=mainScripts.utils().getProjectPropertyValue("tablePosProfTreatment")
        String query="update "+table+" set self_appointment_allowed_to_area_type = "+saat.toString()+
                " where position_profile_id="+posProfId+" and treatment_id="+tId
        mainScripts.sql().executeQuery(query)
    }

    def createRegularPosProfiles(int count=1, int countTreatments=0, Boolean findSpec=true){
        if(findSpec) getSpecForPosProf()
        def params=[
                specParamName:"specId1",
                archived:false,
                profileOPP:true,
                genderId:1
        ]
        createPosProfiles( params,count)
        if(countTreatments >0){
            List treatmentIds=t.getTreatmentsIdList(countTreatments)
            List posProfIds=getPosProfIdList(count)
            createPosProfTreatments(posProfIds, treatmentIds)
            mainScripts.sql().getDoesntExistedForPush("tablePosProfTreatment","posProfTreatId")
        }
    }

    def createRegularPosProfWithCounter( Boolean oneSpec=false){
        int counter=mainScripts.utils().returnIntegerTCPropValue("counterPosProf")
        if(!oneSpec) getSpecForPosProfWithCounter()
        createRegularPosProfiles(counter,0,oneSpec)
    }

    def getPosProIdForPush(){
        spec.findSpecializations()
        mainScripts.sql().getDoesntExistedForPush("tablePosProf","posProfId")
    }

    def createPosProForPush(def params){
        createPosProfiles(params ,1 ,1, true)
    }

    def getPosProfMap(Long id){
        def posProf=mainScripts.sql().returnEntityFromDBbyId("tablePosProf", id)
        if(posProf){
            def map=[
                    id : posProf.id,
                    archived: posProf.archived ,
                    title: posProf.title ,
                    code: posProf.code ,
                    specId: posProf.specialization_id ,
                    popp: posProf.profile_of_primary_position,
                    genderId : posProf.gender_id
            ]
            return map
        }
        else return
    }

    def getPosProTreatIdForPush(){
        t.findOrCreateTreatments(1)
        createRegularPosProfiles(1)
        mainScripts.sql().getDoesntExistedForPush("tablePosProfTreatment","posProfTreatId")
    }

    def createPosProfTreatForPush(boolean archived=false, boolean saa=true ){
        t.findOrCreateTreatments(2)
        getSpecForPosProf()
        def params=[
                specParamName:"specId1",
                archived:false,
                profileOPP:false,
                genderId:1
        ]
        createPosProfiles( params,2, 1 )
        String treatmentId=mainScripts.utils().getTcPropertyValue("treatmentId1")
        String posProfId=mainScripts.utils().getTcPropertyValue("posProfId1")
        mainScripts.sql().createEntitiesInDBForPush("tablePosProfTreatment", TableColumns.posProfTreatment, archived.toString()+","+posProfId+","+treatmentId+","+saa.toString() , "posProfTreatId", 1)
    }

    def getPosProfTreatMap(Long id){
        def posProfTreat=mainScripts.sql().returnEntityFromDBbyId("tablePosProfTreatment", id)
        if(posProfTreat){
            def map=[
                    id : posProfTreat.id,
                    archived: posProfTreat.archived ,
                    posProfId: posProfTreat.position_profile_id,
                    treatmentId : posProfTreat.treatment_id,
                    saat: posProfTreat.self_appointment_allowed_to_area_type
            ]
            return map
        }
        else return
    }

    def clearPosProfTreatForPush(int countTreatments=1){
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tablePosProfTreatment", "posProfTreatId", 1)
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tablePosProf", "posProfId", countTreatments)
        t.clearTreatments(countTreatments)
        spec.clearSpecializations()
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("posProfTreatId", "tablePosProfTreatment")
        mainScripts.sql().clearPropertiesByPropertyName("posProfId", "tablePosProf")
        t.clearAllByPropertyName()
        spec.clearAllByPropertyName()
    }
}