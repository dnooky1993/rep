import groovy.sql.Sql

class SqlLib {
    Sql sql
    Utils utils

    SqlLib(Utils utils) {
        this.utils=utils
        this.sql=createDBConnection()
    }

    //Создание коннекта к бд для груви скриптов.
    // Для использования метода из библиотеки отдельно вызывать не нужно
    Sql createDBConnection(){
        if (!sql) {
            utils.loger("Новое подключение к БД")
            def login = utils.getProjectPropertyValue("dbLogin")
            def password = utils.getProjectPropertyValue("dbPassword")
            def dbconstring = utils.getProjectPropertyValue("dbConnect")
            def dbType = utils.getProjectPropertyValue("dbType")
            switch (dbType) {
                case "oracle":
                    return Sql.newInstance("jdbc:oracle:thin:" + login + "/" + password + dbconstring, login, password, "oracle.jdbc.driver.OracleDriver");
                case "postgre":
                    return Sql.newInstance("jdbc:postgresql://" + dbconstring + "?user=" + login + "&password=" + password, "org.postgresql.Driver");
                default:
                    utils.fail("метод createDBConnection не настроен на соединение с базой типа " + dbType);
                    break;
            }
        }
        else{
            utils.loger("Текущее подключение к БД")
            return sql
        }
    }

    //возвращает функцию замены значения null на значение для полученного типа БД
    def getCheckNullFunctionName() {
        def dbType=utils.getProjectPropertyValue("dbType")
        switch(dbType) {
            case "oracle":
                return "nvl"
            case "postgre":
                return "coalesce"
            default:
                utils.fail("метод createDBConnection не настроен на соединение с базой типа "+dbType);
                break;
        }
    }

    //возвращает запись из БД по id
    def returnEntityFromDBbyId(String tableName, long id){
        String table = utils.getProjectPropertyValue(tableName)
        def entity=getEntityFromDb(tableName, id)
        if (!entity ){
            utils.fail("В таблице "+table+" не найдена запись с id "+id);
        }
        else {
            utils.loger("В таблице "+table+" найдена запись с id "+id)
            //log.info(lastItem)
        }
        return entity
    }

    def returnRows(String tableName, String condition){
        String table = utils.getProjectPropertyValue(tableName)
        def rows=returnQueryResult("SELECT * FROM "+table+" "+condition)
        return rows
    }

    def returnQueryResult(String query){
        def result=sql.rows(query)
        return result
    }

    def returnQueryFirstRow(String query){
        def result=sql.firstRow(query)
        return result
    }

    def executeQuery(String query){
        sql.execute(query)
    }

    //возвращает первую найденную запись в указанной таблице с указанным id
    def getEntityFromDb(String tableName, long id){
        String table = utils.getProjectPropertyValue(tableName);
        String getEntity = ("SELECT * FROM "+table+"  WHERE ID="+id);
        def entity= sql.firstRow(getEntity);
        utils.loger(entity);
        return entity
    }

    // проверяет наличие в указанной таблице записи с указанным id, возвращает результат проверки
    def checkExistenceInDb(String tableName, long id){
        def entity=getEntityFromDb(tableName, id)
        if(entity){
            return true
        }
        else{
            return false
        }
    }

    //возвращает несущетвующий в таблице id
    def getNonExistedIdInDb(String table, int k=100){
        String functionName=getCheckNullFunctionName()
        String query = "SELECT "+functionName+"(max(id)+"+k+",1) as ID FROM "+table
        def result =sql.firstRow(query)
        utils.loger("ID несуществубщей записи в таблице  "+table+ " = "+result.ID.toString())
        return result.ID
    }

    // Находит несуществующий id из указанной таблицы и записывает в проперти тест кейса
    def getDoesntExisted(String table, String propertyName, int counter=1){
        long id=getNonExistedIdInDb(table)
        for(int i=1; i<=counter; i++){
            utils.createPropertyWithCreatedByTCparameter(propertyName, id.toString(),'1', i )
            id=id+1
        }
    }

    def getDoesntExistedForPush(String tableName, String propertyName, int index=1){
        String table = utils.getProjectPropertyValue(tableName)
        int n= utils.getTsPropertyValue("n").toInteger()
        int k= n+2
        long id=getNonExistedIdInDb(table, k)
        utils.createPropertyWithCreatedByTCparameter(propertyName, id.toString(),'1', index )
        k=k+index
        utils.setTsPropertyValue("n", k.toString())
    }

    def getDoesntExistedForPushWithCount(String tableName, String propertyName, int counter=1){
        for(int i=1; i<=counter; i++){
            getDoesntExistedForPush(tableName,propertyName,counter)
        }
    }

    //находит нужное количество записей в указанной таблице по условию и записывает их в проперти
    //если не находит записи то досоздает их
    //в параметр values не нужно передавать id
    def findOrCreateEntitiesInDBwithConditionAndCount ( String tableName, String columns, String values , String propertyName, int count, String condition=""  ){
        String table = utils.getProjectPropertyValue(tableName)
        String query = "SELECT * FROM "+table+ " "+condition
        utils.loger(query)
        String functionName=getCheckNullFunctionName()
        String queryGetNewId="SELECT "+functionName+"(max(id)+1,1)AS ID FROM "+table
        def result =sql.rows(query)
        def rows = result != null ? result.size() : 0;
        Long sub=count-rows
        def id = sql.firstRow(queryGetNewId).ID
        if (rows >0){
            int n=0
            if(sub>=0){
                n=rows
            }
            else{
                n=count
            }
            for(int i=0; i<n;i++){
                int nextI=i+1
                def resultId= result[i].ID
                utils.loger("Таблица "+table+ " имеет подходящую запись № "+nextI+" с id = "+resultId)
                utils.createPropertyWithCreatedByTCparameter(propertyName,resultId.toString(),"0",nextI)
            }
        }
        if(sub>0){
            for( int i=rows; i<count ; i++){
                int nextI=i+1
                sql.execute("insert into  "+table+ " ("+columns+") values( "+id+","+values+") ")
                utils.loger("В таблице  "+table+ " нет или недостаточно записей по условию, создаю новую запись № "+nextI+"  с id ="+id)
                utils.createPropertyWithCreatedByTCparameter(propertyName,id.toString(),"1",nextI)
                id=id+1
            }
        }
    }

