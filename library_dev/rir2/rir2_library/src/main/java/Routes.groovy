import groovy.sql.Sql

class Routes {
    def context
    def testRunner
    def log
    def mainScripts

    Routes(def context, def testRunner, def log) {
        this.context=context
        this.testRunner=testRunner
        this.log=log
        this.mainScripts = ScBuilder.init(context, testRunner, log)
    }

    def getNonExistedRoutes(int counter=1, String propertyName="nonExistedRouteId"){
        String table = testRunner.testCase.testSuite.project.getPropertyValue("tableRoute")
        mainScripts.getDoesntExisted(table, propertyName, counter)
    }

    def createRoute(String propertyName, String values ){
        mainScripts.createEntitiesInDBwithCount("tableRoute",TableColumns.route,values,propertyName)
    }

    def getRouteById(Long id){
        String table =testRunner.testCase.testSuite.project.getPropertyValue("tableRoute");
        String getEntity="select * from "+table+" where ID="+id
        def entity = sql.firstRow(getEntity)
        return entity ? buildRouteMap(entity) : null

    }

    def buildRouteMap(def route){
        def formattedCreated=mainScripts.formatDBdate(route.created.toString())
        def formattedUpdated=mainScripts.formatDBdate(route.updated.toString())
        def map=[
                id : route.id,
                active:route.active,
                needReferral : route.need_referral,
                number : route.number,
                document : route.document,
                created : formattedCreated,
                updated : formattedUpdated,
                receivingTreatmentCategory : route.receiving_treatment_category,
                receivingMedicalOrganization : route.receiving_medical_organization
        ]
        return map
    }

    def createBunchForRoute(String routePropertyName="routeId", String otherPropertyName,  int startIndex=1, int lastIndex=1, String table, String columns){
        Long routeId =testRunner.testCase.getPropertyValue(routePropertyName).toLong()
        mainScripts.createEntitiesInDBWithValuesInProperty(table, columns, otherPropertyName ,otherPropertyName, otherPropertyName, startIndex, lastIndex,routeId)
    }

    def compareRouteParameters(String tablePropertyName,String routePropertyName="routeId",String otherPropertyName,String otherColumnName, int startIndex=1, int lastIndex=1, String reqDataType="long"){
        Long routeId =testRunner.testCase.getPropertyValue(routePropertyName).toLong()
        List requestList= mainScripts.getListFromProperties(lastIndex, startIndex, otherPropertyName, reqDataType)
        List dbList= mainScripts.getListParametersValueFromTAble(tablePropertyName, routeId, "route_id", otherColumnName)
        mainScripts.compareLists(requestList,dbList)
    }

    def createRouteTreatment(String routePropertyName="routeId", String otherPropertyName="tId",  int startIndex=1, int lastIndex=1){
        createBunchForRoute(routePropertyName,otherPropertyName, startIndex, lastIndex,"tableRouteT",  TableColumns.routeTreatment)
    }

    def compareRouteTreatment(String routePropertyName="routeId", String otherPropertyName="tId",  int startIndex=1, int lastIndex=1){
        compareRouteParameters("tableRouteT",routePropertyName,otherPropertyName,"receiving_treatment_id",startIndex,lastIndex)
    }

    def createRouteDiagnosis(String routePropertyName="routeId", String otherPropertyName="diagnosId",  int startIndex=1, int lastIndex=1){
        createBunchForRoute(routePropertyName,otherPropertyName, startIndex, lastIndex,"tableRouteDiag",  TableColumns.routeDiagnosis)
    }

    def compareRouteDiagnosis(String routePropertyName="routeId", String otherPropertyName="diagnosId",  int startIndex=1, int lastIndex=1){
        compareRouteParameters("tableRouteDiag",routePropertyName,otherPropertyName,"diagnosis_id",startIndex,lastIndex)
    }

    def createRoutePosProf(String routePropertyName="routeId", String otherPropertyName="posProfId",  int startIndex=1, int lastIndex=1){
        createBunchForRoute(routePropertyName,otherPropertyName, startIndex, lastIndex,"tableRoutePosProf",  TableColumns.routePosProf)
    }

    def comparePosProf(String routePropertyName="routeId", String otherPropertyName="posProfId",  int startIndex=1, int lastIndex=1){
        compareRouteParameters("tableRoutePosProf",routePropertyName,otherPropertyName,"referral_position_profile_id",startIndex,lastIndex)
    }

    def createRouteRecMF(String routePropertyName="routeId", String otherPropertyName="mfId",  int startIndex=1, int lastIndex=1){
        createBunchForRoute(routePropertyName,otherPropertyName, startIndex, lastIndex,"tableRouteRecMF",  TableColumns.routeRecMF)
    }

    def compareRouteRecMF(String routePropertyName="routeId", String otherPropertyName="mfId",  int startIndex=1, int lastIndex=1){
        compareRouteParameters("tableRouteRecMF",routePropertyName,otherPropertyName,"receiving_medical_facility_id",startIndex,lastIndex)
    }

    def createRouteRefMO(String routePropertyName="routeId", String otherPropertyName="moId",  int startIndex=1, int lastIndex=1){
        createBunchForRoute(routePropertyName,otherPropertyName, startIndex, lastIndex,"tableRouteRefMO",  TableColumns.routeRefMO)
    }

    def compareRouteRefMO(String routePropertyName="routeId", String otherPropertyName="moId",  int startIndex=1, int lastIndex=1){
        compareRouteParameters("tableRouteRefMO",routePropertyName,otherPropertyName,"referral_medical_organization_id",startIndex,lastIndex)
    }

    def clearRoute(String propertyName){
        String tableRoute = testRunner.testCase.testSuite.project.getPropertyValue("tableRoute")
        String tableRouteDiag = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteDiag")
        String tableRouteT = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteT")
        String tableRoutePosProf = testRunner.testCase.testSuite.project.getPropertyValue("tableRoutePosProf")
        String tableRouteRefMO = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteRefMO")
        String tableRouteRecMF = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteRecMF")
        String id = testRunner.testCase.getPropertyValue(propertyName)
        String query="delete from "+tableRouteDiag+" where route_id ="+id+"; "+
                "delete from "+tableRouteT+" where route_id ="+id+"; "+
                "delete from "+tableRouteRecMF+" where route_id ="+id+"; "+
                "delete from "+tableRoutePosProf+" where route_id ="+id+"; "+
                "delete from "+tableRouteRefMO+" where route_id ="+id+"; "+
                "delete from "+tableRoute+" where id ="+id
        sql.execute(query)
    }

}
