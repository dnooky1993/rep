import groovy.json.JsonSlurper
import groovy.sql.Sql
import groovy.xml.XmlSlurper
import org.apache.kafka.clients.consumer. *
import java.sql.Timestamp
import java.time.*
import java.time.temporal.*



class Rir2Library {

    def context
    def testRunner
    def log
	def mainScripts

	Rir2Library(def context, def testRunner, def log) {
		this.context=context
		this.testRunner=testRunner
		this.log=log
		this.mainScripts = ScBuilder.init(context, testRunner, log)
	}

	def getTreatmentsWithMapping(int requiredNumber ){
		//def sql=createDBConnection()
		def tableT=testRunner.testCase.testSuite.project.getPropertyValue("tableT")
		def tableTC=testRunner.testCase.testSuite.project.getPropertyValue("tableTC")
		def tableRTLM=testRunner.testCase.testSuite.project.getPropertyValue("tableRTLM")
		String getTreatmentsWithMapping = "SELECT DISTINCT t.id AS TREATMENT_ID, t.CODE  AS TREATMENT_CODE,tc.CODE  AS TREATMENT_CATEGORY_CODE, t.TREATMENT_CATEGORY_ID FROM "+tableT+" t JOIN "+tableRTLM+"  rtlm ON t.CODE=rtlm.TREATMENT_CODE JOIN "+tableTC+"  tc ON tc.id=t.TREATMENT_CATEGORY_ID WHERE t.ARCHIVED=false AND tc.ARCHIVED =false ORDER BY t.id DESC"
		//log.info(getTreatmentsWithMapping)
		def treatmentsWithMapping=sql.rows(getTreatmentsWithMapping)
		def countTreatmentsWithMapping=treatmentsWithMapping.size()
		//log.info("countTreatmentsWithMapping="+countTreatmentsWithMapping)
		def sub= requiredNumber-countTreatmentsWithMapping
		int lastId=0
		String listOfTreatmensWithMapping="0"
		if(countTreatmentsWithMapping>0){
			if(countTreatmentsWithMapping>requiredNumber){
				lastId=requiredNumber
			}
			else{
				lastId=countTreatmentsWithMapping
			}
			//log.info("lastId1= "+lastId)
			for(int i=0; i<lastId; i++){
				int nexti=i+1
				def treatmentId = treatmentsWithMapping[i].TREATMENT_ID.toString()
				def treatmentCode = treatmentsWithMapping[i].TREATMENT_CODE.toString()
				def treatmentCategoryId = treatmentsWithMapping[i].TREATMENT_CATEGORY_ID.toString()
				def treatmentCategoryCode = treatmentsWithMapping[i].TREATMENT_CATEGORY_CODE.toString()
				def getMppingCode="SELECT * FROM "+tableRTLM+" WHERE TREATMENT_CODE='"+treatmentCode+"' order by LDP_CODE"
				//log.info(getMppingCode)
				def mappingCode=sql.rows(getMppingCode)
				//log.info(mappingCode)
				def mCode=mappingCode[0].LDP_CODE.toString()
				testRunner.testCase.setPropertyValue("tId"+nexti, treatmentId)
				testRunner.testCase.setPropertyValue("tCreatedByTest"+nexti, "0")
				testRunner.testCase.setPropertyValue("tCode"+nexti, treatmentCode)
				testRunner.testCase.setPropertyValue("tcId"+nexti, treatmentCategoryId)
				testRunner.testCase.setPropertyValue("tcCreatedByTest"+nexti, "0")			
				testRunner.testCase.setPropertyValue("tcCode"+nexti, treatmentCategoryCode)
				testRunner.testCase.setPropertyValue("tMappingCode"+nexti, mCode)
				testRunner.testCase.setPropertyValue("tMappingCodeCreatedByTest"+nexti, "0")
				log.info("Для записи №"+nexti+" есть ММ с маппингом, ее id = "+treatmentId)
				listOfTreatmensWithMapping=listOfTreatmensWithMapping+","+treatmentId.toString()
			}
		}
		if(sub>0){
			String getNonarchivedTreatments = "SELECT DISTINCT t.id AS TREATMENT_ID, t.CODE  AS TREATMENT_CODE,tc.CODE  AS TREATMENT_CATEGORY_CODE, t.TREATMENT_CATEGORY_ID FROM "+tableT+" t JOIN "+tableTC+"  tc ON tc.id=t.TREATMENT_CATEGORY_ID WHERE t.ARCHIVED=0 AND tc.ARCHIVED =0 and t.id not in ("+listOfTreatmensWithMapping+") ORDER BY t.id DESC"
			//log.info(getNonarchivedTreatments)
			def  nonarchivedTreatments=sql.rows(getNonarchivedTreatments)
			def countNonarchivedTreatments=nonarchivedTreatments.size()
			log.info("countNonarchivedTreatments= "+countNonarchivedTreatments)
			String getNonarchivedTreatmentCategories = "SELECT DISTINCT tc.CODE  AS TREATMENT_CATEGORY_CODE, tc.ID TREATMENT_CATEGORY_ID FROM "+tableTC+" tc WHERE tc.ARCHIVED =0 ORDER BY tc.id DESC"
			//log.info(getNonarchivedTreatmentCategories)
			def nonarchivedTreatmentCategories=sql.rows(getNonarchivedTreatmentCategories)
			def countNonarchivedTreatmentCategories=nonarchivedTreatmentCategories.size()
			log.info("countNonarchivedTreatmentCategories= "+countNonarchivedTreatmentCategories)
			boolean needToCreateTreatmentCategory=true
			//sub2=sub-countNonarchivedTreatments
			int index=0
			int newLastId=lastId+1
			for(int i=newLastId; i<=requiredNumber; i++){
				if(countNonarchivedTreatments>0){
						def treatmentId = nonarchivedTreatments[index].TREATMENT_ID.toString()
						def treatmentCode = nonarchivedTreatments[index].TREATMENT_CODE.toString()
						def treatmentCategoryId = nonarchivedTreatments[index].TREATMENT_CATEGORY_ID.toString()
						def treatmentCategoryCode = nonarchivedTreatments[index].TREATMENT_CATEGORY_CODE.toString()
						String tCreatedByTest="0"
						String tcCreatedByTest="0"
						countNonarchivedTreatments=countNonarchivedTreatments-1
						log.info("Для записи №"+i+" есть неархивная ММ без маппинга, ее id = "+treatmentId)
						index=index+1
				}
				else{
						if(countNonarchivedTreatmentCategories>0){
							def treatmentCategoryId = nonarchivedTreatmentCategories[0].TREATMENT_CATEGORY_ID.toString()
							def treatmentCategoryCode = nonarchivedTreatmentCategories[0].TREATMENT_CATEGORY_CODE.toString()
							tcCreatedByTest="0"
						}
						else{
							if(needToCreateTreatmentCategory==true){
								def treatmentCategoryId=sql.rows("select max(id)+10 AS ID from "+tableTC)[0].ID
								def treatmentCategoryCode="autotest_treatment_category_code"
								sql.execute("INSERT INTO "+tableTC+" (ID, CODE, TITLE, ARCHIVED) VALUES ("+treatmentCategoryId+", '"+treatmentCategoryCode+"', 'Категория ММ автотест', '0')")
								String tcCreatedByTest="1"
								needToCreateTreatmentCategory=false
								log.info("Требуется создать неархивную Категорию ММ. Создаю Категорию ММ, ее id = "+treatmentCategoryId)
							}
						}
						def treatmentId=sql.rows("select max(id)+1 AS ID from "+tableT)[0].ID
						String treatmentCode="autotest_treatment_code"+i.toString()
						log.info(" Для записи №"+i+" требуется создать неархивную ММ. Создаю ММ, ее id = "+treatmentId)
						sql.execute("INSERT INTO  "+tableT+"  (ID, ARCHIVED, TITLE, DEFAULT_DURATION, TREATMENT_CATEGORY_ID, CODE) VALUES("+treatmentId+", '0', 'Мед манипуляция №"+i+" автотест ', 60, "+treatmentCategoryId+", '"+treatmentCode+"')")
						String tCreatedByTest="1"
					}
				testRunner.testCase.setPropertyValue("tId"+i, treatmentId.toString())
				testRunner.testCase.setPropertyValue("tCreatedByTest"+i, tCreatedByTest)
				testRunner.testCase.setPropertyValue("tCode"+i, treatmentCode.toString())
				testRunner.testCase.setPropertyValue("tcId"+i, treatmentCategoryId.toString())
				testRunner.testCase.setPropertyValue("tcCreatedByTest"+i, tcCreatedByTest)			
				testRunner.testCase.setPropertyValue("tcCode"+i, treatmentCategoryCode.toString())
				mappingId=sql.rows("select max(id)+1 AS ID from "+tableRTLM)[0].ID
				String mCode="autotest_ldp_code"+i
				log.info("Создаю маппинг для записи №"+i+",  id ММ= "+treatmentId)
				sql.execute("INSERT INTO "+tableRTLM+" (ID, TREATMENT_CODE, LDP_CODE) VALUES("+mappingId+", '"+treatmentCode+"', '"+mCode+"')")
				testRunner.testCase.setPropertyValue("tMappingCode"+i, mCode)
				testRunner.testCase.setPropertyValue("tMappingCodeCreatedByTest"+i, "1")
			}
		}
	};

