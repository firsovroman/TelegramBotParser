# TelegramBotParser

Проект представляет собой бота, созданного для регулярного автоматического отслеживания объявлений на сайте Авито и отправки уведомлений о новых объявлениях в Telegram. Бот позволяет пользователям подписаться на интересующие их категории и моментально получать оповещения о новых объявлениях в этих категориях.



Архитектурная схема проекта.
![alt text](https://github.com/firsovroman/TelegramBotParserPublic/blob/master/.doc/scheme.png)


# Установка и настройка на локальной машине разработчика (dev).
На локальной машине разработчика потребуется наличие запущенной MySQL, а так же JDK 8.

1. Клонируйте репозиторий на свой локальный компьютер
2. В конфигурационном файле <b>src/main/resources/telegram.properties</b> задайте свойства <b>bot.name</b> и <b>bot.key</b> вашего бота
3. Запустите скрип <b>init_db.sql</b> на локальной MySQL
4. С помошью maven соберите проект. (ВАЖНО! Соберите с использованием профиля сборки <b>dev</b> )
5. Запускайте <b>target/TelegramBotParser.jar</b> любым удобным сопсобом


# Установка и настройка в контейнере (prod).

Для запуска потребуется наличие инстумента сборки Maven и контейнеризатор приложений Docker

1. Клонируйте репозиторий на свой локальный компьютер
2. В конфигурационном файле <b>src/main/resources/telegram.properties</b> задайте свойства <b>bot.name</b> и <b>bot.key</b> вашего бота
3. С помошью maven соберите проект. (ВАЖНО! Соберите с использованием профиля сборки <b>prod</b> ) Проверьте что сборка появилась <b>target/TelegramBotParser.jar</b> 
4. Выполните <b>_docker compose up_</b> в директории проекта


# Конфигурационные файлы

| Файл | Свойство | Описание |
|------------|------------|------------|
| telegram.properties  | bot.name  | Имя вашего бота  |
| telegram.properties   | bot.key   | Токен вашего бота  |
| parser.properties  | parser.adAgeRegExp  | Регулярное выражение котороым можно фильтровать возраст объявлений. При значении (.*) объявления по возрасту не фильтруются.  |
| parser.properties   | parser.defUrlForParse   | URL сайта avito с выставленным фильтром "по времени" важно  |
| application.properties   | processor.siteScanningIntervalMinutes   | Частота запуска сканнера в минутах  |

# Доступные (Telegram) пользователю команды

| Команда | Описание |
|------------|------------|
| /info     | Получить справочную информацию о боте.  |
| /start    | Подписаться на анонсы.                  |
| /stop     | Отписаться от рассылки анонсов.         |
| /edit     | Задать URL поиска. Копируется из адресной строки сайта объявлений. После того как на сайте вы попали в нужный раздел поиска по интересной вам категории.  | 
| /excluded | Можно определить слова исключения. Если в графе "Описание" рассматриваемого объявления встретится слово из этого параметра то по такому объявлению уведомление вы не получите.   | 
| /reset    | Сбросить список слов исключений. После вызова команды слов в списке не будет.  | 
| /config   | Проверить объявления по какому URL (с настроенным поиском) сейчас отслеживаются. И проверить какие слова сейчас в списке "нежелательные". Иными словами проверить значения установленные командами /edit и  /excluded  | 


# Пример использования:
Допустим вам нужно купить собаку породы Той-терьер.
Однако если смотреть низкий ценовой сегмент то вы наткнетесь на объявления (на вязку), к сожалению в авито нет разделения на категории продажа собаки и сдача в аренду для получения щенков.
Решить проблему с не релевантными объявлениями можно с помошью бота мы можем отфильтровать объявления по списку нежелательных слов в описании по типу (вязка, жених, невеста и прочее). 

Пример НЕ релевантного объявления.
<br>
![alt text](https://github.com/firsovroman/TelegramBotParserPublic/blob/master/.doc/examples/1.jpg)
<br>

Пример настройки фильтров поиска.
<br>
![alt text](https://github.com/firsovroman/TelegramBotParserPublic/blob/master/.doc/examples/2.png)
<br>

После выставления параметров поиска копируем ссылку.
<br>
![alt text](https://github.com/firsovroman/TelegramBotParserPublic/blob/master/.doc/examples/3.jpg)
<br>

Затем в самом боте используем команду _/edit_ для добавления полученного URL.
<br>
После используем команду _/excluded_ для добавления слова "вязка".
<br>

Copyright © 2023 Roman Firsov All rights reserved.
