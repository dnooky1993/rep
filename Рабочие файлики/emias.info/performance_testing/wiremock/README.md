# Исходники docker образа мок-сервиса для ЕМИАС.ИНФО

Позволяют собрать docker образ мок-сервиса со встроенными маппингами/файлами ответов

### Состав:
- Dockerfile
- docker-entrypoint.sh
- mappings (маппинги мок-сервиса)
- __files(файлы с ответами)

### Алгоритм запуска
1. Загружаем docker образ из docker.solit-clouds.ru `docker pull docker.solit-clouds.ru/mockservice:latest`
2. Запускаем `docker run --rm -d -p 8443:8080 docker.solit-clouds.ru/mockservice:latest`

Для тестирования производительности при запуске потребуется указать доп. аргументы, позволяющие увеличить производительность сервиса.
Пример команды с доп. аргументами:
`docker run --rm -d -p 8443:8080 docker.solit-clouds.ru/mockservice:latest --no-request-journal --container-threads 10000 --async-response-enabled true --async-response-threads 10000 --global-response-templating --jetty-acceptor-threads 10 --jetty-header-buffer-size 16384`

В основе wiremock лежит Jetty сервер, судя по всему, команды при старте контейнера пробрасываются в Jetty. 
Список команд для конфигурации Jetty можно найти тут: https://unpkg.com/browse/wiremock@2.28.1/README.md