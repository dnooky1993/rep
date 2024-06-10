import groovy.json.JsonSlurper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata

import java.sql.Timestamp

class KafkaLib {
    Utils utils

    KafkaLib(Utils utils) {
        this.utils=utils
    }

    //Отправляет сообщение в указанный топик кафки
    //Для корректной работы в проперти проекта должен быть параметр bootstrapServers
    def sendMessageToKafka(String topicName, String textOfMessage){
        def bootstrapServers= utils.getProjectPropertyValue("bootstrapServers")
        Properties props= putPropertiesToSend(bootstrapServers)
        def producer = new KafkaProducer(props)
        messageSender(topicName, textOfMessage, producer)
        //producer.close()
    };

    //Отправляет сообщение в указанный топик кафки
    //Для корректной работы в проперти проекта должен быть параметр bootstrapServers
    //Имя топика берется из проперти проекта
    def sendMessageToKafka2(String topicName, String textOfMessage){
        String topic = utils.getProjectPropertyValue(topicName);
        def bootstrapServers= utils.getProjectPropertyValue("bootstrapServers")
        utils.loger(bootstrapServers)
        //Отправка сообщения в топик
        Properties props= putPropertiesToSend(bootstrapServers)
        def producer = new KafkaProducer(props)
        messageSender(topic, textOfMessage, producer)
    };

    def messageSender(String topic, String message, def producer ){
        utils.loger( "messageSender: "+topic+"; "+message )
        def key =  UUID.randomUUID().toString()
        Date date = new Date();
        long  timestamp= date.getTime()
        int partition =0
        String compoundMessage = "$message"

        producer.send(
                new ProducerRecord<String, String>(topic, partition, timestamp,  key, compoundMessage),
                { RecordMetadata metadata, Exception e ->
                    utils.loger( "The offset of the record we just sent is: ${metadata.offset()}")
                } as Callback
        )
        producer.close()
    }

    def putPropertiesToSend(bootstrapServers){
        Properties props = new Properties()
        props.put('zk.connect', '10.2.172.24:2181')
        props.put('bootstrap.servers', bootstrapServers)
        props.put('key.serializer', 'org.apache.kafka.common.serialization.StringSerializer')
        props.put('value.serializer', 'org.apache.kafka.common.serialization.StringSerializer')
        return props
    }

    def putPropertiesToGet(bootstrapServers){
        Properties props = new Properties()
        props.put('zk.connect', '10.2.172.24:2181')
        props.put('bootstrap.servers',bootstrapServers)
        props.put('group.id', UUID.randomUUID().toString())
        props.put("enable.auto.commit", "false")
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        props.put('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
        props.put('value.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
    }

    //Возвращает последнее сообщение из указанного топика с указанным значением параметра
    //Ищет среди сообщений полученных после currentDateTime(метод getCurrentDateTime)
    def getKafkaLastMessageJson( String topicName, String parameter, String value, Long time=18000){
        List messages=getKafkaAllMessageJson( topicName, parameter, value, time)
        return messages.last()
    }

    //Возвращает все сообщения из указанного топика с указанным значением параметра
    //Ищет среди сообщений полученных после currentDateTime(метод getCurrentDateTime)
    def getKafkaAllMessageJson( String topicName, String parameter, String value, Long time=18000){
        String topic =utils.getProjectPropertyValue(topicName);
        String bootstrapServers =utils.getProjectPropertyValue("bootstrapServers");
        long currentDateTime =utils.getTcPropertyValue("currentDateTime").toLong();

        Properties props = putPropertiesToGet(bootstrapServers)
        def consumer = new KafkaConsumer(props)

        consumer.subscribe([topic])
        def messageToReturn = null;
        long t = System.currentTimeMillis();
        long end = t + time;
        List messages =[]
        while (System.currentTimeMillis()<end && messageToReturn == null)
        {
            utils.loger ( "while ")
            ConsumerRecords<String, String> records = consumer.poll(time)
            for (ConsumerRecord<String, String> record : records)
            {
                def rootNode
                try {
                    rootNode = new JsonSlurper().parseText(record.value())
                    String messageParamValue=parsePath(rootNode, parameter)
                    if (messageParamValue == value && record.timestamp() >= currentDateTime) {
                        utils.loger("Новое сообщение : "+record.value()+" время "+ new Timestamp(record.timestamp()));
                        messages << record.value()

                    }
                } catch(Exception e) {continue}
            }
        }
        consumer.close()
        utils.loger ( "close ")
        return messages
    }

    //Возвращает значение переданного узла в json
    def parsePath(Object node, String path){
        String[] arr = path.replace("[",".").replace("]","").split("\\.");
        Object obj=node
        for (int i = 0; i < arr.size(); i++) {
            def key = arr[i].isInteger()  ? Integer.parseInt(arr[i]) : arr[i]
            obj = obj.get(key);
        }
        return obj.toString()
    }

}
