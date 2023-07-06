# В качестве родительского образа используется образ ubuntu-server с предустановленными openjdk8, GoogleChrome
FROM romanyakit/botparser:latest

# Копируем сборку TelegramBotParser.jar в контейнер
COPY target/TelegramBotParser.jar /home/TelegramBotParser.jar

# Копируем драйвер в корень файловой системы контейнера
COPY build_profiles/prod/chromedriver /chromedriver

# запускаем сам сервис
CMD ["java", "-jar", "/home/TelegramBotParser.jar"]