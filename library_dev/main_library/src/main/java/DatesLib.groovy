import java.text.SimpleDateFormat

class DatesLib {
    Utils utils

    DatesLib(Utils utils) {
        this.utils=utils
    }

    //получает текущую дату и время в формате long
    //используется для поиска сообщений в кафке, которые были отправлены после этого времени
    def getCurrentDateTime(){
        long currentDateTime = System.currentTimeMillis()
        String currentDate=String.valueOf(currentDateTime)
        utils.loger(currentDateTime.getClass())
        utils.loger(currentDate+" "+currentDateTime)
        utils.setTcPropertyValue("currentDateTime", currentDate)
    }

    // записывает в проперти кейса текущую дату в переданном формате или если он пуст берет формат из проперти сьюта
    def getCurrentDate(String format=null){
        String currentDate=utils.getTcPropertyValue("currentDate")
        String dateTimeFormat= format==null ? utils.getTcPropertyValue("dateTimeFormat") : format
        def formater=new SimpleDateFormat(dateTimeFormat)
        def formatedCurrentDate=new SimpleDateFormat(dateTimeFormat).parse(currentDate)
        def d2=formater.format(formatedCurrentDate)
        utils.loger(d2)
        return d2
    }


    def saveCurrentDateToProperty(String format=null){
        def currentDate=returnCurrentDate(format)
        utils.setTcPropertyValue("currentDate",currentDate)
    };

    // возвращает текущую дату в переданном формате или если он пуст берет формат из проперти сьюта
    def returnCurrentDate(String format=null){
        String dateTimeFormat= format==null ? utils.getTsPropertyValue("dateTimeFormat") : format
        def date = new Date()
        def sdf = new SimpleDateFormat(dateTimeFormat)
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+03"))
        def currentDate=sdf.format(date)
        utils.loger(currentDate)
        return currentDate
    }

    //возвращает переданную дату в переданном формате или если он пуст берет формат из проперти сьюта
    def formatDBdate(String date, String format =null ){
        format= format==null ? utils.getTsPropertyValue("dateTimeFormat") : format
        formatStringDate(date, "yyyy-MM-dd HH:mm:ss.SSSSS", format )
    }

    def formatStringDate(String date, String oldFormat, String newFormat =null ){
        newFormat= newFormat==null ? utils.getTsPropertyValue("dateTimeFormat") : newFormat
        def formatDate=new SimpleDateFormat(newFormat)
        Date date2=new SimpleDateFormat(oldFormat).parse(date)
        def stringDate = formatDate.format(date2)
        return stringDate
    }

    def convertStringToDate(String date, String oldFormat, String newFormat =null){
        String stringFormattedDate=formatStringDate(date,oldFormat,newFormat)
        def formattedDate=new SimpleDateFormat(newFormat).parse(stringFormattedDate)
        return formattedDate
    }

    def formatStringDateForScriptAssert(String date, String oldFormat, String newFormat =null ){
        newFormat= newFormat==null ? messageExchange.modelItem.testStep.testCase.testSuite.getPropertyValue("dateTimeFormat"): newFormat
        def formatDate=new SimpleDateFormat(newFormat)
        Date date2=new SimpleDateFormat(oldFormat).parse(date)
        def dbDate = formatDate.format(date2)
        return dbDate
    }

}
