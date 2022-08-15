#fog
###_Создание индекса сайтов. Поиск страниц._
____
####Используются технологии: Spring, Sql, Lombook, ForkJoin, Thread.
____
####Обязательные настройки в файле application.yml:
> 1. Подключение к базе MySQL
ПРИМЕР:
spring.datasource:
    url: jdbc:mysql://localhost:3306/search_engine 
    username: root
    password: 111111

> 2. User agent.
По умолчанию:
userAgent: Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6
Но можно выбрать свой

> 3. Список сайтов:
Пример:
connection:
  sites:
    - url: http://www.test.ru (ОБЯЗАТЕЛЬНО!!! В конце не надо ставить слэш)
      name: Пример сайта
____
####Настройки SQL.
> 1. Создать базу.
CREATE DATABASE search_engine;

> 2. Создать таблицу site.
CREATE TABLE site
(
id INT NOT NULL AUTO_INCREMENT,
status ENUM('INDEXING', 'INDEXED', 'FAILED') NOT NULL,
status_time DATETIME NOT NULL,
last_error TEXT,
url VARCHAR(255) NOT NULL,
name VARCHAR(255) NOT NULL,
count_lemma INT NOT NULL,
PRIMARY KEY (id)
);

> Остальные таблицы программа создаст сама.
____
####Работа программы.
> После запуска, программа работает по адресу http://localhost:8080/admin/
#####Страницы:
> Страница DASHBOARD - Информация общая и по сайтам.

> Страница MANAGEMENT - Старт / Стоп индексации, проиндексировать отдельную страницу.

> Страница SEARCH - Поиск по индексу.
___
