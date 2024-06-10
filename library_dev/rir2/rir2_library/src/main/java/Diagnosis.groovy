class Diagnosis {

    def mainScripts

    Diagnosis(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
    }
    //найти диагнозы в указанном количестве для нужной архивности
    def findDiagnosis(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableDiagnosis", TableColumns.diagnosis, " false, 'Диагноз автотест', 'code autotest'", "diagnosisId", count, "where archived = false");
        }
        if(countArchived >0) {
            mainScripts.sql().findOrCreateEntitiesInDBwithConditionAndCount("tableDiagnosis", TableColumns.diagnosis, " true, 'Архивный диагноз автотест', 'archived code autotest'", "archivedDiagnosisId", countArchived, "where archived = true");
        }
    }
    //очистить  диагнозы
    def clearDiagnosis(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableDiagnosis", "diagnosisId", count)
        }
        if(countArchived >0) {
            mainScripts.sql().clearCreatedByTestInTestCaseWithCount("tableDiagnosis", "archivedDiagnosisId", countArchived)
        }
    }
    // найти несуществующий идентификатор
    def getNonExistedDiagnosis(int counter=1){
        String table = mainScripts.utils().getProjectPropertyValue("tableDiagnosis")
        mainScripts.sql().getDoesntExisted(table, "diagnosisId", counter)
    }
    // найти несуществующий идентификатор для пушей
    def getDiagnosisIdForPush(){
        mainScripts.sql().getDoesntExistedForPush("tableDiagnosis","diagnosisId")
    }
    // метод создания специализации для пуша
    def createDiagnosisForPush(int count=1, int countArchived=0){
        if(count >0){
            mainScripts.sql().createEntitiesInDBForPush("tableDiagnosis", TableColumns.diagnosis, " false, 'Диагноз автотест', 'code autotest'", "diagnosisId", count)
        }
        if(countArchived >0) {
            mainScripts.sql().createEntitiesInDBForPush("tableDiagnosis", TableColumns.diagnosis, " true, 'Архивный диагноз автотест', 'archived code autotest'", "archivedDiagnosisId", countArchived)
        }
    }
    //получить диагноз из БД
    def getDiagnosisFromDB(Long id){
        def diagnosis=mainScripts.sql().returnEntityFromDBbyId("tableDiagnosis", id)
        return diagnosis
    }
    //получить мапу диагноза  из БД
    def getDiagnosisMap(Long id){
        def diagnosis=getDiagnosisFromDB(id)
        if(diagnosis){
            def map=[
                    id : diagnosis.id,
                    archived: diagnosis.archived ,
                    title: diagnosis.title,
                    code: diagnosis.code
            ]
            return map
        }
        else return
    }

    def clearAllByPropertyName(){
        mainScripts.sql().clearPropertiesByPropertyName("diagnosisId", "tableDiagnosis")
    }
}