	def clearTreatmentsWithMapping(int count, int firstIndex=1 ){
		String tableT=testRunner.testCase.testSuite.project.getPropertyValue("tableT")
		String tableTC=testRunner.testCase.testSuite.project.getPropertyValue("tableTC")
		String tableRouteTreatmentMapping=testRunner.testCase.testSuite.project.getPropertyValue("tableRTLM")
		for( int i=firstIndex; i<=count; i++){
			String treatmentId = testRunner.testCase.getPropertyValue("tId"+i)
			String tCreatedByTest=testRunner.testCase.getPropertyValue("tCreatedByTest"+i)
			String treatmentCode = testRunner.testCase.getPropertyValue("tCode"+i)
			String tMappingCode = testRunner.testCase.getPropertyValue("tMappingCode"+i)
			String tMappingCodeCreatedByTest=testRunner.testCase.getPropertyValue("tMappingCodeCreatedByTest"+i)
			if(tMappingCodeCreatedByTest=='1'){
				log.info("Для записи №"+i+" маппинг был создан автотестом. Удаляю")
				sql.execute("DELETE FROM "+tableRouteTreatmentMapping+" WHERE TREATMENT_CODE='"+treatmentCode+"'")
			}
			if(tCreatedByTest=='1'){
				log.info("Для записи №"+i+" ММ №"+treatmentId+" была создана автотестом. Удаляю")
				sql.execute("DELETE FROM "+tableT+"  WHERE ID='"+treatmentId+"'")
			}
			log.info("Очищаю проперти маппинга и ММ для записи №"+i)
			testRunner.testCase.removeProperty("tMappingCode"+i)
			testRunner.testCase.removeProperty("tMappingCodeCreatedByTest"+i)
			testRunner.testCase.removeProperty("tCode"+i)
			testRunner.testCase.removeProperty("tCreatedByTest"+i)
			testRunner.testCase.removeProperty("tId"+i)
		}
		for( int i=firstIndex; i<=count; i++){
			String treatmentCategoryId = testRunner.testCase.getPropertyValue("tcId"+i)
			String tcCreatedByTest=testRunner.testCase.getPropertyValue("tcCreatedByTest"+i)
			if(tcCreatedByTest=='1'){
				("Для записи №"+i+" Категория ММ №"+treatmentCategoryId+" была создана автотестом. Удаляю")
				sql.execute("DELETE FROM "+tableTC+"  WHERE ID='"+treatmentCategoryId+"'")
			}
			log.info("Очищаю проперти Категории ММ для записи №"+i)
			testRunner.testCase.removeProperty("tcId"+i)
			testRunner.testCase.removeProperty("tcCreatedByTest"+i)
			testRunner.testCase.removeProperty("tcCode"+i)
		}
	};

