import json
from kafka import KafkaProducer
import logging
import requests


class MainFunction():

    def serialized_message(self, path):
        with open(path, 'r', encoding='utf-8') as file:
            file_connect = json.load(file)
        return file_connect


class Kafka():

    def send_message_to_kafka(self, topic, message):
        # Включаем логирование
        logging.basicConfig(level=logging.INFO)
        # Подключаемся к Кафке
        producer = KafkaProducer(bootstrap_servers='10.2.172.24:9092')
        producer.send(topic, message.encode('utf-8'))


class Elastic():

    def check_patient_PR(self, patientId):
        """Поиск пациента с последующим сохранением в файл"""

        # Создание клиента
        BASE_URL = 'http://ehr-index-elasticsearch.svc-emias-registry.test.emias.mos.ru/default_registry/_search'
        query = {
            "size": 20,
            "track_total_hits": False,
            "query": {
                "match": {
                    "patientId": patientId
                }
            }
        }

        headers = {
            "Content-Type": "application/json"
        }
        response = requests.post(
            BASE_URL, data=json.dumps(query), headers=headers)
        with open(f'C:/Users/sbronnikov/Desktop/responseHistory_PR/{patientId}.json', 'w', encoding='utf-8') as file:
            file.write(response.text)
        return json.loads(response.text)

    def find_patient(self, patientId):
        """Поиск пациента по ID"""

        BASE_URL = 'http://ehr-index-elasticsearch.svc-emias-registry.test.emias.mos.ru/default_registry/_search'
        query = {
            "size": 20,
            "track_total_hits": False,
            "query": {
                "match": {
                    "patientId": patientId
                }
            }
        }

        headers = {
            "Content-Type": "application/json"
        }
        response = requests.post(
            BASE_URL, data=json.dumps(query), headers=headers)
        return response

    def check_find_patient(self):
        """Поиск свободного ID пациента в эластике"""

        import random
        while True:
            patientId = random.randint(100**3, 100**4)
            BASE_URL = 'http://ehr-index-elasticsearch.svc-emias-registry.test.emias.mos.ru/default_registry/_search'
            query = {
                "size": 20,
                "track_total_hits": False,
                "query": {
                    "match": {
                        "patientId": patientId
                    }
                }
            }

            headers = {
                "Content-Type": "application/json"
            }
            response = requests.post(
                BASE_URL, data=json.dumps(query), headers=headers)
            hits = response.json().get('hits', {}).get('hits', [])
            if hits:
                continue  # переходим на следующую итерацию цикла, генерируя новый пациент
            else:
                break  # выходим из цикла, потому что нашли свободный id пациента
        return patientId
