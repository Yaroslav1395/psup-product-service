# PSUP-PRODUCT-SERVICE

**PSUP-PRODUCT-SERVICE** — это микросервис для взаимодействия с каталогом продукции. Является частью кластера сервисов универсальной платформы по дистрибуции товаров.

---

## 🔧 Возможности

- ✅ Реактивная архитектура
- ✅ Запуск без контейнера в режиме standalone (с базой данных H2)
- ✅ Запуск в контейнере с базой postgres через профиль `local`. Kafka должна быть запущена
- ✅ Запуск в контейнере с базой postgres через профиль `test`. Вся инфраструктура должна быть запущена (Kafka, Config, Eureka и т.д.)
- ✅ Приложение запускается на порте **8080**
- ✅ Настроен circuit breaker при взаимодействии с другими сервисами. Проверять события по http://localhost:8080/actuator/circuitbreakerevents
- ✅ WebClient работает в режиме балансировки нагрузки если сервис зарегистрирован в Eureka
- ✅ Настроена трассировка запросов. Смотреть по http://localhost:9411/
- ✅ Настроено логирование. Перед просмотром добавить Discover c индексом filebeat-*. Смотреть по http://localhost:5601/
- ✅ Настроена kafka. Топики можно смотреть по http://localhost:9090/

---

## 🛠️ Используемые технологии

| Технология                            | Назначение                                            |
|---------------------------------------|-------------------------------------------------------|
| **Spring WebFlux**                    | Реактивный веб-фреймворк                              |
| **Spring Data R2DBC**                 | Асинхронная работа с БД                               |
| **R2DBC PostgreSQL**                  | Драйвер для реактивного PostgreSQL                    |
| **JDBC**                              | Синхронные миграции с Liquibase                       |
| **PostgreSQL**                        | Боевая и тестовая база данных                         |
| **Liquibase Core**                    | Управление схемой БД                                  |
| **H2**                                | Встраиваемая база данных для локального запуска       |
| **Netflix Eureka Client**             | Для регистрации сервиса                               |
| **Spring Cloud Config Client**        | Для получение конфигурации запуска                    |
| **Spring Cloud Starter Bootstrap**    | Для запуска первоначального этапа конфигурации        |
| **Spring Retry**                      | Для обеспечения повторных попыток запроса при ошибках |
| **Resilience4j Reactor**              | Защита от избыточных запросов на сломанный сервис     |
| **Spring Boot Starter AOP**           | Логирование, транзакции, retry через аннотации        |
| **Actuator**                          | Для контроля состояния сервиса                        |
| **Zipkin Reporter Brave**             | Для отправки трассировки в Zipkin сервер              |
| **Micrometer Tracing Bridge Brave**   | Адаптер трассировок. Мост для Brave с Micrometer      |
| **Logstash Logback Encoder**          | Для преобразования логов в json формат для ELK        |
| **Spring Kafka**                      | Для работы с Kafka                                    |
| **Docker Mvn Plugin**                 | Для создания образа в docker                          |

---

## 🧪 Профили конфигурации

| Профиль                  | Назначение                                                                            | База данных |
|--------------------------|---------------------------------------------------------------------------------------|-------------|
| `application.yaml`       | Локальный запуск без Docker                                                           | H2          |
| `application-local.yaml` | Локальный запуск в Docker (`docker-compose`). Kafka должна быть доступна.             | PostgreSQL  |
| `application-test.yaml`  | Локальный запуск в Docker (`docker-compose`). Вся инфраструктура должна быть доступна | PostgreSQL  |

---

## 🗄️ Доступ к Базе данных (тестовая среда)

- **Пользователь**: `admin`
- **Пароль**: `admin`

---

## 🚀 Запуск

### 🔹 Локально (без Docker c H2)

```bash
./mvnw spring-boot:run
```

### 🔹 Локально (Docker c Postgres)
Для очистки старых образов в docker:
```bash
docker image prune -f
```
Для сборки образа
```bash
mvn clean package -DskipTests docker:build
```
Для запуска контейнера
```bash
docker-compose -f docker-compose-local.yaml up --build
```

### 

### 🛠🔹 Запуск приложения локально с профилем test (без докера). Запуск локально в докере (через compose). Запуск в тестовой среде. При любом запуске вся инфраструктура должна быть доступна.
Для очистки старых образов в docker:
```bash
docker image prune -f
```
Для сборки образа
```bash
mvn clean package -DskipTests docker:build
```
Для создания общей сети внутри докер 
```bash
if (-not (docker network inspect psup-shared-net -ErrorAction SilentlyContinue)) {docker network create psup-shared-net}
```
Для запуска контейнера
```bash
docker-compose -f docker-compose-test.yaml up --build
```