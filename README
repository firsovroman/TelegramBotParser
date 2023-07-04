# TelegramBotParser
Copyright © 2022 Roman Firsov All rights reserved.

Бот предназначен для пуш уведомлений пользователя по заданному фильтру на сайте объявлений Ав*то .


Block diagram for this project.
![alt text](https://github.com/firsovroman/TelegramBotParserPublic/blob/master/.doc/scheme.png)

![alt text](https://github.com/firsovroman/SpringMVC_Hibernate_AOP/raw/main/pictures/1.png)

Контейнер под приложение: https://hub.docker.com/repository/docker/romanyakit/botparser


Инструкция по запуску приложения в контейнере:
```
mysql -p
password: zxcv
```

Минимальный конфиг базы:
```
USE mysql;   
SELECT user FROM user;  
CREATE DATABASE tg_bot;  
CREATE USER 'tg_bot'@'%' IDENTIFIED BY 'tg_bot'; 
GRANT ALL PRIVILEGES ON *.* TO 'tg_bot'@'%' WITH GRANT OPTION; 
FLUSH PRIVILEGES;
```

перейти в /home и запустить
```
java -jar TelegramBotParser.jar
```