    //Очищает созданную автотестом запись в БД и чистит по ней проперти, из пропертей забирает запись с индексом
    def clearCreatedByTestInTestCaseWithIndex(String tableName,String propertyName, int index =1){
        String table = utils.getProjectPropertyValue(tableName)
        def propertyValue  = utils.getTcPropertyValue(propertyName+index)
        def createdByTest  = utils.getTcPropertyValue(propertyName+"CreatedByTest"+index).toBoolean()
        if (createdByTest){
            sql.execute("DELETE FROM "+table+ " WHERE ID =" +propertyValue)
            utils.loger("Запись в таблице "+table+ " с ID ="+propertyValue+" была создана в рамках теста  и удалена ")
        }
        else {
            utils.loger("Тест использовал существующую запись в таблице "+table+" с ID ="+propertyValue+". Запись не будет удалена. ")
        }
        utils.deletePropertyCreatedByTC(propertyName, index)
    }

    def clearCreatedByTestInTestCaseWithCount(String tableName,String propertyName, int count =1){
        for (int i = 1; i <= count; i++) {
            clearCreatedByTestInTestCaseWithIndex(tableName,propertyName, i)
        }
    }

    def createEntitiesInDB(String tableName, String columns, String values , Long id=null){
        String table = utils.getProjectPropertyValue(tableName);
        if (id ==null){
            id=getNonExistedIdInDb(table)
        }
        String createQuery="insert into  "+table+ " ("+columns+") values( "+id+","+values+") ";
        utils.loger("createQuery: "+createQuery);
        sql.execute(createQuery);
        utils.loger("В таблице  "+table+ " создаю новую запись с id ="+id);
    }

    //Создает переданное количество записей в таблице с указанными параметрами
    def createEntitiesInDBwithCount(String tableName, String columns, String values , String propertyName, int count=1){
        String table = utils.getProjectPropertyValue(tableName);
        long id=getNonExistedIdInDb(table)
        for( int i=1; i<=count ; i++){
            createEntitiesInDBwithIndex(tableName,columns,values,propertyName,i,id)
            id=id+1
        }
    }

    //Создает запись в бд с переданными параметрами и записывает в проперти с указанным индексом
    def createEntitiesInDBwithIndex(String tableName, String columns, String values , String propertyName, int index=1, Long id=null){
        String table = utils.getProjectPropertyValue(tableName);
        if (id ==null){
            id=getNonExistedIdInDb(table)
        }
        createEntitiesInDB(tableName,columns,values,id)
        utils.createPropertyWithCreatedByTCparameter(propertyName,id.toString(),"1",index)
    }

    def createEntitiesInDBForPush(String tableName, String columns, String values , String propertyName, int count=1){
        getDoesntExistedForPushWithCount(tableName,propertyName,count)
        for(int i=1; i<=count; i++) {
            Long id = utils.getTcPropertyValue(propertyName+i).toLong()
            createEntitiesInDB(tableName, columns, values, id)
        }
    }


    def createEntitiesInDBWithValuesInProperty(String tableName, String columns, String values ,String propertyName, String valueToReplace, int startIndex=1, int lastIndex=1, Long id=null){
        String table = utils.getProjectPropertyValue(tableName);
        if (id ==null){
            id=getNonExistedIdInDb(table)
        }
        String fullQuery=""
        for(int i=startIndex; i<=lastIndex; i++){
            String property=utils.getTcPropertyValue(propertyName+i.toString())
            String end = i != startIndex ?  " " : ";"
            String query="insert into "+table+" ("+columns+") values("+id+","+values+")"+end
            fullQuery = fullQuery + query.replace(valueToReplace, property)
        }
        utils.loger("Запрос на создание данных: "+fullQuery);
        sql.execute(fullQuery)
    }
    //собирает значения пропертей в строку через запятую
    def collectValuesForInsert(String propertyNames){
        def properties=propertyNames.split(',').collect{it as String}
        String values=""
        for(int i=0; i<properties.size(); i++){
            String value=utils.getTcPropertyValue(properties[i])
            value = value ? value : properties[i]
            if(i==0){
                values=value
            }
            else{
                values= values+","+value
            }
        }
        return values
    }

    def getListParametersValueFromTAble(String tablePropertyName, Long id, String idColumn, String column){
        String table =utils.getProjectPropertyValue(tablePropertyName)
        List result=sql.rows("select "+column+" from "+table+" where "+idColumn+"="+id+" order by "+column)
        List values = []
        result.each {
            values.addAll(it.values())
        }
        return values
    }

    def clearTcPropertiesByNamesList(List propNamesList, String tableName){
        propNamesList.each{
            clearCreatedByTestInTestCaseWithIndex(tableName, it.propName, it.index)
        }
    }

    def clearPropertiesByPropertyName(String propertyName, String tableName){
        List propNamesList=utils.collectPropertyNames(propertyName)
        if(propNamesList !=[]) {
            List separatedList = utils.separateIndexFromPropertyNames(propNamesList)
            clearTcPropertiesByNamesList(separatedList, tableName)
        }
    }

}
