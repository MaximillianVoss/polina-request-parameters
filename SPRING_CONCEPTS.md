# Spring-концепции на реальном проекте

Ниже приведены фрагменты, соответствующие текущему проекту `com.example.requestparameters`. Для каждого элемента указаны назначение, частая ошибка и короткое модельное объяснение для защиты.

## Java: класс, метод, параметры и конструктор

```java
public class HelloController {
    public String hello(String location, String name) { /* ... */ }
}
```

**Назначение.** Класс группирует поведение; публичный метод выполняет действие; параметры — входные значения метода. У `HelloController` явный конструктор отсутствует: Java создаёт неявный конструктор без аргументов. Spring создаёт экземпляр контроллера как bean.

**Типичная ошибка.** Сделать контроллер или метод непубличным, хотя эталон и договорённость проекта требуют `public`, либо пытаться вручную создавать контроллер через `new`.

**Модельное объяснение.** «Класс — контейнер для endpoint, метод — обработчик, параметры — значения запроса. Контроллером управляет Spring, поэтому я не создаю его вручную».

## Точка входа и `@SpringBootApplication`

```java
package com.example.requestparameters;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RequestParametersApplication {
    // SpringApplication.run(RequestParametersApplication.class, args)
}
```

**Назначение.** `@SpringBootApplication` объединяет конфигурацию, автоконфигурацию и сканирование компонентов. Пакет основного класса охватывает `HelloController`, поэтому Spring его находит.

**Типичная ошибка.** Поместить контроллер в пакет вне области сканирования или убрать аннотацию приложения; тогда endpoint не будет зарегистрирован.

**Модельное объяснение.** «Этот класс запускает Spring Boot и задаёт корневой пакет, из которого Spring находит контроллеры».

## Maven, `pom.xml` и Maven Wrapper

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.16</version>
</parent>
<properties><java.version>17</java.version></properties>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**Назначение.** `pom.xml` описывает версию Java и зависимости. Web starter приносит Spring MVC и встроенный Tomcat. Файлы `mvnw`/`mvnw.cmd` — Maven Wrapper: он запускает согласованную Maven-среду командой `./mvnw test` или `mvnw.cmd test`.

**Типичная ошибка.** Запускать проект на другой версии Java или удалить Web starter, ожидая, что HTTP endpoint продолжит работать.

**Модельное объяснение.** «Maven читает `pom.xml`, а Wrapper даёт воспроизводимый способ собрать и запустить проект без отдельной установки Maven».

## Embedded Tomcat и HTTP

```text
GET /hello?location=moon&name=John HTTP/1.1
Host: localhost:8080
```

**Назначение.** После запуска Spring Boot встроенный Tomcat принимает HTTP на порту 8080 и передаёт запрос Spring MVC. `GET` — HTTP-метод; `/hello` — путь; строка после `?` — query string. Успешный ответ имеет статус `200 OK` и body `Welcome to the moon John!`.

**Типичная ошибка.** Считать query string частью пути, не запустить приложение из-за занятого 8080 или ждать 200 при неполном запросе.

**Модельное объяснение.** «Tomcat принимает HTTP, Spring выбирает обработчик по GET и пути, затем возвращает текст со статусом 200».

## Контроллер и class-level `@ResponseBody`

```java
@Controller
@ResponseBody
public class HelloController {
    // методы этого контроллера возвращают тело HTTP-ответа
}
```

**Назначение.** `@Controller` регистрирует MVC-компонент. `@ResponseBody` находится на классе и предписывает записывать результат его методов прямо в HTTP body. На `hello` эта аннотация не дублируется.

**Типичная ошибка.** Поставить `@ResponseBody` только в старом фрагменте на метод, забыть её на классе либо ожидать, что строка станет текстовым ответом без неё.

**Модельное объяснение.** «Контроллер принимает HTTP, а class-level `@ResponseBody` превращает возвращаемую строку в body, а не в имя представления».

## `@GetMapping` и связь с `@RequestMapping`

```java
@GetMapping("/hello")
public String hello(/* параметры */) { /* ... */ }
```

**Назначение.** `@GetMapping("/hello")` — сокращённая специализированная форма `@RequestMapping(path = "/hello", method = RequestMethod.GET)`. Она связывает только GET-запросы пути `/hello` с методом.

**Типичная ошибка.** Написать `/Hello`, `/hello/anything` или ожидать, что POST автоматически попадёт в тот же endpoint.

**Модельное объяснение.** «`@GetMapping` — читаемая запись маршрута: GET плюс путь `/hello`; общий механизм за ней — `@RequestMapping`».

## Query string и `@RequestParam`

```java
@GetMapping("/hello")
public String hello(
        @RequestParam(name = "location") String location,
        @RequestParam(name = "name") String name) {
    return "Welcome to the " + location + " " + name + "!";
}
```

**Назначение.** `@RequestParam` связывает пары после `?` с параметрами Java-метода. `name` фиксирует внешний ключ. По умолчанию `required = true`, поэтому отсутствие `location` или `name` заканчивается `400 Bad Request` до обычного ответа. Конкатенация `+` формирует точное body; пробелы в строковых литералах намеренны.

**Типичная ошибка.** Опечататься в ключе, изменить регистр (`Name` вместо `name`), не поставить `&`, убрать `@RequestParam` или передать незакодированный пробел.

**Модельное объяснение.** «Spring ищет значения по именам, поэтому порядок не важен. Но оба именованных параметра обязательны; пробелы в URL передаются как `%20` или `+`».

### Матрица binding

| Запрос | `location` | `name` | Результат |
| --- | --- | --- | --- |
| `/hello?location=moon&name=John` | `moon` | `John` | 200, `Welcome to the moon John!` |
| `/hello?name=John&location=moon` | `moon` | `John` | 200, тот же текст |
| `/hello?location=New%20York&name=Mary%20Jane` | `New York` | `Mary Jane` | 200, декодированные значения |
| `/hello?name=John` | отсутствует | `John` | 400 до обычного ответа |

## JUnit и MockMvc

```java
@WebMvcTest(HelloController.class)
class HelloControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void welcomesJohnToTheMoon() throws Exception {
        mockMvc.perform(get("/hello").param("location", "moon").param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome to the moon John!"));
    }
}
```

**Назначение.** JUnit запускает тесты, `@WebMvcTest` поднимает MVC-срез, MockMvc имитирует запрос без реального TCP-порта. `status().isOk()` проверяет 200, `content().string(...)` — точное body. Отдельный `contextLoads()` проверяет создание полного Spring-контекста.

**Типичная ошибка.** Проверить только статус, но не тело; ожидать 200 при пропуске обязательного параметра; путать MockMvc с браузером или настоящим сервером.

**Модельное объяснение.** «MockMvc быстро проверяет MVC-контракт в памяти, а `contextLoads()` отдельно доказывает, что весь контекст приложения поднимается».

## Мини-упражнения с ответами

1. **Задание:** назовите путь в `/hello?location=moon&name=John`. **Ответ:** `/hello`.
2. **Задание:** что отделяет `?`. **Ответ:** путь от query string.
3. **Задание:** какой статус у `/hello?location=moon`? **Ответ:** 400, отсутствует обязательный `name`.
4. **Задание:** изменится ли ответ при `name=John&location=moon`? **Ответ:** нет, связывание выполняется по именам.
5. **Задание:** как безопасно передать `New York` в URL? **Ответ:** `New%20York` или `New+York` в query string.
6. **Задание:** где находится `@ResponseBody` в эталоне? **Ответ:** на `public class HelloController`, поэтому на методе она не требуется.
