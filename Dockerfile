# В качестве родительского образа используется образ ubuntu-server с предустановленными openjdk8, mysql, googleChrome
FROM romanyakit/botparser:latest

# Копируем сборку TelegramBotParser.jar в контейнер
COPY target/TelegramBotParser.jar /home/TelegramBotParser.jar

# Копируем драйвер в корень файловой системы контейнера
COPY build_profiles/prod/chromedriver /chromedriver

ENV MYSQL_ROOT_PASSWORD=zxcv
ENV MYSQL_DATABASE=tg_bot
ENV MYSQL_USER=tg_bot
ENV MYSQL_PASSWORD=tg_bot

COPY ./init_db.sql /docker-entrypoint-initdb.d/

# CMD закоментирован потому что Mysql не успевает запуститися до приложения
# как следствие приложение падает вместе с контейнером из за ошибки подключения к БД
# поэтому запуск java -jar /home/TelegramBotParser.jar нужно выполнять в ручную после поднятия контейнера
# TODO переделать на docker-compose где 1ый сервис это бд 2ой это приложение с указанием depends_on. Тогда вначале будет подниматся БД, а потом будет запускаться приложение
#CMD ["java", "-jar", "/home/TelegramBotParser.jar"]