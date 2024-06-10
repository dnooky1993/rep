import java.text.SimpleDateFormat
import java.nio.file.Paths
class ScriptLibrary {

    def static context
    def static testRunner
    def static log
    public KafkaLib kafka
    public SqlLib sql
    public Utils utils
    public DatesLib dates

    ScriptLibrary(def context, def testRunner, def log) {
        this.context=context
        this.testRunner=testRunner
        this.log=log
        //this.sql=new SqlLib(context, testRunner, log)
//        log.info ("имя кейса в конструкторе ScriptLibrary : "+testRunner.testCase.getName())
    }
//


    SqlLib sql(){
        if (!sql){
            sql = new SqlLib(utils())
//            log.info ("имя кейса в собираторе SqlLib: "+testRunner.testCase.getName())
        }
        else{
            sql.utils.testRunner=testRunner
//            log.info ("имя кейса в собираторе SqlLib2: "+testRunner.testCase.getName())
        }

        return sql
    }

    KafkaLib kafka(){
        if (!kafka){
            kafka = new KafkaLib(utils())
        }
        else {
            kafka.utils.testRunner=testRunner
        }
        return kafka
    }

    Utils utils(){
        if (!utils){
            utils = new Utils(context, testRunner, log)
        }
        else{
            utils.testRunner=testRunner
        }

        return utils
    }
    DatesLib dates(){
        if (!dates){
            dates = new DatesLib(utils())
        }
        else{
            dates.utils.testRunner=testRunner
        }
        return dates
    }
}

class ScBuilder{
    static ScriptLibrary scripts

    static init(def context, def testRunner, def log){
        if (!scripts){
            scripts = new ScriptLibrary(context, testRunner, log)
            log.info "Создаю новый ScriptLibrary"
//            log.info ("имя кейса в собираторе ScBuilder : "+testRunner.testCase.getName())
        }
        else{
            scripts.testRunner = testRunner
//            log.info ("имя кейса в собираторе ScBuilder : "+testRunner.testCase.getName())
        }
        return scripts
    }
}