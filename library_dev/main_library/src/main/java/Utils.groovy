import java.nio.file.Paths

class Utils {

    def static context
    def static testRunner
    def static log

    Utils(def context, def testRunner, def log) {
        this.context=context
        this.testRunner=testRunner
        this.log=log
        //this.sql=new SqlLib(context, testRunner, log)
//        log.info ("имя кейса в конструкторе Utils: "+testRunner.testCase.getName())

    }

    static loger(def text){
        log.info(text)
    }
    static setTcPropertyValue(String propertyName, String propertyValue){
//        loger("имя кейса в setPropertyValue: "+testRunner.testCase.getName())
        testRunner.testCase.setPropertyValue(propertyName, propertyValue)
    }

    static getTcPropertyValue(String propertyName){
        String value=testRunner.testCase.getPropertyValue(propertyName)
        return value
    }

    static removeTcProperty(String propertyName){
        testRunner.testCase.removeProperty(propertyName)
    }

    static setTsPropertyValue(String propertyName, String propertyValue){
        testRunner.testCase.testSuite.setPropertyValue(propertyName, propertyValue)
    }

    static getTsPropertyValue(String propertyName){
        String value=testRunner.testCase.testSuite.getPropertyValue(propertyName)
        return value
    }

    static setProjectPropertyValue(String propertyName, String propertyValue){
        testRunner.testCase.testSuite.project.setPropertyValue(propertyName, propertyValue)
    }

    static getProjectPropertyValue(String propertyName){
        String value=testRunner.testCase.testSuite.project.getPropertyValue(propertyName)
        return value
    }

    def returnIntegerTCPropValue(String propertyName){
        def value = getTcPropertyValue(propertyName) ? getTcPropertyValue(propertyName).toInteger() : null
        return value
    }

    def returnLongTCPropValue(String propertyName){
        def value = getTcPropertyValue(propertyName) ? getTcPropertyValue(propertyName).toLong() : null
        return value
    }

    static fail(String failReason=""){
        testRunner.fail(failReason)
    }
    //Создание в тест кейсе проперти с соответствующими названием и номером
    // + проперти с признаком создания в рамках тест кейса. это значение должно быть "0" или "1"
    static createPropertyWithCreatedByTCparameter(String propertyName, String propertyValue, String createdByTestCaseValue, int index=1){
        setTcPropertyValue(propertyName+index, propertyValue);
        setTcPropertyValue(propertyName+"CreatedByTest"+index, createdByTestCaseValue)
    };

    static createPropertyWithCreatedByTCparameterAndCount(String propertyName, String propertyValue, String createdByTestCaseValue, int count=1){
        for(int i=1; i<=count; i++){
            createPropertyWithCreatedByTCparameter(propertyName,propertyValue,createdByTestCaseValue, i)
        }
    }

    static deletePropertyCreatedByTC(String propertyName, int index =1){
        removeTcProperty(propertyName+index)
        removeTcProperty(propertyName+"CreatedByTest"+index)
    }

    // Забирает текст из файла и возвращает его
    // Чтобы метод отработал файл должен находиться в той же директории, что и SOAPUI проект, либо в дочерних папках
    //В тест кейсе должна быть проперти  directory со значением = путь до файла с именем из папки проекта
    static returnFileAsText(String directoryPropertyName){
        String fullPath=testRunner.getTestCase().getTestSuite().getProject().getPath()
        String projectPath= Paths.get(fullPath).getParent();
        def directory=getTcPropertyValue(directoryPropertyName)
        String path=projectPath+directory
        def file  = new File(path).text
        return file
    };

    static stringToIntegerList(String stringList){
        def list=stringList.split(',').collect{it as int}
        return list
    }

    static listToString(List list){
        String stringList=list.toString().replace('[','').replace(']','')
        return stringList
    }

