# 02 — Параметры HTTP-запроса в Spring Boot

Небольшое учебное приложение на Spring Boot 3.5.16 и Java 17. Оно отвечает на `GET /hello`, читает из адреса два обязательных параметра и возвращает простой текст. Шаблонизаторы и HTML-представления здесь не используются: это именно HTTP API.

## 1. Цель, original task и перевод

Цель работы — научиться принимать query-параметры HTTP-запроса с помощью `@RequestParam`, связывать их с аргументами Java-метода и проверять контракт контроллера автоматическими тестами.

Create a Spring Boot endpoint `GET /hello`. It must accept required query parameters `location` and `name` and return `Welcome to the {location} {name}!`.

Correct request example: `http://localhost:8080/hello?location=moon&name=John`.

Создайте endpoint Spring Boot `GET /hello`. Он обязан принять обязательные query-параметры `location` и `name` и вернуть строку `Welcome to the {location} {name}!`.

Корректный пример запроса: `http://localhost:8080/hello?location=moon&name=John`.

## 2. Критерии готовности

- Используются Spring Boot 3.5.16 и Java 17.
- В пакете `com.example.requestparameters` есть публичный `HelloController` с `@Controller` и `@ResponseBody` на классе.
- Публичный метод имеет `@GetMapping("/hello")`; отдельная `@ResponseBody` на методе не нужна, поскольку она уже задана на классе.
- У обоих параметров явно указаны имена: `@RequestParam(name = "location")` и `@RequestParam(name = "name")`.
- При `location=moon&name=John` ответ — `Welcome to the moon John!` и статус `200 OK`.
- При отсутствии любого обязательного параметра сервер отвечает `400 Bad Request`.
- Автотесты включают шесть проверок контроллера и проверку загрузки контекста.

## 3. Подготовка: macOS и IntelliJ IDEA

Установите JDK 17 (например, Temurin) и IntelliJ IDEA. В IDEA выберите **File → Open**, откройте папку проекта и дождитесь импорта Maven. В **Project Structure** убедитесь, что SDK — Java 17. В терминале macOS проверить Java можно командой `java -version`. Maven Wrapper уже лежит в проекте, поэтому отдельная установка Maven не требуется.

## 4. Запуск и проверка

Из корня этого проекта выполните:

```bash
./mvnw test
./mvnw spring-boot:run
```

После сообщения о запуске откройте в браузере:

```text
http://localhost:8080/hello?location=moon&name=John
```

Либо отправьте запрос из терминала. URL заключён в одинарные кавычки: так оболочка не воспримет `&` как разделитель команд.

```bash
curl 'http://localhost:8080/hello?location=moon&name=John'
```

Остановить приложение можно `Ctrl+C`.

## 5. Структура проекта

```text
src/main/java/com/example/requestparameters/
  RequestParametersApplication.java  # точка входа Spring Boot
  HelloController.java               # HTTP endpoint
src/test/java/com/example/requestparameters/
  RequestParametersApplicationTests.java # contextLoads
  HelloControllerTest.java               # 6 MockMvc-тестов
```

## 6. HTTP и query-contract

Контракт — это обещание API клиенту.

| Свойство | Значение |
| --- | --- |
| Метод | `GET` |
| Путь | `/hello` |
| Обязательные параметры | `location`, `name` |
| Успешный ответ | `200 OK`, `text/plain` |
| Тело успешного ответа | `Welcome to the {location} {name}!` |
| Ошибка без `location` или `name` | `400 Bad Request` |

В URL знак `?` отделяет путь от query string, а `&` отделяет одну пару `имя=значение` от другой. Поэтому `location=moon&name=John` — два параметра. Порядок пар не важен: `?name=John&location=moon` даёт тот же результат. Неизвестный параметр, например `language=English`, контроллер не использует и он не меняет успешный ответ.

Пробелы и специальные символы в URL должны быть закодированы. Например, `New York` можно передать как `New%20York` (или `New+York` в query string):

```bash
curl 'http://localhost:8080/hello?location=New%20York&name=Mary%20Jane'
```

## 7. Путь запроса

```text
клиент → Tomcat → Spring MVC ищет GET /hello → HelloController.hello(...)
       → @RequestParam извлекает location и name → строка ответа → клиент
```

Если обязательного значения нет, Spring MVC не вызывает обычный успешный сценарий метода: она формирует HTTP 400.

## 8. Основные понятия

- **Spring Boot** автоматически настраивает веб-приложение и встроенный сервер.
- **Контроллер** принимает HTTP-запрос и формирует ответ.
- **Аннотация** добавляет декларативную инструкцию к классу или методу.
- **Query-параметр** — данные после `?` в URL.
- **`@RequestParam`** связывает query-параметр с аргументом метода.
- **`@ResponseBody` на классе** отправляет возвращённую строку каждого подходящего метода в тело HTTP-ответа, а не ищет HTML-шаблон.

Подробнее — в [SPRING_CONCEPTS.md](../SPRING_CONCEPTS.md).

## 9. Автоматические тесты

