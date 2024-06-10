import groovy.sql.Sql

class Equipment {

    def mainScripts
    Treatments t
    ResourceTypes resTypes
//    MedicalFacility mf
    Location loc
    EquipmentProfile ep
    DepNom dn

    Equipment(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
        this.t = new Treatments(context, testRunner, log)
        this.resTypes = new ResourceTypes(context, testRunner, log)
//        this.mf = new MedicalFacility(context, testRunner, log)
        this.loc = new Location(context, testRunner, log)
        this.ep = new EquipmentProfile(context, testRunner, log)
        this.dn = new DepNom(context, testRunner, log)
    }

    def getNonexistentEquipment(int counter=1, String propertyName="equipmentId"){
        String table = mainScripts.utils().getProjectPropertyValue("tableE")
        mainScripts.sql().getDoesntExisted(table, propertyName, counter)
    }

    def getEquipmentIdForEsu(int counter=1, String propertyName="equipmentId"){
        getNonexistentEquipment()
        t.findTreatmentsWithTreatmentCount()
        ep.findEquipmentProfileWithRT()
        loc.createLocationForEsu()
        dn.findDepNom()
    }

    def findEverythingForEquipment(int counter=1){
        t.findTreatmentsWithTreatmentCount()
        ep.findEquipmentProfileWithRT(counter)
        loc.createLocations(false, counter)
        dn.findDepNom(counter)
    }

    def createEquipmentForEsu(def params){
        findEverythingForEquipment(2)
        createEquipment(params)
    }

    def createEquipment(def params, String propertyName="equipmentId",int index=1){
        String values = "123, "+params.personalizedService.toString()+","+params.capacity.toString()+","+params.equipmentProfileId.toString()+","+
                params.moId.toString()+","+params.mfId.toString()+",123456, 'Тестовое подразделение',"+params.depNomId.toString()+","+
                params.locId.toString()+","+params.archived.toString()+","+params.equipmentId.toString()+",'"+params.model.toString()+"'"
        mainScripts.sql().createEntitiesInDBwithIndex("tableE", TableColumns.equipment, values, propertyName, index)
    }

    def createEquipmentTreatments(String equipmentPropertyName="equipmentId",int count=1, String tPropertyName="treatmentId"){
        ArrayList treatmentsId=t.getTreatmentsIdList(count, 1,tPropertyName)
        if (!treatmentsId)  System.exit(0)
        String equipmentId  = mainScripts.utils().getTcPropertyValue(equipmentPropertyName)
        String tableET = mainScripts.utils().getProjectPropertyValue("tableET")
        long id=mainScripts.sql().getNonExistedIdInDb(tableET)
        String query=" INSERT INTO "+tableET+"\n" +
                "(id, equipment_id, treatment_id, use_by_default)\n" +
                "values ("+id+", "+equipmentId+", "+treatmentsId[0]+", true)"
        for(int i=1; i<treatmentsId.size(); i++){
            id+=1
            query=query+",("+id+", "+equipmentId+", "+treatmentsId[i]+", false)"
        }
        mainScripts.sql().executeQuery(query)
    }

    def deleteET(String propertyName="equipmentId1"){
        String id  = mainScripts.utils().getTcPropertyValue(propertyName)
        String tableET = mainScripts.utils().getProjectPropertyValue("tableET")
        mainScripts.sql().executeQuery("delete from "+tableET+" where equipment_id="+id)
    }

    def clearEquipmentTreatments(String propertyName="equipmentId1"){
        deleteET(propertyName)
        t.clearTreatmentsWithCount()
    }

    def clearEquipment(List resType, String propertyName="equipmentId1", String epPropertyName="equipmentProfileId", int countEP=1 ){
        String id  = mainScripts.utils().getTcPropertyValue(propertyName)
        mainScripts.clearCreatedByTestInTestCaseWithIndex("tableE", propertyName, 1)
        ep.clearEquipmentProfile(countEP, resType, epPropertyName)
    }

    def findTreatments(int count =1,int startIndex=1, String tcPropertyName="tcId", String tPropertyName="tId", String tcCodePropertyName="tcCode1"){
        t.findOrCreateTreatments(count,tcPropertyName,tPropertyName,tcCodePropertyName)
        t.getTreatmentsIdList(count, startIndex,tPropertyName)
    }

    def collectTreatmentsForMessage(def list){
        String treatments=""
        String useByDefault="true"
        for(int i=0; i<list.size(); i++){
            String tId = list[i]
            treatments=treatments+"<ns4:treatment><ns4:id>"+tId+"</ns4:id><ns4:useByDefault>"+useByDefault+"</ns4:useByDefault></ns4:treatment>"
            useByDefault="false"
        }
        return treatments
    }

    def findTreatmentsAndCollectForMessage(String tcPropertyName="tcId", String tPropertyName="tId", String tcCodePropertyName="tcCode1"){
        ArrayList treatmentsId=t.findTreatmentsWithTreatmentCount(tcPropertyName,tPropertyName,tcCodePropertyName)
        collectTreatmentsForMessage(treatmentsId)
    }

    def collectTreatmentsFromList(String treatments){
        def listOfTreatments=mainScripts.stringToIntegerList(treatments)
        collectTreatmentsForMessage(listOfTreatments)
    }

    def collectTreatmentsFromListInProperty(String propertyName="treatments") {
        String treatments = mainScripts.utils().getTcPropertyValue("treatments")
        collectTreatmentsFromList(treatments)
    }

    def collectTreatments(int count, int startIndex=1){
        List trIds=t.getTreatmentsIdList(count,startIndex)
        collectTreatmentsForMessage(trIds)
    }

    def getEquipmentMapFromDB(long id){
        def e=mainScripts.sql().returnEntityFromDBbyId("tableE", id )
        if (!e){
            mainScripts.utils().fail("Запись с id ="+id+" в таблице equipment не найдена")
        }
        else {
            def map = [
                    id                 : e.id,
                    equipmentTypeId    : e.equipment_type_id,
                    personalizedService: e.personalized_service,
                    capacity           : e.capacity_for_room,
                    equipmentProfileId : e.equipment_profile_id,
                    moId               : e.medical_organization_id,
                    mfId               : e.medical_facility_id,
                    depId              : e.department_id,
                    depTitle           : e.department_title,
                    depNomId           : e.department_nom_id,
                    locId              : e.location_id,
                    archived           : e.archived,
                    equipmentId        : e.equipment_id,
                    model              : e.equipment_model
            ]
            return map
        }
    }

    def getEquipmentTreatments(long id){
        def eTreatments=mainScripts.sql().returnRows("tableET"," where equipment_id="+id)
        if (!eTreatments){
            mainScripts.utils().fail("Запись с id ="+id+" в таблице equipment не найдена")
        }
        else {
            def treatments = []
            eTreatments.each { it ->
                def map = [id: it.treatment_id, useByDefault: it.use_by_default]
                treatments << map
            }
            return treatments
        }
    }

    def clearAllByPropertyName(){
        List eIds=mainScripts.utils().collectPropertyNames("equipmentId")
        eIds.each{
            deleteET(it)
        }
        mainScripts.sql().clearPropertiesByPropertyName("equipmentId", "tableE")
        ep.clearAllByPropertyName()
        loc.clearAllByPropertyName()
        t.clearAllByPropertyName()
        dn.clearAllByPropertyName()
    }
}
