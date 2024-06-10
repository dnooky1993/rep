class JobExecution {

    def mainScripts
    MedicalFacility mf
    PosProf posProf
    PosNom posNom
    DepNom dn

    JobExecution(def context, def testRunner, def log) {
        this.mainScripts = ScBuilder.init(context, testRunner, log)
        this.mf = new MedicalFacility(context, testRunner, log)
        this.posProf = new PosProf(context, testRunner, log)
        this.posNom = new PosNom(context, testRunner, log)
        this.dn = new DepNom(context, testRunner, log)
    }

    def findEverythingForRegularJobExecution(int counter=1){
        posNom.findAllPosNomsForJE()
        posProf.createRegularPosProfWithCounter()
        mf.createDefaultMfAndMoWithPropertyCounter()
        dn.findDepNom(counter)
    }

    def getNonExistedJobExecution(int counter=1, String propertyName="jeId"){
        findEverythingForRegularJobExecution(counter)
        String table = mainScripts.utils().getProjectPropertyValue("tableJE")
        mainScripts.sql().getDoesntExisted(table, propertyName, counter)
    }

    def getJobExecutionFromDB(long id){
        def entity=mainScripts.sql().returnEntityFromDBbyId("tableJE", id)
        return entity
    }
    def getJEPosProfsFromDB(long id){
        String tableName=mainScripts.utils().getProjectPropertyValue("tableJEPP")
        String query = ("SELECT position_profile_id as id FROM "+tableName+" WHERE job_execution_id="+id+" order by position_profile_id")
        def result= mainScripts.sql().returnQueryResult(query)
        return result.id
    }

    def collectJEPosProf(int counter=1, int startIndex=1, String propertyName="posProfId"){
        List list =[]
        for(int i=startIndex; i <=counter; i++){
            Long posProfId=mainScripts.utils().returnLongTCPropValue(propertyName+i)
            list << posProfId
        }
        return list
    }

    def getJobExecutionMapFromDB(long id){
        def je=getJobExecutionFromDB(id)
        if (je){
            def map = [
                    id: je.id,
                    archived: je.archived,
                    moId: je.medical_organization_id,
                    employeeId: je.employee_id,
                    snils: je.employee_snils,
                    lastName: je.employee_last_name,
                    firstName: je.employee_first_name,
                    middleName: je.employee_middle_name,
                    volume: je.job_execution_volume,
                    start: je.start,
                    end: je.finish,
                    posNomId: je.position_nom_id,
                    title: je.position_title,
                    departmentId: je.department_id,
                    departmentTitle: je.department_title,
                    departmentNomId: je.department_nom_id,
                    specId: je.specialization_id,
                    maternityLeave: je.maternity_leave,
                    defaultMfId: je.default_medical_facility_id,
                    isResource: je.is_resource
            ]
            return map
        }
        else mainScripts.utils().loger("Не найдена запись job_execution")
    }

    def clearJobExecution(String propertyName="jeId"){
        String id  = mainScripts.utils().getTcPropertyValue(propertyName+"1")
        String table = mainScripts.utils().getProjectPropertyValue("tableJEPP")
        mainScripts.sql.execute("delete from "+table+" where job_execution_id="+id)
        mainScripts.clearCreatedByTestInTestCaseWithIndex("tableJE", propertyName, 1)
    }

    def checkExistenceJobExecution(long id, boolean mustBeCreated=false){
        Boolean exist=mainScripts.sql().checkExistenceInDb("tableJE", id )
        if(exist !=mustBeCreated ){
            String should = mustBeCreated ? "не" : ""
            mainScripts.utils().fail("В таблице job_execution "+should+" найдена запись с id="+id)
        }
    }

    def createJobExecution(String values, String propertyName="jobExecutionId",int index=1){
        mainScripts.createEntitiesInDBwithIndex("tableJE", TableColumns.jobExecution, values, propertyName, index)
    }

    def createJobExecutionPosProf(String jePropertyName="jobExecutionId",int count=1, String posProfPropertyName="posProfId"){
        ArrayList posProfIdList=posProf.getPosProfIdList(count,1,posProfPropertyName)
        if (!posProfIdList)  System.exit(0)
        String jobExecutionId  = mainScripts.utils().getTcPropertyValue(jePropertyName)
        String table = mainScripts.utils().getProjectPropertyValue("tableJEPP")
        String query=" INSERT INTO "+table+"\n " +
                "("+TableColumns.jobExecutionPosProf+")\n" +
                "values ("+jobExecutionId+", "+posProfIdList[0]+")"
        for(int i=1; i<posProfIdList.size(); i++){
            query=query+",("+jobExecutionId+", "+posProfIdList[i]+")"
        }
        mainScripts.sql().executeQuery(query)
    }

    def deleteJobExecutionPosProf(){
        String tableName=mainScripts.utils().getProjectPropertyValue("tableJEPP")
        List propertyNames=mainScripts.utils().collectPropertyNames("jeId", true)
        List values=[]
        propertyNames.each{
            String value=mainScripts.utils().getTcPropertyValue(it)
            values << value
        }
        String valuesString=mainScripts.utils().listToString(values)
        mainScripts.sql().executeQuery("delete from "+tableName+" where job_execution_id in ("+valuesString+")")
    }

    def clearAllByPropertyName() {
        deleteJobExecutionPosProf()
        mainScripts.sql().clearPropertiesByPropertyName("jeId", "tableJE")
        posProf.clearAllByPropertyName()
        posNom.clearAllByPropertyName()
        mf.clearAllByPropertyName()
        dn.clearAllByPropertyName()
    }
}