    static compareMaps(Map m1, Map m2) {
        String errors=""
        int countErrors = 0
        m1.each{k,v->
            if(m2[k] != m1[k]) {
                countErrors=countErrors+1
                errors=errors+"Параметр ${k}, значение в первом объекте =${m1[k]}, значение во втором =${m2[k]}.  "
            }
        }
        if(countErrors){
            fail("Обнаружены несоответствия данных в сообщении и в БД. Их количество : "+countErrors)
            loger(errors)
        }
        else {
            loger("Объекты идентичны")
        }
    }

    static getListFromProperties(int count, int startIndex=1, String propertyName, String format = ""){
        def list=[]
        for(int i=startIndex; i<=count; i++){
            String value = getTcPropertyValue(propertyName+i)
            list << value
        }
        List newList=[]
        if(format !=""){
            switch (format.toLowerCase().trim()) {
                case "long":
                    list.each {newList << Long.valueOf(it) }
                    loger(newList[0].getClass())
                    return newList
                case "bool":
                    list.each {newList << Boolean.valueOf(it) }
                    return newList
                    loger(newList[0].getClass())
                case "boolean":
                    list.each {newList << Boolean.valueOf(it) }
                    return newList
                    loger(newList[0].getClass())
                default:
                    loger("не знаю такой формат, отдам как есть")
            }
        }
        return list
    }

    static getStringListFromProperties(int count, int startIndex=1, String propertyName){
        def list=getListFromProperties(count,startIndex,propertyName)
        String stringList=listToString(list)
        return stringList
    }

    static compareLists(List list1, List list2){
        List logList=[]
        int errors=0
        if(list1.size() != list2.size()){
            String text="Списки имеют разное колличество элементов. в первом списке ="+list1.size()+", во втором -"+list2.size()
            logList << text
            errors=errors+1
        }
        else{
            String text="Списки имеют одинаковое колличество элементов равное "+list1.size()
            logList << text
        }
        list1.sort()
        list2.sort()
        if( list1 != list2){
            def dif1 = list1.toSet()-list2.toSet()
            def dif2 = list2.toSet()-list1.toSet()
            errors = errors + dif1.size() + dif2.size()
            if(dif1){
                String textDif="В списке 1 содержатся элементы, которых нет в списке 2. Количетсво таких элементов="+dif1.size()+", список этих элементов: "+dif1
                logList << textDif
            }
            if(dif2) {
                String textDif = "В списке 2 содержатся элементы, которых нет в списке 1. Количетсво таких элементов=" + dif2.size() + ", список этих элементов: " + dif2
                logList << textDif
            }
        }
        else logList << "Элементы списков одинаковые"
        if(errors) fail("При сравнении списков были обнаружены несоответсвия. Их количество="+errors)
        logList.each{
            loger(it)
        }
    }

    def collectPropertyNames(String text, Boolean skipCBTC=true){
        Map map=testRunner.testCase.getProperties()
        List list = []
        map.each{
            key, value ->
                if( key.toLowerCase().contains(text.toLowerCase())){
                    if( skipCBTC){
                        String skipText="CreatedByTest"
                        if( !key.toLowerCase().contains(skipText.toLowerCase())){
                            list << key
                        }
                    }
                    else  list << key
                }
        }
        return list
    }

    def separateIndexFromPropertyNames(List list){
        List propNamesList=[]
        String numbers='0123456789'
        list.each{
            String name=it
            String index=""
            String propName=""
            for(int i=0; i<name.size(); i++){
                if( numbers.contains(name[i])) index += name[i]
                else propName += name[i]
            }
            if(index != "") propNamesList << [propName : propName, index : index.toInteger()]
        }
        return propNamesList
    }

    def deleteTcPropertiesByNames(String propertyName){
        List propNamesList=collectPropertyNames(propertyName,false)
        if(propNamesList !=[]) {
            List separatedList = separateIndexFromPropertyNames(propNamesList)
            separatedList.each{
                removeTcProperty(it.propName+it.index.toString())
            }
        }
    }
}
