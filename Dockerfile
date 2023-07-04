# В качестве родительского образа используется образ ubuntu-server с предустановленными openjdk8, mysql, googleChrome
FROM romanyakit/botparser:latest

# Копируем файл TelegramBotParser.jar в контейнер
COPY target/TelegramBotParser.jar /home/TelegramBotParser.jar

# Копируем драйвер в корень файловой системы контейнера
COPY build_profiles/prod/chromedriver /chromedriver

# Добавляем преднастройку для MySQL
# Создаем базу данных tg_bot
# Создаем пользователя tg_bot и даем ему права на базу данных tg_bot
ENV MYSQL_ROOT_PASSWORD=zxcv
ENV MYSQL_DATABASE=tg_bot
ENV MYSQL_USER=tg_bot
ENV MYSQL_PASSWORD=tg_bot

COPY ./init_db.sql /docker-entrypoint-initdb.d/

# Запускаем файл TelegramBotParser.jar
#CMD ["java", "-jar", "/home/TelegramBotParser.jar"]