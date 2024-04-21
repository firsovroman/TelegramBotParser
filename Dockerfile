# В качестве родительского образа используется образ mongo:6.0.15 с предустановленными openjdk-8-jdk и GoogleChrome 124.0.6367.60
FROM romanyakit/bot2024:2

# Копируем сборку TelegramBotParser.jar в контейнер
COPY target/TelegramBotParser.jar /home/TelegramBotParser.jar

# Копируем драйвер в корень файловой системы контейнера
COPY build_profiles/prod/chromedriver /chromedriver

# запускаем сам сервис
CMD ["java", "-jar", "/home/TelegramBotParser.jar"]