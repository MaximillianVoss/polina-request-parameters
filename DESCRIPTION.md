# Карточка проекта: Request Parameters

## Карточка

| Поле | Значение |
| --- | --- |
| Тема | Query-параметры в Spring MVC |
| Стек | Java 17, Spring Boot 3.5.16, Maven |
| Пакет | `com.example.requestparameters` |
| Endpoint | `GET /hello` |
| Формат ответа | обычный текст, без HTML и JSON |

## Условие и перевод

**Original:** Create a Spring Boot endpoint `GET /hello`. It must accept required query parameters `location` and `name` and return `Welcome to the {location} {name}!`.

**Перевод:** создайте endpoint `GET /hello`, принимающий обязательные query-параметры `location` и `name`, который возвращает `Welcome to the {location} {name}!`.

Правильный адрес: `http://localhost:8080/hello?location=moon&name=John`.

## Learning outcomes

После работы учащийся умеет сопоставить GET-путь с методом, извлечь именованные query-параметры через `@RequestParam`, объяснить HTTP 200/400, кодировать пробелы в URL и проверить MVC-контракт через MockMvc.

## Criteria

- `public class HelloController` имеет `@Controller` и `@ResponseBody` на классе;
- `public String hello(...)` имеет `@GetMapping("/hello")`;
- аргументы содержат явные `@RequestParam(name = "location")` и `@RequestParam(name = "name")`;
- точный успешный ответ: `Welcome to the moon John!`;
- любой отсутствующий обязательный параметр даёт 400;
- шесть MockMvc-тестов и `contextLoads()` описывают проверяемый контракт.

## Tree

```text
src/main/java/com/example/requestparameters/
  RequestParametersApplication.java
  HelloController.java
src/test/java/com/example/requestparameters/
  RequestParametersApplicationTests.java
  HelloControllerTest.java
```

## Responsibilities

`RequestParametersApplication` запускает приложение. Встроенный Tomcat принимает HTTP. `HelloController` выбирается Spring MVC для `GET /hello`, связывает параметры и формирует текст. Тесты отдельно проверяют веб-слой и загрузку контекста.

## Contract

| Вход или случай | Результат |
| --- | --- |
| `/hello?location=moon&name=John` | `200`, `Welcome to the moon John!` |
| `/hello?name=John&location=moon` | тот же `200` и текст |
| `/hello?location=New%20York&name=Mary%20Jane` | `200`, декодированные пробелы |
| нет `location` или `name` | `400 Bad Request` |
| есть `language=English` | лишний параметр не меняет ответ |

## Binding и encoding

`?` отделяет путь от query string, `&` — пары параметров, а `=` — имя от значения. Spring ищет значения по именам, поэтому порядок пар не важен. Значения с пробелами кодируются как `%20` или `+`. В shell URL берут в кавычки: `curl 'http://localhost:8080/hello?location=moon&name=John'`, иначе `&` может интерпретироваться оболочкой.

## macOS и запуск

Нужны JDK 17 и IntelliJ IDEA. Откройте Maven-проект в IDEA и выберите JDK 17. В macOS/Linux используйте `./mvnw test` и `./mvnw spring-boot:run`; в Windows — `mvnw.cmd test` и `mvnw.cmd spring-boot:run`. Приложение по умолчанию слушает 8080.

## Testing

`@WebMvcTest(HelloController.class)` и MockMvc проверяют успешный запрос, обратный порядок, пробелы, два отсутствующих параметра и лишний параметр без реального сетевого сервера. `contextLoads()` проверяет создание полного Spring-контекста.

## Limitations

Это минимальное упражнение: нет Thymeleaf, HTML, базы данных, DTO, валидации длины, JSON-ошибок или OpenAPI. Оно намеренно ограничено одним текстовым контрактом.

## RU defense

Контроллер объявлен публичным и помечен `@Controller` и `@ResponseBody` на уровне класса. Поэтому его публичный метод `hello` сопоставляется с `GET /hello`, а строка возвращается в HTTP-тело. `@RequestParam` по именам связывает `location` и `name`; они обязательны, следовательно, отсутствие одного из них приводит к HTTP 400. Порядок параметров значения не имеет, а пробелы в URL кодируются.

## EN defense

The public `HelloController` is annotated with `@Controller` and class-level `@ResponseBody`. Its public `hello` method handles `GET /hello` and writes the returned greeting directly to the response body. Named `@RequestParam` annotations bind required `location` and `name` query values. Missing either value produces HTTP 400, while parameter order does not matter. URL spaces must be encoded.

## Navigation

- [README.md](docs/PROJECT-README.md) — запуск, приёмка, вопросы и типичные ошибки;
- [METHODICAL_GUIDE.md](METHODICAL_GUIDE.md) — сценарий 90-минутного занятия;
- [SPRING_CONCEPTS.md](SPRING_CONCEPTS.md) — разбор механизмов Spring MVC.