	def getSendedOperationMessages(String topicName, String operationPropertyName){
		def operationId= testRunner.testCase.getPropertyValue(operationPropertyName);
		String topic =testRunner.testCase.testSuite.project.getPropertyValue(topicName);
		String bootstrapServers =testRunner.testCase.testSuite.project.getPropertyValue("bootstrapServers");
		//def operationId= testRunner.testCase.getPropertyValue("testOperationId")
		//String topic =testRunner.testCase.testSuite.project.getPropertyValue("topicScheduleProcessingTest")
		log.info ("ID операции: " + operationId)
		long currentDateTime =testRunner.testCase.getPropertyValue("currentDateTime").toLong();

		Properties props = new Properties()
		props.put('zk.connect', '10.2.172.24:2181')
		props.put('bootstrap.servers',bootstrapServers)
		props.put('group.id', UUID.randomUUID().toString())
		props.put("enable.auto.commit", "false")
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
		props.put('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
		props.put('value.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
		def offset =null
		def value =null
		def messages = []
		def consumer = new KafkaConsumer(props)

            consumer.subscribe([topic])

            long t = System.currentTimeMillis()
            long end = t + 18000;
            while (System.currentTimeMillis()<end)
            {
                log.info ( "while ")
                ConsumerRecords<String, String> records = consumer.poll(18000)
                log.info ( "records")
                for (ConsumerRecord<String, String> record : records)
                {
                    //log.info(record.value())
                    def rootNode
                    try {
                        rootNode = new XmlSlurper().parseText(record.value())
                    } catch(Exception e) {continue}
                    if (rootNode.operationId ==operationId && record.timestamp() >= currentDateTime) {
                        log.info("Есть сообщение с operationId= "+ operationId)
                        //value= record.value()
                        //log.info ("Сообщение:"+value)
                        log.info ("Сообщение: "+record.value())
                        //consumer.close()
                        log.info ( "close ")
						messages << record.value()
                    }
                    //log.info ( "offset = " + record.offset() +" value = " + record.value())
                    //offset= record.offset()
                    //value= record.value()
                }
            }
            consumer.close()
		log.info ( "close ")
		return messages
		//def valueScheduleChange = value
	};

	def checkResourceMessage( long resourceId, boolean expectMessage=true){
		String topic =testRunner.testCase.testSuite.project.getPropertyValue("topicResource");
		String bootstrapServers =testRunner.testCase.testSuite.project.getPropertyValue("bootstrapServers");
		String table =testRunner.testCase.testSuite.project.getPropertyValue("tableRes");
		long currentDateTime =testRunner.testCase.getPropertyValue("currentDateTime").toLong();
		log.info ("ID ресурса : " + resourceId)

		Properties props = new Properties()
		props.put('zk.connect', '10.2.172.24:2181')
		props.put('bootstrap.servers',bootstrapServers)
		props.put('group.id', UUID.randomUUID().toString())
		props.put("enable.auto.commit", "false")
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
		props.put('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
		props.put('value.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
		def offset =null
		def consumer = new KafkaConsumer(props)

		consumer.subscribe([topic])
		boolean hasMessage = false
		boolean success  = false
		def resourceMessage = null
		long t = System.currentTimeMillis()
		long end = t + 18000;
		while (System.currentTimeMillis()<end && resourceMessage == null)
		{
			log.info ( "while ")
			ConsumerRecords<String, String> records = consumer.poll(18000)
			for (ConsumerRecord<String, String> record : records)
			{
				def rootNode
				try {
					rootNode = new XmlSlurper().parseText(record.value())
				} catch(Exception e) {continue}
				//log.info(rootNode.resource.@id)
				if (rootNode.resource.@id == resourceId && record.timestamp() >= currentDateTime) {
					//log.info("Сообщение: "+record.value());
					log.info("Новое сообщение : "+record.value());
					resourceMessage=rootNode
					hasMessage=true

				}
			}
		}
		consumer.close()
		log.info ( "close ")
		//def valueScheduleChange = value
		if(hasMessage == true && expectMessage == true ){
			def resourceInDB= sql.rows("select ARCHIVED,TITLE,RESOURCE_KIND,PARENT_ID,MEDICAL_ORGANIZATION_ID, MEDICAL_FACILITY_ID,LOCATION_ID from "+table+" where id="+resourceId)
			if(resourceInDB!= null){
				assert resourceMessage.resource.@archived.toBoolean() == resourceInDB[0].ARCHIVED.toBoolean();
				assert resourceMessage.resource.title == resourceInDB[0].TITLE;
				assert resourceMessage.resource.kind == resourceInDB[0].RESOURCE_KIND;
				assert resourceMessage.resource.parentId == resourceInDB[0].PARENT_ID;
				assert resourceMessage.resource.locationId == resourceInDB[0].LOCATION_ID;
				assert resourceMessage.resource.medicalOrganizationId == resourceInDB[0].MEDICAL_ORGANIZATION_ID;
				assert resourceMessage.resource.medicalFacilityId == resourceInDB[0].MEDICAL_FACILITY_ID;
				log.info("Собщение по ресурсу "+resourceId+" отправленно корректно.");
				success  = true
			}
			else{
				testRunner.fail("Не найден ресурс с id ="+resourceId)
			}
		}
		else if (hasMessage == false && expectMessage == true) {
			testRunner.fail("Не найдено сообщение по ресурсу с id ="+resourceId)
		}
		else if (hasMessage == true && expectMessage == false) {
			testRunner.fail("Найдено сообщение по ресурсу с id ="+resourceId)
		}
		else {
			success  = true
		}
	};

	//возвращает максимальное значение оффсет для переданного ресурса
	def getMaxOffsetForResourceMessages( long resourceId){
		String topic =testRunner.testCase.testSuite.project.getPropertyValue("topicResource");
		String bootstrapServers =testRunner.testCase.testSuite.project.getPropertyValue("bootstrapServers");
		log.info ("ID ресурса : " + resourceId)

		Properties props = new Properties()
		props.put('zk.connect', '10.2.172.24:2181')
		props.put('bootstrap.servers',bootstrapServers)
		props.put('group.id', UUID.randomUUID().toString())
		props.put("enable.auto.commit", "false")
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
		props.put('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
		props.put('value.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
		def offset =null
		def value =null
		def consumer = new KafkaConsumer(props)

		consumer.subscribe([topic])

		def resourceMessage = null
		Integer maxOffset= null
		ArrayList offsets = []
		long t = System.currentTimeMillis()
		long end = t + 18000;
		while (System.currentTimeMillis()<end)
		{
			log.info ( "while ")
			ConsumerRecords<String, String> records = consumer.poll(18000)
			for (ConsumerRecord<String, String> record : records)
			{
				def rootNode
				try {
					rootNode = new XmlSlurper().parseText(record.value())
				} catch(Exception e) {continue}
				//log.info(rootNode.resource.@id)
				if (rootNode.resource.@id == resourceId && maxOffset<record.offset()) {
					offsets << record.offset()
					log.info(offsets)

				}
			}
		}
		consumer.close()
		log.info ( "close . and max of offsets() is "+offsets.max())
		return offsets.max()
	};

	//возвращает максимальное значение оффсет для переданного маршрута
	def getMaxOffsetForRouteMessages( long routeId){
		String topic =testRunner.testCase.testSuite.project.getPropertyValue("topicRoute");
		String bootstrapServers =testRunner.testCase.testSuite.project.getPropertyValue("bootstrapServers");
		log.info ("ID ресурса : " + routeId)

		Properties props = new Properties()
		props.put('zk.connect', '10.2.172.24:2181')
		props.put('bootstrap.servers',bootstrapServers)
		props.put('group.id', UUID.randomUUID().toString())
		props.put("enable.auto.commit", "false")
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
		props.put('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
		props.put('value.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
		def offset =null
		def value =null
		def consumer = new KafkaConsumer(props)

		consumer.subscribe([topic])

		def resourceMessage = null
		Integer maxOffset= null
		ArrayList offsets = []
		long t = System.currentTimeMillis()
		long end = t + 18000;
		while (System.currentTimeMillis()<end)
		{
			log.info ( "while ")
			ConsumerRecords<String, String> records = consumer.poll(18000)
			for (ConsumerRecord<String, String> record : records)
			{
				def rootNode
				try {
					rootNode = new JsonSlurper().parseText(record.value())
				} catch(Exception e) {continue}
				//log.info(rootNode.resource.@id)
				if (rootNode.idRir2 == routeId && maxOffset<record.offset()) {
					offsets << record.offset()
					log.info(offsets)

				}
			}
		}
		consumer.close()
		log.info ( "close . and max of offsets() is "+offsets.max())
		return offsets.max()
	};

	//Проверяет наличие корректного типа ресурса указанного вида, если нет - создает
	def checkExistenceResourceType (String resourceType, int index=1 ){
		String functionName=mainScripts.getCheckNullFunctionName()
		String propertyName1
		String propertyName2
		String result1
		String result2
		String CreatedByTest1="0"
		String CreatedByTest2="0"
		String tableResType=testRunner.testCase.testSuite.project.getPropertyValue("tableResType")
		if(resourceType=="SPECIAL_AVAILABLE_RESOURCE") {
			//log.info("СДР")
			propertyName1="resTypeSarId"
			propertyName2="epId"
			String tableEP=testRunner.testCase.testSuite.project.getPropertyValue("tableEP")
			String query = "SELECT rt.ID as RESOURCE_TYPE_ID, ep.id as EQUIPMENT_PROFILE_ID FROM "+tableResType+" rt JOIN "+tableEP+"  ep ON ep.RESOURCE_TYPE_ID =rt.ID WHERE rt.RESOURCE_KIND ='SPECIAL_AVAILABLE_RESOURCE' AND rt.ARCHIVED =0 AND ep.ARCHIVED =false"
			//log.info(query)
			//log.info(row)
			def result =sql.rows(query)
			def rows = result.size()
			//log.info(result)
			if (rows == 0){
				log.info("В таблице "+tableEP+"  и "+tableResType+"  нет подходящих записей для корректного подбора типа ресурса, создаю новую запись.")
				def getSarResTypeId = sql.firstRow("SELECT ID FROM "+tableResType+" WHERE ARCHIVED =false and RESOURCE_KIND ='SPECIAL_AVAILABLE_RESOURCE' ")
				//def equipmentProfileId = sql.firstRow("SELECT ID FROM "+tableEP+" WHERE ARCHIVED =0").ID
				//long equipmentProfileId=sql.rows("SELECT MIN(ID)- 1 AS ID FROM "+tableEP)[0].ID
				def sarResTypeId
				if (getSarResTypeId != null){
					sarResTypeId = getSarResTypeId.ID
				}
				else{
					sarResTypeId=sql.rows("SELECT "+functionName+"(min(id)-1,-1) AS ID FROM "+tableResType)[0].ID
					sql.execute("insert into  "+tableResType+" (ID,TITLE,RESOURCE_KIND,ARCHIVED) values( "+sarResTypeId+", 'Тип ресурса для CДР автотест РиР.2','SPECIAL_AVAILABLE_RESOURCE',false)")
					CreatedByTest1="1"
				}
				def equipmentProfileId=sql.rows("SELECT "+functionName+"(min(id)-1,-1) AS ID FROM "+tableEP)[0].ID
				sql.execute("insert into "+tableEP+" (ID, ARCHIVED,RESOURCE_TYPE_ID) values ("+equipmentProfileId+", false,"+sarResTypeId+")")
				result1=sarResTypeId.toString()
				result2=equipmentProfileId.toString()
				CreatedByTest2="1"
			}
			else {
				result1= result[0].RESOURCE_TYPE_ID.toString()
				result2= result[0].EQUIPMENT_PROFILE_ID.toString()
				log.info("Таблицы "+tableResType+" и "+tableEP+"  имеют подходящие записи.")
			}
		} else if(resourceType=="AVAILABLE_RESOURCE") {
			log.info("ДР")
			String tablePosNom=testRunner.testCase.testSuite.project.getPropertyValue("tablePosNom")
			propertyName1="resTypeArId"
			propertyName2="posNomId"
			String query1 = "SELECT rt.ID as RESOURCE_TYPE_ID, pn.id as POSITION_NOM_ID  FROM "+tableResType+"  rt "+
					"JOIN "+tablePosNom+" pn ON pn.RESOURCE_TYPE_ID =rt.id WHERE rt.ARCHIVED =false AND rt.RESOURCE_KIND ='AVAILABLE_RESOURCE'"
			//log.info(query)
			def result =sql.rows(query1)
			def rows = result.size()
			//log.info(result)
			if (rows == 0){
				log.info("В таблицах "+tablePosNom+"  и "+tableResType+"  нет подходящих записей для корректного подбора типа ресурса, создаю новую запись.")
				def getArResTypeId = sql.firstRow("SELECT ID FROM "+tableResType+" WHERE ARCHIVED =false and RESOURCE_KIND ='AVAILABLE_RESOURCE' ")
				def arResTypeId
				if (getArResTypeId != null){
					arResTypeId = getArResTypeId.ID
				}
				else{
					arResTypeId=sql.rows("SELECT "+functionName+"(min(id)-1,1) AS ID FROM "+tableResType)[0].ID
					sql.execute("insert into  "+tableResType+" (ID,TITLE,RESOURCE_KIND,ARCHIVED) values( "+arResTypeId+", 'Тип ресурса для ДР автотест РиР.2','AVAILABLE_RESOURCE',false)")
					CreatedByTest1="1"
				}
				def posNomId=sql.rows("SELECT "+functionName+"(min(id)-1,1) AS ID FROM "+tablePosNom)[0].ID
				sql.execute("insert into  "+tablePosNom+" (ID,TITLE,RESOURCE_TYPE_ID) values( "+posNomId+",'Должность автотест РиР.2', "+arResTypeId+")")
				result1=arResTypeId.toString()
				result2=posNomId.toString()
				CreatedByTest1="1"
			}
			else {
				result1= result[0].RESOURCE_TYPE_ID.toString()
				result2= result[0].POSITION_NOM_ID.toString()
				log.info("Таблицы "+tableResType+" и "+tablePosNom+" имеют подходящие записи.")
			}
		} else {
			testRunner.fail("Передан некорректный тип ресурса")
		}

		testRunner.testCase.setPropertyValue(propertyName1+index, result1)
		testRunner.testCase.setPropertyValue(propertyName2+index, result2)
		testRunner.testCase.setPropertyValue(propertyName1+"CreatedByTest"+index, CreatedByTest1)
		testRunner.testCase.setPropertyValue(propertyName2+"CreatedByTest"+index, CreatedByTest2)
		log.info("Записан  параметр "+propertyName1+ " = "+result1)
		log.info("Записан  параметр "+propertyName2+ " = "+result2)


	};

	//найти или создать номенклатуру должности и тип ресурса
	def findOrCreateResTypeFopPosNom(String posNomPropertyName, String resTypePropertyName, String resType, Boolean resTypeArchived){
		String functionName=mainScripts.getCheckNullFunctionName()
		String CreatedByTest1="0"
		String CreatedByTest2="0"
		String tableResType=testRunner.testCase.testSuite.project.getPropertyValue("tableResType")
		String tablePosNom=testRunner.testCase.testSuite.project.getPropertyValue("tablePosNom")
		List arList=["др","ДР","Др","AR","Ar","ar","AVAILABLE_RESOURCE"]
		List sarList=["сдр","СДР","Сдр"," SAR","Sar","sar","SPECIAL_AVAILABLE_RESOURCE"]
		if (resType in arList){
			resType='AVAILABLE_RESOURCE'
		}
		else if (resType in sarList){
			resType='SPECIAL_AVAILABLE_RESOURCE'
		}
		else {
			testRunner.fail("Передан некорректный тип ресурса. Допустимые типы: др,ДР,Др,AR,Ar,AVAILABLE_RESOURCE,сдр,СДР,Сдр,SAR,Sar,SPECIAL_AVAILABLE_RESOURCE")
		}
		String query1 = "SELECT rt.ID as RESOURCE_TYPE_ID, pn.id as POSITION_NOM_ID  FROM "+tableResType+"  rt "+
				"JOIN "+tablePosNom+" pn ON pn.RESOURCE_TYPE_ID =rt.id WHERE rt.ARCHIVED ="+resTypeArchived+
				" AND rt.RESOURCE_KIND ='"+resType+"'"
		//log.info(query)
		def result =sql.firstRow(query1)
		String result1
		String result2
		if (result == null){
			log.info("В таблицах "+tablePosNom+"  и "+tableResType+"  нет подходящих записей для корректного подбора типа ресурса, создаю новую запись.")
			def getArResTypeId = sql.firstRow("SELECT ID FROM "+tableResType+" WHERE ARCHIVED ="+resTypeArchived+
					" and RESOURCE_KIND ='"+resType+"' ")
			def arResTypeId
			if (getArResTypeId != null){
				arResTypeId = getArResTypeId.ID
			}
			else{
				arResTypeId=sql.firstRow("SELECT "+functionName+"(max(id)+1,1) AS ID FROM "+tableResType).ID
				sql.execute("insert into  "+tableResType+" (ID,TITLE,RESOURCE_KIND,ARCHIVED) values( "+arResTypeId+", 'Тип ресурса для pos_nom автотест РиР.2','"+resType+"',"+resTypeArchived+")")
				CreatedByTest1="1"
			}
			def posNomId=sql.rows("SELECT "+functionName+"(min(id)+1,1) AS ID FROM "+tablePosNom)[0].ID
			sql.execute("insert into  "+tablePosNom+" (ID,TITLE,RESOURCE_TYPE_ID) values( "+posNomId+",'Должность автотест РиР.2', "+arResTypeId+")")
			result1=arResTypeId.toString()
			result2=posNomId.toString()
			CreatedByTest1="1"
		}
		else {
			result1= result.RESOURCE_TYPE_ID.toString()
			result2= result.POSITION_NOM_ID.toString()
			log.info("Таблицы "+tableResType+" и "+tablePosNom+" имеют подходящие записи.")
		}
		testRunner.testCase.setPropertyValue(resTypePropertyName, result1)
		testRunner.testCase.setPropertyValue(posNomPropertyName, result2)
		testRunner.testCase.setPropertyValue(resTypePropertyName+"CreatedByTest", CreatedByTest1)
		testRunner.testCase.setPropertyValue(posNomPropertyName+"CreatedByTest", CreatedByTest2)
		log.info("Записан  параметр "+resTypePropertyName+ " = "+result1)
		log.info("Записан  параметр "+posNomPropertyName+ " = "+result2)
	};

	def getPosNomAndNonArchivedArResType(){
		findOrCreateResTypeFopPosNom("posNomId","resTypeAr","ar",false)
	};

	def getPosNomAndArchivedArResType(){
		findOrCreateResTypeFopPosNom("posNomIdArchivedRT","archivedResTypeAr","ar",true)
	};

	def getPosNomAndSarResType(){
		findOrCreateResTypeFopPosNom("posNomIdSarRT","resTypeSr","sar",false)
	};

	//очищает созданные в бд записи типа ресурса и номенклатуры должности и очищает проперти
	def clearPosNomAndResTypes(){
		if(testRunner.getTestCase().hasProperty("posNomId")){
			log.info "есть проперти posNomId "
			mainScripts.deleteCreatedByTestInTestCase("tablePosNom","posNomId")
			mainScripts.deleteCreatedByTestInTestCase("tableResType","resTypeAr")
		}
		if(testRunner.getTestCase().hasProperty("posNomIdArchivedRT")){
			log.info "есть проперти posNomIdArchivedRT "
			mainScripts.deleteCreatedByTestInTestCase("tablePosNom","posNomIdArchivedRT")
			mainScripts.deleteCreatedByTestInTestCase("tableResType","archivedResTypeAr")
		}
		if(testRunner.getTestCase().hasProperty("posNomIdSarRT")){
			log.info "есть проперти posNomId "
			mainScripts.deleteCreatedByTestInTestCase("tablePosNom","posNomIdSarRT")
			mainScripts.deleteCreatedByTestInTestCase("tableResType","resTypeSr")
		}
	};

	//Создает несколько структур Медицинской огранизации: МО, переданное количество Адресных объектов, Мед учреждений и локаций. Записывает всё в проперти
	def createStructureOfMOWithMOcounter(int counterMO=1,int counterMF=1){
		String functionName=mainScripts.getCheckNullFunctionName()
		String tableMO = testRunner.testCase.testSuite.project.getPropertyValue("tableMO");
		String tableMF = testRunner.testCase.testSuite.project.getPropertyValue("tableMF");
		long minId;
		log.info("SELECT "+functionName+"(min(ID)-1,-1) AS ID FROM "+tableMO);
		long minIdMO=sql.rows("SELECT "+functionName+"(min(ID)-1,-1) AS ID FROM "+tableMO)[0].ID;
		long minIdMF=sql.rows("SELECT "+functionName+"(min(ID)-1,-1) AS ID FROM "+tableMF)[0].ID;
		minId = minIdMO > minIdMF ? minIdMF : minIdMO;
		mainScripts.findOrCreateEntitiesInDBwithConditionAndCount("tableSpec", "ID,ARCHIVED,TITLE", "-2,0,'Специализация автотест РиР.2'", "specId", 1, "where archived = 0");
		String specId = testRunner.testCase.getPropertyValue("specId1");
		for(int j=1; j<=counterMO; j++) {
			mainScripts.createEntitiesInDBwithIndex("tableMO", "ID,ARCHIVED,NAME,NAME_FULL,OGRN,CLOSED,CREATED", "0,'МО автотест РиР.2','МО автотест РиР.2',1111,NULL,TIMESTAMP '1970-01-01 00:00:00.000000'", "moId", j, minId);
			String isHead = '1';
			String headDescription = "Головное ";

			long mfId = minId
			for (int i = 1; i <= counterMF; i++) {
				if (i > 1) {
					isHead = '0';
					headDescription = ""
				}
				mainScripts.createEntitiesInDBwithIndex("tableAO", "ID, ARCHIVE, GLOBAL_ID, ADDRESS_STRING, UPDATED", "0, 6203000, 'Адресный объект автоест РиР.2', CURRENT_TIMESTAMP", "aoId"+j+"_", i, null);
				String aoId = testRunner.testCase.getPropertyValue("aoId"+j+"_"+ i);
				mainScripts.createEntitiesInDBwithIndex("tableMF", "ID, ARCHIVED, MEDICAL_ORGANIZATION_ID, NAME_SHORT, NAME, NAME_FULL, IS_HEAD, ADDRESS_OBJECT_ID, CLOSED, CREATED", "'0'," + minId + ", '" + headDescription + "МУ автотест РиР.2','" + headDescription + "МУ автотест РиР.2','" + headDescription + "МУ автотест РиР.2','" + isHead + "'," + aoId + ", NULL,CURRENT_TIMESTAMP ", "mfId"+j+"_", i, mfId);
				mainScripts.createEntitiesInDBwithIndex("tableLoc", "ID, ARCHIVED, ROOM_TITLE, STAGE, MEDICAL_FACILITY_ID, SPECIALIZATION_ID", "'0','Локация автотест РиР.2',1," + mfId + "," + specId, "locId"+j+"_", i);
				mfId = mfId - 1
			}
			minId=mfId
		}
	};

	//Очищает созданные методом createStructureOfMOWithMOcounter записи в бд и проперти
	def clearStructureOfMOWithMOcounter(int counterMO=1,int counterMF=1) {
		for(int j=1; j<=counterMO; j++) {
			for (int i = 1; i <= counterMF; i++) {
				mainScripts.clearCreatedByTestInTestCaseWithIndex("tableLoc", "locId"+j+"_", i)
			};
			for (int i = 1; i <= counterMF; i++) {
				String tableMFWH = testRunner.testCase.testSuite.project.getPropertyValue("tableMFWH");
				String mfId = testRunner.testCase.getPropertyValue("mfId"+j+"_" + i);
				String deleteMFWH = ("delete from " + tableMFWH + " where MEDICAL_FACILITY_ID=" + mfId);
				log.info(deleteMFWH);
				sql.execute(deleteMFWH)
			};
			for (int i = 1; i <= counterMF; i++) {
				mainScripts.clearCreatedByTestInTestCaseWithIndex("tableMF", "mfId"+j+"_", i)
			};
			for (int i = 1; i <= counterMF; i++) {
				mainScripts.clearCreatedByTestInTestCaseWithIndex("tableAO", "aoId"+j+"_", i)
			};
			mainScripts.clearCreatedByTestInTestCaseWithIndex("tableMO", "moId", j)
		}
	};

	def waitResourceSyncStatusChange(Long parentId , String rKind){
		ResourceKinds resourceKind = ResourceKinds.valueOf(rKind)
		String tableRes =testRunner.testCase.testSuite.project.getPropertyValue("tableRes");
		String getResource="select * from "+tableRes+" where RESOURCE_KIND ='"+resourceKind.toString()+"' AND PARENT_ID="+parentId;
		for (int j = 0; j < 23; j++) {
			//log.info(querySar)
			def resource = sql.firstRow(getResource);
			// log.info(rowsSar)
			if (resource.SYNC_STATUS=='IN_PROGRESS') {
				if (j<22){
					log.info("Идет синхронизация ресруса. Подождём...");
					sleep(6000)
				}
				else {
					log.info("Синхронизация ресурса "+resource.ID+" не завершена, количество попыток исчерпано.")
					return resource.SYNC_STATUS
				}
			}
			else {
				log.info("Синхрнизация ресурса завершена.")
				return resource.SYNC_STATUS
			}
		}
	};

	def returnResourceScheduleByResourceIdAndScheduleId( long resId, Integer scheduleId){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableResSchedule");
		String getEntity = ("SELECT * FROM "+table+"  WHERE Resource_ID="+resId+" and schedule_id="+scheduleId);
		//log.info(getEntity)
		def entity= sql.rows(getEntity);
		log.info(entity);
		if (entity == [] ){
			log.info("В таблице "+table+" не найдена запись с id ресурса "+resId);
			entity= null
		}
		else {
			log.info("В таблице "+table+" найдена запись с id ресурса "+resId)
			//log.info(lastItem)
		}
		return entity
	};

	//возвращает последнюю запись в журнале по условию
	def returnJournalLastEntry( String condition){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableJ");
		String getEntity = ("SELECT * FROM "+table+"  WHERE "+condition+" order by EVENT_DATE_TIME desc");
		//log.info(getEntity)
		def entity= sql.firstRow(getEntity);
		log.info(entity);
		if (entity == [] ){
			testRunner.fail("В таблице "+table+" не найдена запись с условием "+condition);
			entity= null
		}
		else {
			log.info("В таблице "+table+" найдена запись с условием "+condition)
			//log.info(lastItem)
		}
		return entity
	};

	//возвращает информацию по открытым ошибкам переданного ресурса
	def returnOpenResourceSycnErrors(long resourceId){
		String query="SELECT DISTINCT rsem.code, rsemp.\"KEY\" , rsemp.VALUE FROM RESOURCE_SYNC_ERROR rse"+
				" JOIN RESOURCE_SYNC_ERROR_MSG rsem ON RSE.ID=RSEM.RESOURCE_SYNC_ERROR_ID "+
				" JOIN RESOURCE_SYNC_ERROR_MSG_PR rsemp ON rsemp.RESOURCE_SYNC_ERROR_MSG_ID=rsem.id "+
				" WHERE rse.STATUS ='OPEN' AND rse.RESOURCE_ID ="+resourceId+" order by rsemp.\"KEY\""
		def errors = sql.rows(query)
		return errors
	};



	def getRouteMessage( long routeId ){
		String topic =testRunner.testCase.testSuite.project.getPropertyValue("topicRoute");
		String bootstrapServers =testRunner.testCase.testSuite.project.getPropertyValue("bootstrapServers");
		long currentDateTime =testRunner.testCase.getPropertyValue("currentDateTime").toLong();
		log.info ("ID маршрута : " + routeId)

		Properties props = new Properties()
		props.put('zk.connect', '10.2.172.24:2181')
		props.put('bootstrap.servers',bootstrapServers)
		props.put('group.id', UUID.randomUUID().toString())
		props.put("enable.auto.commit", "false")
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
		props.put('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
		props.put('value.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
		def offset =null
		def consumer = new KafkaConsumer(props)

		consumer.subscribe([topic])
		def routeMessage = null
		long t = System.currentTimeMillis()
		ArrayList timestamps = []
		long end = t + 18000;
		while (System.currentTimeMillis()<end && routeMessage == null)
		{
			log.info ( "while ")
			ConsumerRecords<String, String> records = consumer.poll(18000)
			for (ConsumerRecord<String, String> record : records)
			{
				def rootNode
				try {
					rootNode = new JsonSlurper().parseText(record.value())
				} catch(Exception e) {continue}
				//log.info(rootNode.resource.@id)
				if (rootNode.idRir2 == routeId && record.timestamp() >= currentDateTime) {
					//log.info("Сообщение: "+record.value());
					log.info("Новое сообщение : " + record.value() + " время "+ new Timestamp(record.timestamp()));
					routeMessage=record.value()

				}
			}
		}
		consumer.close()
		log.info ( "close ")
		return routeMessage
	};

	//получает переданное количество маппингов должостей для маршрутов. если недостаточно- досоздает
	def getPositionMappingCodes(int countPosCodes){
		String tableRPCM=testRunner.testCase.testSuite.project.getPropertyValue("tableRPCM")
		String getCodes="SELECT SUPP_POSITION_CODE_CODE, NSI_POSITION_CODE_CODE FROM "+tableRPCM+
				" ORDER BY NSI_POSITION_CODE_CODE, SUPP_POSITION_CODE_CODE "
		log.info getCodes
		def posCodes=sql.rows(getCodes);
		int i=0
		int index=1
		String routeNSIcode
		String routeSUPPcode
		String createdByTest="0"
		while (i<countPosCodes){
			if (posCodes[i]!=null){
				routeNSIcode=posCodes[i].NSI_POSITION_CODE_CODE
				routeSUPPcode=sql.firstRow("SELECT SUPP_POSITION_CODE_CODE as CODE from "+tableRPCM+" where NSI_POSITION_CODE_CODE='"+routeNSIcode+"'").CODE

			}
			else {
				routeNSIcode="testNsiCode"+i.toString()
				routeSUPPcode="routeSUPPcode"+i.toString()
				sql.execute("INSERT INTO "+tableRPCM+" (ID, NSI_POSITION_CODE_CODE, SUPP_POSITION_CODE_CODE) VALUES((select max(id)+1 from "+tableRPCM+"),"+routeNSIcode+","+routeSUPPcode+")")
				createdByTest="0"
			}
			mainScripts.createPropertyWithCreatedByTCparameter("routeNSIcode",routeNSIcode,createdByTest, index)
			mainScripts.createPropertyWithCreatedByTCparameter("routeSUPPcode",routeSUPPcode,createdByTest, index)
			i=i+1
			index=index+1
		}
	};

	//Удаляет записи созданные getPositionMappingCodes и чистит проперти
	def clearPositionMappingCodes(int countPosCodes){
		String tableRPCM=testRunner.testCase.testSuite.project.getPropertyValue("tableRPCM")
		String routeNSIcode
		int index=1
		while (index<=countPosCodes){
			routeNSIcode=testRunner.testCase.getPropertyValue("routeNSIcode"+index)
			def createdByTest=testRunner.testCase.getPropertyValue("routeNSIcodeCreatedByTest"+index).toBoolean()
			if (createdByTest==true){
				sql.execute("delete  from "+tableRPCM+" where NSI_POSITION_CODE_CODE='"+routeNSIcode+"routeNSIcode'")
			}
			testRunner.testCase.removeProperty("routeNSIcode"+index)
			testRunner.testCase.removeProperty("routeNSIcodeCreatedByTest"+index)
			testRunner.testCase.removeProperty("routeSUPPcode"+index)
			testRunner.testCase.removeProperty("routeSUPPcodeCreatedByTest"+index)
			index=1+index
		}
	};

	// возвращает последнюю запись журнала маршрута по id маршрута
	def returnRouteJournalLastLastEntry( long routeId){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteJ");
		String getEntity = ("SELECT * FROM "+table+"  WHERE ROUTE_ID="+routeId+" order by EVENT_DATE  desc");
		//log.info(getEntity)
		def entity= sql.firstRow(getEntity);
		log.info(entity);
		if (entity == null ){
			log.info("В таблице "+table+" не найдена запись с условием routeId="+routeId);
		}
		else {
			log.info("В таблице "+table+" найдена запись с условием routeId="+routeId)
		}
		return entity
	};

	//Получает первую и последнюю дату изменения маршрута
	def getFirstAndLastRouteEditDate(long routeId){
		String query="SELECT EVENT_DATE AS LAST_EVENT_DATE, rj2.CREATE_DATE FROM ROUTE_JOURNAL rj "+
				"JOIN (SELECT EVENT_DATE AS CREATE_DATE, ROUTE_ID FROM ROUTE_JOURNAL "+
				"WHERE EVENT_TYPE='CREATE' AND ROUTE_ID ="+routeId+") rj2 ON rj.ROUTE_ID = rj2.ROUTE_ID "+
		"ORDER BY LAST_EVENT_DATE DESC"
		def editeDates=sql.firstRow(query)
		log.info editeDates
		return editeDates
	};

	//сравнивает определенные параметры маршрута с количеством и значениями параметров в пропертях и в бд
	def checkRouteEntries(String tableName, String column, String propertyName, long routeId, int counter =1){
		String table = testRunner.testCase.testSuite.project.getPropertyValue(tableName);
		String query ="SELECT *  FROM  "+table+
				" WHERE ROUTE_ID="+routeId
		def rows=sql.rows(query)
		int countRows=rows.size()
		ArrayList propertiesArray = []
		ArrayList dbEntriesArray = []
		assert countRows ==counter
		int index=1
		//log.info rows[0].get(column)
		for(int i=0;i<counter; i++){
			def propert =testRunner.testCase.getPropertyValue(propertyName+index)
			propertiesArray << propert
			dbEntriesArray << rows[i].get(column).toString()
			index=index+1
		}
		assert propertiesArray.sort() == dbEntriesArray.sort()
	};

	def countNumberByIndices(int first, int last){
		int counter=Math.abs(last-first)+1
		return 	counter
	};

	def checkRouteEntriesAmount(String table, long routeId, int counter){
		String query ="SELECT *  FROM  "+table+	" WHERE ROUTE_ID="+routeId
		def rows=sql.rows(query)
		int countRows=rows.size()
		assert countRows == counter
		log.info "В таблице "+table+" "+countRows+" записей"
	};

	def checkRouteRecMF(long routeId,int firstIndexMO=1, int lastIndexMO=1, int firstIndex=1, int lastIndex=1){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteRecMF");
		int counterMF=countNumberByIndices(firstIndex,lastIndex);
		int counterMO=countNumberByIndices(firstIndexMO,lastIndexMO);
		int counter=counterMF*counterMO;
		checkRouteEntriesAmount(table,routeId,counter)
		for (int m=firstIndexMO; m<=lastIndexMO; m++){
			for (int i=firstIndex; i<=counter; i++){
				def mfId=testRunner.testCase.getPropertyValue("recMfId"+m+"_"+i)
				def moId = testRunner.testCase.getPropertyValue("recMoId"+m)
				String query ="SELECT *  FROM  "+table+	" WHERE ROUTE_ID="+routeId+" AND MEDICAL_FACILITY_ID="+mfId
				def rows=sql.rows(query)
				int countRows=rows.size()
				assert countRows== 1
				assert moId== rows[0].RECEIVE_MO_ID.toString()
			}
		}
	};

	def checkRouteDirMF(long routeId,int firstIndexMO=1, int lastIndexMO=1, int firstIndex=1, int lastIndex=1){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteDirMF");
		int counterMF=countNumberByIndices(firstIndex,lastIndex);
		int counterMO=countNumberByIndices(firstIndexMO,lastIndexMO);
		int counter=counterMF*counterMO
		checkRouteEntriesAmount(table,routeId,counter)
		for (int m=firstIndexMO; m<=lastIndexMO; m++){
			for (int i=firstIndex; i<=counter; i++){
				def mfId=testRunner.testCase.getPropertyValue("dirMfId"+m+"_"+i)
				def moId = testRunner.testCase.getPropertyValue("dirMoId"+m)
				String query ="SELECT *  FROM  "+table+	" WHERE ROUTE_ID="+routeId+" AND MEDICAL_FACILITY_ID="+mfId
				def rows=sql.rows(query)
				int countRows=rows.size()
				assert countRows== 1
				assert moId== rows[0].DIRECT_MO_ID.toString()
			}
		}
	};

	def checkRouteDirMO(long routeId, int firstIndex=1, int lastIndex=1){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteDirMO");
		int counter=countNumberByIndices(firstIndex,lastIndex);
		checkRouteEntriesAmount(table,routeId,counter)
		for (int i=firstIndex; i<=counter; i++){
			def moId = testRunner.testCase.getPropertyValue("dirMoId"+i)
			String query ="SELECT *  FROM  "+table+	" WHERE ROUTE_ID="+routeId+" AND MEDICAL_ORGANIZATION_ID="+moId
			def rows=sql.rows(query)
			int countRows=rows.size()
			assert countRows== 1
		}
	};

	def checkRouteRecMO(long routeId, int firstIndex=1, int lastIndex=1){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteRecMO");
		int counter=countNumberByIndices(firstIndex,lastIndex);
		checkRouteEntriesAmount(table,routeId,counter)
		for (int i=firstIndex; i<=counter; i++){
			def moId = testRunner.testCase.getPropertyValue("recMoId"+i)
			String query ="SELECT *  FROM  "+table+	" WHERE ROUTE_ID="+routeId+" AND MEDICAL_ORGANIZATION_ID="+moId
			def rows=sql.rows(query)
			int countRows=rows.size()
			assert countRows== 1
		}
	};

	def checkRouteTreatments(long routeId, int firstIndex=1, int lastIndex=1){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteT");
		int counter=countNumberByIndices(firstIndex,lastIndex);
		checkRouteEntriesAmount(table,routeId,counter)
		for (int i=firstIndex; i<=counter; i++){
			def tId = testRunner.testCase.getPropertyValue("tId"+i)
			def tCode = testRunner.testCase.getPropertyValue("tCode"+i)
			def tTitle = testRunner.testCase.getPropertyValue("tTitle"+i)
			def tcCode = testRunner.testCase.getPropertyValue("tcCode"+i)
			String query ="SELECT *  FROM  "+table+	" WHERE ROUTE_ID="+routeId+" AND TREATMENT_ID="+tId
			def rows=sql.rows(query)
			int countRows=rows.size()
			assert countRows== 1
			assert tCode== rows[0].CODE.toString()
			assert tTitle== rows[0].TITLE.toString()
			assert tcCode== rows[0].TREATMENT_CATEGORY_CODE.toString()
		}
	};

	def checkRouteDiagnosis(long routeId, int firstIndex=1, int lastIndex=1){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteDiag");
		int counter=countNumberByIndices(firstIndex,lastIndex);
		checkRouteEntriesAmount(table,routeId,counter)
		for (int i=firstIndex; i<=counter; i++){
			def diagnosId = testRunner.testCase.getPropertyValue("diagnosisId"+i)
			def code = testRunner.testCase.getPropertyValue("diagnosisCode"+i)
			def title = testRunner.testCase.getPropertyValue("diagnosisTitle"+i)
			String query ="SELECT *  FROM  "+table+	" WHERE ROUTE_ID="+routeId+" AND DIAGNOS_ID="+diagnosId
			def rows=sql.rows(query)
			int countRows=rows.size()
			assert countRows== 1
			assert code== rows[0].CODE.toString()
			assert title== rows[0].TITLE.toString()
		}
	};

	def checkRoutePositions(long routeId, int firstIndex=1, int lastIndex=1){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteDirPos");
		int counter=countNumberByIndices(firstIndex,lastIndex)
		checkRouteEntriesAmount(table,routeId,counter)
		for (int i=firstIndex; i<=counter; i++){
			def pnId = testRunner.testCase.getPropertyValue("pnId"+i)
			def code = testRunner.testCase.getPropertyValue("routeNSIcode"+i)
			def title = testRunner.testCase.getPropertyValue("directMedPositionTitle"+i)
			String query ="SELECT *  FROM  "+table+	" WHERE ROUTE_ID="+routeId+" AND DOC_SPECIALITY_ID="+pnId
			def rows=sql.rows(query)
			int countRows=rows.size()
			assert countRows== 1
			assert code== rows[0].CODE.toString()
			assert title== rows[0].TITLE.toString()
		}
	};

	def checkRoute(long routeId, int active=1, int docIndex=1, Integer suppId= null ){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableRoute")
		def document = testRunner.testCase.getPropertyValue("document"+docIndex)
		def ownerMedicalOrganizationId = testRunner.testCase.getPropertyValue("ownerMedicalOrganizationId1")
		String query ="SELECT *  FROM  "+table+	" WHERE ID="+routeId
		def rows=sql.rows(query)
		int countRows=rows.size()
		assert countRows== 1
		assert active== rows[0].ACTIVE
		assert routeId.toString() == rows[0]."NUMBER"
		assert document== rows[0].DOCUMENT
		assert ownerMedicalOrganizationId== rows[0].AUTHOR_MO_ID.toString()
		assert suppId== rows[0].SUPP_ID
	};

	def clearRoutesEntry(long routeId, String propertyName="routeId"){
		String tableRoute = testRunner.testCase.testSuite.project.getPropertyValue("tableRoute")
		String tableRouteDirPos = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteDirPos")
		String tableRouteDiag = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteDiag")
		String tableRouteT = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteT")
		String tableRouteRecMO = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteRecMO")
		String tableRouteDirMO = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteDirMO")
		String tableRouteDirMF = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteDirMF")
		String tableRouteRecMF = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteRecMF")
		String tableRouteJ = testRunner.testCase.testSuite.project.getPropertyValue("tableRouteJ")
		// Удаляем запись в DIRECT_MEDICAL_ORGANIZATIONS
		sql.execute ("DELETE  from  "+tableRouteDirPos+" WHERE ROUTE_ID ="+routeId)
		sql.execute ("DELETE  from  "+tableRouteDiag+" WHERE ROUTE_ID ="+routeId)
		sql.execute ("DELETE  from  "+tableRouteT+" WHERE ROUTE_ID ="+routeId)
		sql.execute ("DELETE  from  "+tableRouteRecMO+" WHERE ROUTE_ID ="+routeId)
		sql.execute ("DELETE  from  "+tableRouteDirMO+" WHERE ROUTE_ID ="+routeId)
		sql.execute ("DELETE  from  "+tableRouteDirMF+" WHERE ROUTE_ID ="+routeId)
		sql.execute ("DELETE  from  "+tableRouteRecMF+" WHERE ROUTE_ID ="+routeId)
		sql.execute ("DELETE  from  "+tableRouteJ+" WHERE ROUTE_ID ="+routeId)
		sql.execute ("DELETE  from  "+tableRoute+" WHERE ID ="+routeId)
		testRunner.testCase.setPropertyValue(propertyName, "")
	};

	// создать запись о блокировке сущности
	def createSystemLockEntry(String entityType, long id){
		String functionName=mainScripts.getCheckNullFunctionName()
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableSysLock");
		sql.execute("INSERT INTO "+table+" VALUES ((SELECT "+functionName+"(max(id)+1,1) FROM "+table+"),'"+entityType+"', "+id+", current_timestamp)")
	};

	def returnSystemLockEntry(String entityType, long id){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableSysLock");
		def lock=sql.firstRow("select * from "+table+" where ENTITY_TYPE='"+entityType+"' and  ENTITY_ID="+id)
		int size = lock == null ? 0 : lock.size();
		return size
	};

	def deleteSystemLockEntry(String entityType, long id){
		String table = testRunner.testCase.testSuite.project.getPropertyValue("tableSysLock");
		sql.execute("DELETE FROM "+table+" WHERE ENTITY_TYPE='"+entityType+"' AND ENTITY_ID="+id)
	};

	//Возвращает все записи по системным операциям указанного типа и с ключом
	def getSysopOperation(String operationType, String key){
		Long currentDateTime= testRunner.testCase.getPropertyValue("currentDateTime").toLong()
		Timestamp timestamp = new Timestamp(currentDateTime)
		String query="SELECT * FROM SYSOP WHERE CREATED_AT >= timestamp '"+timestamp+"' AND MODEL LIKE '%"+operationType+"%' AND MODEL LIKE '%"+key+"%' ORDER BY CREATED_AT DESC"
		log.info query
		def sysop = sql.rows(query) != null ? sql.rows(query) : []
		return sysop
	};

	//Возвращает запись системной операции по id
	def getSysopOperationById(String operationId){
		String query="SELECT * FROM SYSOP WHERE ID="+operationId
		//log.info query
		def sysop = sql.rows(query) != null ? sql.rows(query) : []
		return sysop
	};

	// Возвращает ошибки по ассинхронной операции по ее id
	def getSysopErrorsByOperationId(String operationId){
		String query="SELECT sm.\"TYPE\" , sm.code, smp.\"KEY\", smp.VALUE FROM SYSOP_MSG sm \n" +
				"LEFT JOIN SYSOP_MSG_PARAM smp ON smp.SYSOP_MSG_ID=sm.ID \n" +
				"WHERE SYSOP_ID="+operationId+
				" ORDER BY sm.SYSOP_ID "
		//log.info query
		def sysop = sql.rows(query) != null ? sql.rows(query) : []
		return sysop
	};

	//Дожидается завершения асинхронной операции и проверяет ее успешость
	def waitSysopFinish(String operationId, Integer wait=4, boolean success=true){
		String getOperation="SELECT * FROM SYSOP WHERE ID="+operationId
		boolean hasSuccess= false
		boolean isCompleted = false
		for(int i=0; i <=wait; i ++){
			def operation = sql.firstRow(getOperation)
			if (operation == null){
				testRunner.fail("Не найдена асинхронная операция с id = "+operationId );
				return
			}
			hasSuccess =operation.HAS_SUCCEDED.toBoolean()
			isCompleted =operation.IS_COMPLETED.toBoolean()
			if (isCompleted){ break	}
			sleep(5000);
			log.info "Операция пока не завершилась, подождем"
		}
		if (!isCompleted){
			testRunner.fail("Операция не завершена. Количество попыток проверки исчерпано");
			return
		}
		if (hasSuccess==success){log.info "Операция "+operationId+" завершилась с ожидаемой успешностью = "+success }
		else {testRunner.fail("Успешность операции "+operationId+ " отличатся от ожидаемой. Фактический succes= "+hasSuccess+", ожидаемый = "+success )}
	};

	//Удаляет все о системной операции из пропертей и чистит проперти
	def clearSysopById(String operationName){
		String operationId= testRunner.testCase.getPropertyValue(operationName)
		sql.execute("delete from SYSOP_MSG_PARAM where SYSOP_MSG_ID in (select ID from SYSOP_MSG where SYSOP_ID="+operationId+")")
		sql.execute("delete from SYSOP_MSG where SYSOP_ID="+operationId)
		sql.execute("delete from SYSOP where ID="+operationId)
		testRunner.testCase.removeProperty(operationName)
	};

	//удаляет все павила переданного расписания ресурса и ставит ему статус not_defined
	def clearResourceSchedule( int resourceScheduleId){
		//resourceScheduleId=4893801
		String tableResSchedule=testRunner.testCase.testSuite.project.getPropertyValue("tableResSchedule")
		String tableRes=testRunner.testCase.testSuite.project.getPropertyValue("tableRes")
		String query="select RESOURCE_KIND from "+tableRes+" res "+
				"join "+tableResSchedule+" rs on rs.RESOURCE_ID=res.ID WHERE rs.ID="+resourceScheduleId
		log.info query
		String type= sql.firstRow(query).RESOURCE_KIND
		log.info sql.firstRow(query)
		if (type == "AVAILABLE_RESOURCE" ){
			type ="AR"
		}
		else if (type == "SPECIAL_AVAILABLE_RESOURCE"){
			type ="SAR"
		}
		else {
			testRunner.fail("Не смогли определить тип ресурса, полученное значение типа ресурса  "+type)
			return
		}
		String ruleIds="SELECT ID FROM "+type+"_SCHRULE WHERE RESOURCE_SCHEDULE_ID="+resourceScheduleId
		log.info ruleIds
		Integer count = sql.rows(ruleIds).size()
		if (count == 0){
			testRunner.fail("Не смогли найти правил для расписания ресурса  "+resourceScheduleId)
			return
		}
		if(type =="SAR"){
			def sarRuleId=sql.firstRow("select id from SAR_SCHRULE where SAR_SCHRULE_TYPE_ID=7 and RESOURCE_SCHEDULE_ID="+resourceScheduleId)

			if(sarRuleId!=null){
				log.info "найдено правило приема по маршруту"
				sql.execute ("DELETE from SAR_SHRULE_ROUTES WHERE SAR_SCHRULE_ROUTE_RULE_ID in ( "+
						" select ID  from SAR_SHRULE_ROUTE_RULE where SAR_SCHRULE_ID IN ("+ruleIds+"))")
				sql.execute ("DELETE from SAR_SHRULE_ROUTE_RULE where SAR_SCHRULE_ID IN ("+ruleIds+")")
			}
		}

		sql.execute ("DELETE  from  "+type+"_SCHRULE_AGE_GROUP WHERE SAR_SCHRULE_ID IN ("+ruleIds+")")
		sql.execute ("DELETE  from  "+type+"_SCHRULE_TREATMENT WHERE SAR_SCHRULE_ID IN ("+ruleIds+")")
		sql.execute ("DELETE  from  "+type+"_SCHRULE_DATES WHERE SAR_SCHRULE_ID IN ("+ruleIds+")")
		sql.execute ("DELETE  from  "+type+"_SCHRULE_DATES_EXCLUDED WHERE SAR_SCHRULE_ID IN ("+ruleIds+")")
		sql.execute ("DELETE  from  "+type+"_SCHRULE_DATES_INCLUDED WHERE SAR_SCHRULE_ID IN ("+ruleIds+")")
		sql.execute ("DELETE  from  "+type+"_SCHRULE_TIME_INTERVALS WHERE SAR_SCHRULE_ID IN ("+ruleIds+")")
		sql.execute ("DELETE  from  "+type+"_SCHRULE WHERE RESOURCE_SCHEDULE_ID="+resourceScheduleId)

		sql.execute ("update  "+tableResSchedule+" set status='NOT_DEFINED' where ID="+resourceScheduleId)
	};

	//создание нужного количества СДРов
	def createSarWithCount(int count=1, int countMF=1, int countTreatments=1){
		String functionName=mainScripts.getCheckNullFunctionName()
		getTreatmentsWithMapping(countTreatments)
		checkExistenceResourceType("SPECIAL_AVAILABLE_RESOURCE")
		createStructureOfMO(countMF)

		mainScripts.findOrCreateEntitiesInDBwithConditionAndCount("tableDepNom","ID, TITLE, \"START\", \"END\"","-2,'Отделение автотест РиР2', TIMESTAMP '2015-11-01 00:00:00.000000', NULL","depNomId",1)
		def moId=testRunner.testCase.getPropertyValue("moId1")

		def equipmentModel=testRunner.testCase.getPropertyValue("equipmentModel1")
		def equipmentTypeId=testRunner.testCase.getPropertyValue("equipmentTypeId1")
		def equipmentProfileId=testRunner.testCase.getPropertyValue("epId1")
		def equipmentId=testRunner.testCase.getPropertyValue("equipmentId1")

		def depId=testRunner.testCase.getPropertyValue("depId1")
		def depNomTitle=testRunner.testCase.getPropertyValue("depTitle1")
		def depNomId=testRunner.testCase.getPropertyValue("depNomId1")
		def resTypeId=testRunner.testCase.getPropertyValue("resTypeSarId1")
		def parentTypeId=testRunner.testCase.getPropertyValue("equipmentTypeId1")
		def personalizedService=testRunner.testCase.getPropertyValue("personalizedServiceTrue")
		def capacity=testRunner.testCase.getPropertyValue("capacity1")

		String eColumns="ID, EQUIPMENT_TYPE_ID, PERSONALIZED_SERVICE, CAPACITY_FOR_ROOM, EQUIPMENT_PROFILE_ID,"+
				" MEDICAL_ORGANIZATION_ID, MEDICAL_FACILITY_ID, DEPARTMENT_ID, DEPARTMENT_TITLE, DEPARTMENT_NOM_ID,"+
				" LOCATION_ID, ARCHIVED, EQUIPMENT_ID, EQUIPMENT_MODEL"

		String etColumns="ID, EQUIPMENT_ID, TREATMENT_ID, USE_BY_DEFAULT"

		String resColumns="ID, ARCHIVED,TITLE, RESOURCE_KIND,PARENT_ID,MEDICAL_FACILITY_ID,"+
				" MEDICAL_ORGANIZATION_ID, SYNC_STATUS, SYNC_STATUS_CHANGED, "+
				"RESOURCE_TYPE_ID, DEPARTMENT_ID, DEPARTMENT_TITLE, DEPARTMENT_NOM_ID, "+
				"FUNCTION_KIND, FUNCTION_ID, FUNCTION_TITLE, LOCATION_ID, PARENT_TYPE_ID"
		for (int j=1; j<=countMF; j++) {
			def mfId = testRunner.testCase.getPropertyValue("mfId"+j)
			def locId = testRunner.testCase.getPropertyValue("locId"+j)
			String eValues = equipmentTypeId + "," + personalizedService + "," + capacity + "," + equipmentProfileId + "," + moId + "," + mfId + "," + depId + ",'" + depNomTitle + "'," +
					depNomId + "," + locId + ",0," + equipmentId + ",'" + equipmentModel + "'"
			for (int k = 1; k <= count; k++) {

				mainScripts.createEntitiesInDBwithIndex("tableE", eColumns, eValues, "eId"+j+"_", k)

				def eId = testRunner.testCase.getPropertyValue("eId" +j+"_"+ k)

				int useByDefault = 1

				for (int i = 1; i <= countTreatments; i++) {
					if (i > 1) {
						useByDefault = 0
					}
					def tId = testRunner.testCase.getPropertyValue("tId" + i)
					String etValues = eId + "," + tId + "," + useByDefault
					log.info etValues
					mainScripts.createEntitiesInDBwithIndex("tableET", etColumns, etValues, "etId"+j+"_" + k + "_", i)
				}

				long resourceId = sql.firstRow("SELECT "+functionName+"(MAX(ID)+1,3000000000)AS ID FROM \"RESOURCE\"").ID

				log.info resourceId + " resourceId"

				String resValue = "0, '" + equipmentModel + " № " + k + "','SPECIAL_AVAILABLE_RESOURCE'," + eId + "," + mfId + "," + moId + ",'OK', CURRENT_TIMESTAMP, " +
						resTypeId + "," + depId + ",'" + depNomTitle + "'," + depNomId + ",'EQUIPMENT_FUNCTION'," + parentTypeId + ",'" + equipmentModel +
						"'," + locId + "," + parentTypeId

				mainScripts.createEntitiesInDBwithIndex("tableRes", resColumns, resValue, "resId"+j+"_", k, resourceId)
			}
		}
	};

	//удаляет то, что создано методом createSarWithCount
	def clearSarWithCount(int count=1, int countMF=1, int countTreatments=1) {
		for (int j=1; j<=countMF; j++) {
			for (int k = 1; k <= count; k++) {
				String resId = testRunner.testCase.getPropertyValue("resId" + j + "_" + k)
				mainScripts.deleteCreatedByTestInTestCaseWithCounter("tableET", "etId" + j + "_" + k + "_", countTreatments)
				String tableJ = testRunner.testCase.testSuite.project.getPropertyValue("tableJ")
				sql.execute("delete from " + tableJ + " where RESOURCE_ID=" + resId)
			}

			mainScripts.deleteCreatedByTestInTestCaseWithCounter("tableRes", "resId" + j + "_", count)
			mainScripts.deleteCreatedByTestInTestCaseWithCounter("tableE", "eId" + j + "_", count)
		}
		mainScripts.deleteCreatedByTestInTestCaseWithCounter("tableDepNom","depNomId")
		clearStructureOfMO(countMF)
		mainScripts.deleteCreatedByTestInTestCaseWithCounter("tableSpec","specId")
		clearTreatmentsWithMapping(countTreatments)
		mainScripts.deleteCreatedByTestInTestCaseWithCounter("tableEP","epId")
		mainScripts.deleteCreatedByTestInTestCaseWithCounter("tableResType","resTypeSarId")
	};

	def findOrCreateSarSchruleKind(String kind, int priority, String properyName){
		String tableSarRuleType=testRunner.testCase.testSuite.project.getPropertyValue("tableSarRuleType")
		mainScripts.getDoesntExisted(tableSarRuleType,"sarSchruleTypeId")
		String sarSchruleTypeId=testRunner.testCase.testSuite.project.getPropertyValue("sarSchruleTypeId")
		mainScripts.findOrCreateEntitiesInDBwithConditionAndCount("tableSarRuleType", "ID,ARCHIVED,TITLE,DESCRIPTION,KIND,PRIORITY", sarSchruleTypeId+", 0, 'Тип правил расписаний СДР вида '"+kind+" автотест РиР.2',Тип правил расписаний СДР вида '"+kind+" автотест РиР.2'','"+kind+"',"+priority,properyName,1,"where archived = 0 and KIND='"+kind+"'" )
	};

	def getSarSchruleTypes(String kinds){
		String[] sarSchruleKinds = kinds.split(",");
		int count = sarSchruleKinds.size()
		def types = [];
		for (int i=0; i<count; i++){
			log.info sarSchruleKinds[i]
			types << SarSchruleTypes.valueOf(sarSchruleKinds[i])
		}
		return types
	};

	// из строки со списком видов правил СДР находит правила такого вида и записывает в переменные, если нет - создает
	def getSarSchruleKinds(String kind){
		SarSchruleTypes[] kinds = getSarSchruleTypes(kind)
		for (int i=0; i<kinds.size(); i++){
			findOrCreateSarSchruleKind(kinds[i].name(), kinds[i].getPriority(), kinds[i].getPropertyName())
		}
	};

	//чистит данные и проперти созданные методом getSarSchruleKinds
	def clearSarSchruleKinds(String kind){
		SarSchruleTypes[] kinds = getSarSchruleTypes(kind)
		for (int i=0; i<kinds.size(); i++){
			mainScripts.deleteCreatedByTestInTestCaseWithCounter("tableSarRuleType", kinds[i].getPropertyName())
		}
	};

	def sendScheduleStatusMessage( String operationId, String resourceScheduleId, Boolean success =true,int countErrors=1, String topicName="topicScheduleProcessing" ){
		String file =mainScripts.returnFileAsText("directoryOperationMessage");
		String messageWithoutErrors = file.replace("\$operationId",operationId).replace("\$resourceScheduleId",resourceScheduleId).replace("\$success",success.toString());
		String message
		if (success == true){
			message= messageWithoutErrors.replace("\$errors","")
		}
		else {
			String errorsTemplate =mainScripts.returnFileAsText("directoryOperationErrors");
			String errorsArray=""
			String errors="<messages>\$messages</messages>"
			log.info errors
			for (int i=1; i<=countErrors; i++){
				String code =testRunner.testCase.getPropertyValue("errorCode"+i)
				//log.info code
				String mes =testRunner.testCase.getPropertyValue("errorMessage"+i)
				//log.info mes
				errorsArray = errorsArray+errorsTemplate.replace("\$code",code).replace("\$message",mes)
				log.info errorsArray
			}
			message = messageWithoutErrors.replace("\$errors",errorsArray)
			//log.info message
		}

		mainScripts.sendMessageToKafka2(topicName,message)
	};

	//побор дат для расписания
	def getSchedulePeriods(){
		def monthsNumberInSchedule = testRunner.testCase.getPropertyValue("countMonthsInSchedule").toInteger();
		def countSchedule = testRunner.testCase.getPropertyValue("countSchedule").toInteger();

		def schedulePeriodStart=LocalDate.now();

		def schedulePeriodNextDay=schedulePeriodStart.plusDays(1);

		def scheduleNewPeriodStart=schedulePeriodStart.plusMonths(monthsNumberInSchedule-1);
		def schedulePeriodEnd=scheduleNewPeriodStart.with(TemporalAdjusters.lastDayOfMonth());
		def yesterday = schedulePeriodStart.minusDays(1);
		def previousMonth=schedulePeriodStart.minusMonths(1);
		def previousMonthFirstDate=previousMonth.with(TemporalAdjusters.firstDayOfMonth());
		def previousMonthLastDate=previousMonthFirstDate.with(TemporalAdjusters.lastDayOfMonth());
		def penult=schedulePeriodEnd.minusDays(1);

		testRunner.testCase.setPropertyValue("schedulePeriodStart1",schedulePeriodStart.toString());
		testRunner.testCase.setPropertyValue("schedulePeriodNextDay1",schedulePeriodNextDay.toString());
		testRunner.testCase.setPropertyValue("schedulePeriodEnd1",schedulePeriodEnd.toString());
		testRunner.testCase.setPropertyValue("yesterday",yesterday.toString());
		testRunner.testCase.setPropertyValue("previousMonthFirstDate",previousMonthFirstDate.toString());
		testRunner.testCase.setPropertyValue("previousMonthLastDate",previousMonthLastDate.toString());
		testRunner.testCase.setPropertyValue("schedulePeriodPenultDay1",penult.toString());

		def ospe=schedulePeriodEnd

		if (countSchedule>1) {
			for (int i = 2; i <= countSchedule; i++) {
				def sps = ospe.plusDays(1)
				def spe1 = ospe.plusMonths(monthsNumberInSchedule)
				def spe = spe1.with(TemporalAdjusters.lastDayOfMonth())
				penult = spe.minusDays(1)
				def spnd = sps.plusDays(1)
				testRunner.testCase.setPropertyValue("schedulePeriodStart" + i, sps.toString());
				testRunner.testCase.setPropertyValue("schedulePeriodEnd" + i, spe.toString());
				testRunner.testCase.setPropertyValue("schedulePeriodPenultDay" + i, penult.toString());
				testRunner.testCase.setPropertyValue("schedulePeriodNextDay" + i, spnd.toString());
				log.info("Период расписания №" + i + " с " + sps + " по " + spe + " предпоследняя дата периода " + penult);
				ospe = spe
			}
		}
	};

	//очистка пропертей с датами для расписания, созданных в рамках getSchedulePeriods
	def clearSchedulePeriods(){
		def countSchedule = testRunner.testCase.getPropertyValue("countSchedule").toInteger();

		for (int i = 1; i <= countSchedule; i++) {
			testRunner.testCase.removeProperty("schedulePeriodStart" + i);
			testRunner.testCase.removeProperty("schedulePeriodEnd" + i);
			testRunner.testCase.removeProperty("schedulePeriodPenultDay" + i);
			testRunner.testCase.removeProperty("schedulePeriodNextDay" + i)
		}
		testRunner.testCase.removeProperty("yesterday");
		testRunner.testCase.removeProperty("previousMonthFirstDate");
		testRunner.testCase.removeProperty("previousMonthLastDate")
	};

    def getNonarchivedMoMF(Integer countMO=0,Integer countMF=0){
        countMO= countMO==0 ? testRunner.testCase.getPropertyValue("countMO").toInteger() : countMO
        countMF= countMF==0 ? testRunner.testCase.getPropertyValue("countMF").toInteger() : countMF
        String getMoQuery="select mf.medical_organization_id as id  from medical_organization mo "+
                "join medical_facility mf on mf.medical_organization_id =mo.id "+
                "where mo.archived =false and mf.archived = false "+
                "group by mf.medical_organization_id "+
                "having count(mf.id)>="+countMF+
                " order by mf.medical_organization_id desc"
        //log.info getMoQuery
        def moIdArray=sql.rows(getMoQuery).id
        //log.info moIdArray.toString()
        for(int i=0;i<countMO; i++){
            int n=i+1
            def moId=moIdArray[i]
            String getMfQuery="select id from medical_facility where archived = false and medical_organization_id="+moId
            def mfIdArray=sql.rows(getMfQuery).id
            mainScripts.createPropertyWithCreatedByTCparameter("moId",moId.toString(), "1", n )
            log.info "Записан параметр moId"+n+"="+moId
            for(int j=0;j<countMF; j++){
                int k=j+1
                def mfId=mfIdArray[j]
                mainScripts.createPropertyWithCreatedByTCparameter("mfId"+n+"_",moId.toString(), "1", k )
                log.info "Записан параметр mfId"+n+"_"+k+"="+mfId
            }
        }
        log.info "END"
    };

	def createMixedStructureMo(){
		getNonarchivedMoMF(1,2)
		String functionName=mainScripts.getCheckNullFunctionName()
		String tableMO = testRunner.testCase.testSuite.project.getPropertyValue("tableMO");
		String tableMF = testRunner.testCase.testSuite.project.getPropertyValue("tableMF");
		long minId;
		log.info("SELECT "+functionName+"(min(ID)-1,-1) AS ID FROM "+tableMO);
		long minIdMO=sql.rows("SELECT "+functionName+"(min(ID)-1,100) AS ID FROM "+tableMO)[0].ID;
		long minIdMF=sql.rows("SELECT "+functionName+"(min(ID)-1,100) AS ID FROM "+tableMF)[0].ID;
		minId = minIdMO > minIdMF ? minIdMF : minIdMO;
		int nextMoId=minId-2
		mainScripts.createEntitiesInDBwithIndex("tableMO","ID,ARCHIVED,NAME,NAME_FULL,OGRN,CLOSED,CREATED","false,'МО автотест РиР.2','МО автотест РиР.2',1111,NULL,TIMESTAMP '1970-01-01 00:00:00.000000'","moId",2,minId);
		mainScripts.createEntitiesInDBwithIndex("tableMO","ID,ARCHIVED,NAME,NAME_FULL,OGRN,CLOSED,CREATED","true,'Архивное МО автотест РиР.2','Архивное МО автотест РиР.2',1111,NULL,TIMESTAMP '1970-01-01 00:00:00.000000'","moId",3,nextMoId);
		mainScripts.findOrCreateEntitiesInDBwithConditionAndCount("tableAO" , "id,archive,global_id,address_string,updated" , "false, 6203000, 'Адресный объект автоест РиР.2', null" , "aoId" , 1,  " where archive= false")
		def aoId=testRunner.testCase.getPropertyValue("aoId1")
		mainScripts.createEntitiesInDBwithIndex("tableMF", "ID, ARCHIVED, MEDICAL_ORGANIZATION_ID, NAME_SHORT, NAME, NAME_FULL, IS_HEAD, ADDRESS_OBJECT_ID, CLOSED, CREATED", "false," + minId + ", 'Головное неархивное МУ автотест РиР.2','Головное неархивное МУ автотест РиР.2','Головное неархивное МУ автотест РиР.2',true," + aoId + ", NULL,CURRENT_TIMESTAMP ", "mfId2_", 1, minId);
		mainScripts.createEntitiesInDBwithIndex("tableMF", "ID, ARCHIVED, MEDICAL_ORGANIZATION_ID, NAME_SHORT, NAME, NAME_FULL, IS_HEAD, ADDRESS_OBJECT_ID, CLOSED, CREATED", "true," + minId + ", 'Головное архивное МУ автотест РиР.2','Головное архивное МУ автотест РиР.2','Головное архивное МУ автотест РиР.2',false," + aoId + ", NULL,CURRENT_TIMESTAMP ", "mfId2_", 2, minId-1);
		mainScripts.createEntitiesInDBwithIndex("tableMF", "ID, ARCHIVED, MEDICAL_ORGANIZATION_ID, NAME_SHORT, NAME, NAME_FULL, IS_HEAD, ADDRESS_OBJECT_ID, CLOSED, CREATED", "false," + nextMoId + ", 'Головное неархивное МУ автотест РиР.2','Головное неархивное МУ автотест РиР.2','Головное неархивное МУ автотест РиР.2',true," + aoId + ", NULL,CURRENT_TIMESTAMP ", "mfId3_", 1, minId-2);
		mainScripts.createEntitiesInDBwithIndex("tableMF", "ID, ARCHIVED, MEDICAL_ORGANIZATION_ID, NAME_SHORT, NAME, NAME_FULL, IS_HEAD, ADDRESS_OBJECT_ID, CLOSED, CREATED", "true," + nextMoId + ", 'Головное архивное МУ автотест РиР.2','Головное архивное МУ автотест РиР.2','Головное архивное МУ автотест РиР.2',false," + aoId + ", NULL,CURRENT_TIMESTAMP ", "mfId3_", 2, minId-3)
	};


	//очистить все связанные с ДР проперти и созданные автотестом записи в бд
	def clearAR(String jobExId, Boolean clearArchivedPosProf=false) {
		def id = testRunner.testCase.getPropertyValue(jobExId)
		String tableRes = testRunner.testCase.testSuite.project.getPropertyValue("tableRes")
		String tableResSchedule = testRunner.testCase.testSuite.project.getPropertyValue("tableResSchedule")
		def res = sql.firstRow("select id from " + tableRes + " where RESOURCE_KIND ='AVAILABLE_RESOURCE' AND PARENT_ID=" + id)
		if (res != null ) {
			def resId=res.id
			def resSchedules = sql.rows("select id from " + tableResSchedule + " where resource_id=" + resId)
			if (resSchedules.size() > 0) {
				for (int i = 0; i < resSchedules.size(); i++) {
					clearResourceSchedule(resSchedules[i].id)
				}
			}
			clearResourceSyncErrors(resId)
			String tableResPar = testRunner.testCase.testSuite.project.getPropertyValue("tableResPar")
			sql.execute("delete from " + tableResPar + " where resource_id=" + resId)
			sql.execute("delete from " + tableRes + " where id=" + resId)
		}
		JobExecution.clearPosProfWithJobExecution(jobExId, clearArchivedPosProf)
		mainScripts.clearCreatedByTestInTestCaseWithIndex("tableJE", "id", 1)
		mainScripts.clearCreatedByTestInTestCaseWithIndex("tablePosNom", "posNomId", 1)
		mainScripts.clearCreatedByTestInTestCaseWithIndex("tableResType", "resTypeArId", 1)
		clearSpecializations()
		clearDepartmentNoms()
	}

}