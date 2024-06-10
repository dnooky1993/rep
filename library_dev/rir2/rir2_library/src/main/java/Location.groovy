class Location {
    def mainScripts
    MedicalFacility mf
    Specializations spec

    Location(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
        this.mf = new MedicalFacility( context, testRunner, log)
        this.spec = new Specializations( context, testRunner, log)
    }

    def getNonexistentLocationId(){
        String tableName=mainScripts.utils().getProjectPropertyValue("tableLoc")
        mainScripts.sql().getDoesntExisted(tableName,"locId")
    }

    def getNonexistentLocationIdForEsu(){
        mf.createDefaultMfAndMoWithPropertyCounter()
        spec.findSpecializations()
        getNonexistentLocationId()
    }

    def createLocationForEsu(Boolean archived =false, Boolean needSpec=false){
        mf.createDefaultMfAndMoWithPropertyCounter()
        int counterSpec= needSpec ? 2 : 1
        spec.findSpecializations(counterSpec)
        Long specId= needSpec ?  mainScripts.utils().returnLongTCPropValue("specId1") : null
        Long mfId= mainScripts.utils().returnLongTCPropValue("mfId1")
        createLocationWithIndex(mfId,archived,specId)
    }

    def createLocations(Boolean archived =false,int counter=1){
        mf.createDefaultMfAndMoWithPropertyCounter()
        spec.findSpecializations(2)
        Long specId= mainScripts.utils().returnLongTCPropValue("specId1")
        Long mfId= mainScripts.utils().returnLongTCPropValue("mfId1")
        for(int i=1; i<=counter; i++) {
            createLocationWithIndex(mfId, archived, specId, i)
        }
    }
    def createLocationWithIndex(Long mfId, Boolean archived= false, Long specId =null, int index=1, String paramName=""){
        mainScripts.sql().createEntitiesInDBwithIndex("tableLoc", TableColumns.loc, archived.toString()+", 'Локация автотест', null, "+mfId.toString()+", "+specId, paramName+"locId" , index)
    }

    def getLocationFromDB(Long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tableLoc", id)
        return entity
    }

    def getLocationMap(Long id){
        def entity=getLocationFromDB(id)
        if(entity){
            def map=[
                    id : entity.id,
                    archived: entity.archived ,
                    roomTitle: entity.room_title ,
                    stage: entity.stage ,
                    mfId: entity.medical_facility_id ,
                    specId: entity.specialization_id
            ]
            return map
        }
        else return
    }

    def clearLocation(int counter=1, int countArchived=0, int countSpec=0 ){
        if(counter >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableLoc", "locId", counter)
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableLoc", "archivedLocId", countArchived)
        }
        if(countSpec >0) spec.clearSpecializations(countSpec)
    }

    def clearLocationAndDefaultMf(int counter=1, int countArchived=0, int countSpec=0 ){
        clearLocation(counter, countArchived, countSpec)
        mf.clearDefaultMfAndMoWithPropertyCounter()
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("locId", "tableLoc")
        mf.clearAllByPropertyName()
        spec.clearAllByPropertyName()
    }

    def changeLocationSpecToArchived(String propertyName="locId1"){
        spec.findSpecializations(0,1)
        String specId= mainScripts.utils().getTcPropertyValue("archivedSpecId1")
        String locId= mainScripts.utils().getTcPropertyValue(propertyName)
        String tableName=mainScripts.utils().getProjectPropertyValue("tableLoc")
        String query =" update "+tableName+" set specialization_id="+specId+" where id="+locId
        mainScripts.sql().executeQuery(query)
    }

    def clearLocationSpec(String propertyName="locId1"){
        String locId= mainScripts.utils().getTcPropertyValue(propertyName)
        String tableName=mainScripts.utils().getProjectPropertyValue("tableLoc")
        String query =" update "+tableName+" set specialization_id= null where id="+locId
        mainScripts.sql().executeQuery(query)
    }
}