`HelloControllerTest` использует `@WebMvcTest` и `MockMvc` без запуска реального сетевого сервера. Шесть тестов проверяют: обычный запрос, обратный порядок параметров, пробелы в значениях, отсутствие `location`, отсутствие `name` и лишний параметр. `RequestParametersApplicationTests` с `contextLoads()` проверяет, что Spring-контекст создаётся.

## 10. Ручная приёмка

После запуска приложения проверьте:

```bash
curl -i 'http://localhost:8080/hello?location=moon&name=John'
curl -i 'http://localhost:8080/hello?name=John&location=moon'
curl -i 'http://localhost:8080/hello?location=New%20York&name=Mary%20Jane'
curl -i 'http://localhost:8080/hello?name=John'
curl -i 'http://localhost:8080/hello?location=moon'
curl -i 'http://localhost:8080/hello?location=moon&name=John&language=English'
```

В первых трёх и последнем запросе ожидается `200`; в четвёртом и пятом — `400`. У последнего тело остаётся `Welcome to the moon John!`.

## 11. Типичные ошибки и исправления

| Ошибка | Наблюдаемый результат | Исправление |
| --- | --- | --- |
| Опечатка: `loction=moon` | `400` без обязательного `location` | написать ключ точно: `location` |
| Неверный регистр: `Name=John` | `400` без `name` | имена параметров чувствительны к регистру: `name` |
| Нет `?`: `/hello/location=moon` | обычно `404` | после пути поставить `?` |
| Нет `&`: `location=moonname=John` | `400` без `name` | разделить пары знаком `&` |
| URL в `curl` без кавычек | оболочка обрабатывает `&`; запрос ломается | передать URL в одинарных кавычках |
| Нет `@RequestParam` у аргумента | значение не связывается с query string как задумано | добавить `@RequestParam(name = "...")` |
| Пробел или лишний знак в имени параметра | ключ не совпадает, возможен `400` | использовать ровно `location` и `name` |
| Пробелы в значениях не закодированы | URL некорректен или передаётся не целиком | использовать `%20`/`+`, например `New%20York` |
| Не передан `location` или `name` | `400 Bad Request` | передать оба обязательных параметра |
| Порт 8080 уже занят | приложение не запускается | остановить другой процесс или настроить иной порт |
| Нет `@ResponseBody` на классе | Spring может искать представление | вернуть `@ResponseBody` на `HelloController` |

## 12. Вопросы для самопроверки

1. Что делает `@GetMapping("/hello")`?
2. Зачем `@ResponseBody` расположен на классе?
3. Что отделяет `?` в URL?
4. Для чего служит `&`?
5. Почему `@RequestParam(name = "location")` лучше не оставлять без имени в учебном контракте?
6. Что произойдёт без `location`?
7. Важен ли порядок параметров?
8. Как передать пробел в query string?
9. Почему URL для `curl` заключён в кавычки?
10. Что произойдёт с лишним `language`?
11. Что проверяет MockMvc?
12. Чем `@Controller` отличается здесь от поиска шаблона?

## 13. Полные ответы

1. Она сопоставляет GET-запрос на путь `/hello` с методом контроллера.
2. Она говорит Spring поместить `String` в тело ответа для методов этого контроллера; без неё `@Controller` обычно трактует строку как имя представления.
3. `?` отделяет путь `/hello` от строки параметров.
4. `&` разделяет пары параметров, например `location=moon` и `name=John`.
5. Явное имя фиксирует публичный HTTP-контракт и не зависит от имени переменной или настроек компилятора.
6. Параметр обязательный по умолчанию, поэтому Spring возвращает `400 Bad Request`.
7. Нет. Spring ищет значения по их именам, поэтому обратный порядок допустим.
8. Закодировать его: `%20` или `+`; пример — `location=New%20York`.
9. Во многих оболочках `&` имеет специальное значение, а кавычки передают весь URL одной строкой.
10. Он игнорируется данным методом, поскольку не объявлен как `@RequestParam`; ответ остаётся успешным.
11. MockMvc имитирует MVC-запрос и позволяет проверить статус и тело без реального TCP-порта.
12. `@Controller` обозначает MVC-компонент, а `@ResponseBody` на классе меняет обработку результата его методов: вместо шаблона отправляется текст.

## 14. English defense (100–150 words)

This project demonstrates how a Spring Boot controller reads required query parameters from an HTTP GET request. The endpoint is mapped to `/hello` with `@GetMapping`. The method receives `location` and `name` through `@RequestParam`, so Spring MVC binds values from the query string to Java method arguments. `@ResponseBody` is important because it sends the returned string directly to the HTTP response instead of resolving a view. For the request `http://localhost:8080/hello?location=moon&name=John`, the application returns `Welcome to the moon John!`. Both parameters are required by default; therefore, a request without either parameter receives HTTP 400 Bad Request. The test suite uses MockMvc to verify successful requests, parameter order, spaces, missing parameters, and an extra parameter. This makes the endpoint contract explicit and repeatable.

## 15. Optional improvements

Можно добавить валидацию длины, поддержку локализации, JSON-ответ с полями, обработчик ошибок в едином формате и документацию OpenAPI. Это отдельные улучшения: для текущей работы они не нужны и не должны менять базовый контракт.
