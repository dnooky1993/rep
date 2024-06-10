class StructureOfMedOrganization {
    def mainScripts

    StructureOfMedOrganization(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
    }
    def countMoId() {
        String functionName = mainScripts.sql().getCheckNullFunctionName()
        String tableMO = mainScripts.utils().getProjectPropertyValue("tableMO");
        String tableMF = mainScripts.utils().getProjectPropertyValue("tableMF");
        long minId;
        long minIdMO = mainScripts.sql().returnQueryResult("SELECT " + functionName + "(min(ID)-1,-1) AS ID FROM " + tableMO)[0].ID;
        long minIdMF =mainScripts.sql().returnQueryResult("SELECT " + functionName + "(min(ID)-1,-1) AS ID FROM " + tableMF)[0].ID;
        minId = minIdMO > minIdMF ? minIdMF : minIdMO
        return minId
    }
    //Создает основную структуру Медицинской огранизации: МО, переданное количество Адресных объектов, Мед учреждений и локаций. Записывает всё в проперти
    def createStructureOfMO(int counterMF,String propertyName="") {
        long minId = countMoId()
        mainScripts.sql().createEntitiesInDBwithIndex("tableMO", TableColumns.mo, "false,'МО автотест РиР.2','МО автотест РиР.2',1111,NULL,TIMESTAMP '1970-01-01 00:00:00.000000'", propertyName+"moId", 1, minId);
        String isHead = '1';
        String headDescription = "Головное ";
        mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableSpec", TableColumns.spec, "-2,false,'Специализация автотест РиР.2'", propertyName+"specId", 1, "where archived = false");
        String specId = mainScripts.utils().getTcPropertyValue(propertyName+"specId1");
        long mfId = minId
        for (int i = 1; i <= counterMF; i++) {
            if (i > 1) {
                isHead = '0';
                headDescription = ""
            }
            mainScripts.sql().createEntitiesInDBwithIndex("tableAO", TableColumns.ao, "false, 6203000, 'Адресный объект автоест РиР.2', CURRENT_TIMESTAMP", propertyName+"aoId", i, null);
            String aoId = mainScripts.utils().getTcPropertyValue(propertyName+"aoId" + i);
            mainScripts.sql().createEntitiesInDBwithIndex("tableMF", TableColumns.mf, "false," + minId + ", '" + headDescription + "МУ автотест РиР.2','" + headDescription + "МУ автотест РиР.2','" + headDescription + "МУ автотест РиР.2','" + isHead + "'," + aoId + ", NULL,CURRENT_TIMESTAMP ", propertyName+"mfId", i, mfId);
            mainScripts.sql().createEntitiesInDBwithIndex("tableLoc", TableColumns.loc, "false,'Локация автотест РиР.2',1," + mfId + "," + specId, propertyName+"locId", i);
            mfId = mfId - 1
        }
    };

    //Очищает созданные методом createStructureOfMO записи в бд и проперти
    def clearStructureOfMO(int counter=1,String propertyName="") {
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableLoc", propertyName+"locId", counter)
        for (int i = 1; i <= counter; i++) {
            String tableMFWH = mainScripts.utils().getProjectPropertyValue("tableMFWH");
            String mfId = mainScripts.utils().getTcPropertyValue(propertyName+"mfId" + i);
            String deleteMFWH = ("delete from " + tableMFWH + " where MEDICAL_FACILITY_ID=" + mfId);
            mainScripts.sql().executeQuery(deleteMFWH)
        };

        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableMF", propertyName+"mfId", counter)
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableAO", propertyName+"aoId", counter)
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableMO", propertyName+"moId", 1)
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableSpec", propertyName+"specId", 1)
    }

    //создает переданное количество неархивных МО в БД
    def createMo(int counter=1, String propertyName="moId") {
        long minId = countMoId()
        for (int i = 1; i <= counter; i++) {
            mainScripts.sql().createEntitiesInDBwithIndex("tableMO", TableColumns.mo, "false,'МО автотест РиР.2','МО автотест РиР.2',1111,NULL,TIMESTAMP '1970-01-01 00:00:00.000000'", propertyName, i, minId);
            minId = minId -1
        }
    }

    //создает переданное количество неархивных МО в БД
    def clearMO(int counter=1, String propertyName="moId") {
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableMO", propertyName, counter)
    }

    def createArchivedMO(String propertyName="archivedMoId"){
        long minId = countMoId()
        mainScripts.sql().createEntitiesInDBwithIndex("tableMO", TableColumns.mo, "true,'Архивное МО автотест РиР.2','Архивное МО автотест РиР.2',1111,NULL,TIMESTAMP '1970-01-01 00:00:00.000000'", propertyName, 1, minId);
    }

    def clearArchivedMO(String propertyName="archivedMoId") {
        mainScripts.sql().clearCreatedByTestInTestCaseWithIndex("tableMO", propertyName, 1)
    }

    def createArchivedMF(String propertyName="moId1"){
        String moId = mainScripts.utils().getTcPropertyValue(propertyName)
        mainScripts.sql().createEntitiesInDBwithIndex("tableAO", TableColumns.ao, "false, 6203000, 'Адресный объект автоест РиР.2', CURRENT_TIMESTAMP", "aoForArchivedMFId", 1);
        String aoId = mainScripts.utils().getTcPropertyValue("aoForArchivedMFId")
        mainScripts.sql().createEntitiesInDBwithIndex("tableMF", TableColumns.mf, "true," + moId + ", 'Архивное МУ автотест РиР.2','Архивное МУ автотест РиР.2','Архивное МУ автотест РиР.2',false," + aoId + ", NULL,CURRENT_TIMESTAMP ", "archivedMfId", 1);
    }

    def clearArchivedMF(){
        mainScripts.sql().clearCreatedByTestInTestCaseWithIndex("tableMF", "archivedMfId", 1)
        mainScripts.sql().clearCreatedByTestInTestCaseWithIndex("tableAO", "aoForArchivedMFId", 1)
    }

    def getNonExistedMO(int counter=1){
        String table = mainScripts.utils().getProjectPropertyValue("tableMO")
        mainScripts.sql().getDoesntExisted(table, "nonExistedMoId", counter)
    }

    def getNonExistedMF(int counter=1){
        String table = mainScripts.utils().getProjectPropertyValue("tableMF")
        mainScripts.sql().getDoesntExisted(table, "nonExistedMfId", counter)
    }

    def getNonExistedAO(int counter=1){
        String table = mainScripts.utils().getProjectPropertyValue("tableAO")
        mainScripts.sql().getDoesntExisted(table, "nonExistedAoId", counter)
    }

    def getNonExistedLocation(int counter=1){
        String table = mainScripts.utils().getProjectPropertyValue("tableLoc")
        mainScripts.sql().getDoesntExisted(table, "nonExistedLocId", counter)
    }

    def clearAO(){
        mainScripts.sql().clearCreatedByTestInTestCaseWithIndex("tableAO", "aoId", 1)
    }

    def clearLocation(int counter=1, String propertyName="locId1"){
        mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableLoc", propertyName, counter)
    }

    def createEverythingForMF(){
        long minId = countMoId()
        mainScripts.sql().createEntitiesInDBwithIndex("tableMO", TableColumns.mo, "false,'МО автотест РиР.2','МО автотест РиР.2',1111,NULL,TIMESTAMP '1970-01-01 00:00:00.000000'", "moId", 1, minId);
        mainScripts.sql().createEntitiesInDBwithIndex("tableAO", TableColumns.ao, "false, 6203000, 'Адресный объект автоест РиР.2', CURRENT_TIMESTAMP", "aoId", 1, null)
    }

    def clearEverythingForMF(String propertyNameMF='mfId', String propertyNameAO='aoId'){
        mainScripts.sql().clearCreatedByTestInTestCaseWithIndex("tableMF", propertyNameMF, 1)
        mainScripts.sql().clearCreatedByTestInTestCaseWithIndex("tableAO", propertyNameAO, 1)
        mainScripts.sql().clearCreatedByTestInTestCaseWithIndex("tableMO", "moId", 1)
    }

    def findDepartmentNoms(int counter=1){
        String depNomValues="'Отделение автотест РиР2', TIMESTAMP '2015-11-01 00:00:00.000000', NULL"
        mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableDepNom",TableColumns.depNom,depNomValues,"depNomId",counter, "where \"end\" IS NULL ")
    }

    def findArchivedDepartmentNoms(int counter=1){
        String archivedDepNomValues="'Архивное отделение автотест РиР2', TIMESTAMP '2015-11-01 00:00:00.000000', '2015-11-01 00:00:00.000000'"
        mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableDepNom",TableColumns.depNom,archivedDepNomValues,"archivedDepNomId",counter, "where \"end\" < current_date")
    }

    def clearDepartmentNoms(int counter=1){
        mainScripts.sql().clearCreatedByTestInTestCaseWithIndex("tableDepNom", "depNomId", counter)
    }

    def clearArchivedDepartmentNoms(int counter=1){
        mainScripts.sql().clearCreatedByTestInTestCaseWithIndex("tableDepNom", "archivedDepNomId", counter)
    }

    def findMOforMF(String mfIdPropertyName="mfId"){
        String mfId=mainScripts.utils().getTcPropertyValue(mfIdPropertyName)
        String table=mainScripts.utils().getProjectPropertyValue("tableMF")
        def moId=mainScripts.sql().returnQueryFirstRow("select medical_organization_id as id from "+table+" where id="+mfId)
        mainScripts.utils().setTcPropertyValue("moId", moId.id.toString())
    }

    def findLocationForMF(String mfIdPropertyName="mfId"){
        String mfId=mainScripts.utils().getTcPropertyValue(mfIdPropertyName)
        mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableLoc", TableColumns.loc, "false, '99', 2, "+mfId+", NULL", "locId", 1, "where archived = false and medical_facility_id="+mfId)
    }

}
