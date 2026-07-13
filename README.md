# User Finance Service

REST-сервис для поиска пользователей, управления собственными email/телефонами и денежных переводов. Реализован на Java 17 и Spring Boot в трех слоях: controller (API), service, repository (DAO).

## Запуск

Для полного запуска нужен только Docker с Compose. Java-сервис, обе базы PostgreSQL и OAuth2-сервер собираются и запускаются одной командой:

```bash
make docker-up
```

Swagger UI: `http://localhost:8080/swagger-ui.html`.
Логи: `make docker-logs`, остановка: `make docker-down`.

JWT выдаёт включённый в репозиторий [go_oauth2_server](https://github.com/akozadaev/go_oauth2_server). Compose запускает его на `http://localhost:8081` и автоматически создаёт OAuth-клиента. Отдельно скачивать и запускать его не нужно.
Адрес интроспекции настраивается переменной `OAUTH2_INTROSPECTION_URI` (по умолчанию `http://localhost:8081/introspect`).
После запуска токен получается через API финансового сервиса:

```bash
curl -X POST http://localhost:8080/api/v1/auth/token \
  -H 'Content-Type: application/json' \
  -d '{"login":"ivan@example.com","password":"password"}'
```

Для входа по телефону в `login` передается `79207865432`. При первом успешном запросе сервис автоматически регистрирует отсутствующий OAuth username, получает токен и связывает его `sub` с локальным пользователем. Полученный токен передается как `Authorization: Bearer <token>`.

OAuth2-сервер кладет UUID пользователя в `sub`, а интроспекция возвращает его в `user_id`. Таблица `oauth_identity` автоматически связывает один или несколько таких UUID с локальным `users.id`.

## API

- `POST /api/v1/auth/token` - аутентификация по email/phone и паролю;
- `GET /api/v1/users/{id}` - пользователь;
- `GET /api/v1/users?dateOfBirth=&phone=&name=&email=&page=&size=` - поиск;
- `POST|PUT|DELETE /api/v1/users/me/emails[/id]` - собственные email;
- `POST|PUT|DELETE /api/v1/users/me/phones[/id]` - собственные телефоны;
- `POST /api/v1/transfers` - перевод (`{"toUserId":2,"value":100.00}`).

## Решения

- PostgreSQL и Liquibase отвечают за схему и начальные данные; Hibernate только валидирует схему.
- Java-сервис не выпускает и не разбирает JWT самостоятельно: он проверяет access token через `/introspect` OAuth2-сервера и сопоставляет внешний `sub` с локальной записью `oauth_identity`.
- Caffeine кэширует чтение пользователя на 60 секунд. Изменение контактов, перевод и начисление процентов инвалидируют кэш.
- Перевод выполняется в одной транзакции. Оба счета блокируются `PESSIMISTIC_WRITE` в порядке `user_id`, поэтому параллельные переводы не теряют обновления и не образуют циклическую блокировку. Ограничение БД дополнительно запрещает отрицательный баланс.
- Каждые 30 секунд один SQL-запрос атомарно увеличивает балансы на 10%, но не выше 207% начального баланса.
- Поиск построен на JPA Specification: дата строго больше заданной, имя имеет префиксное совпадение, email и телефон - точное.

## Тесты и сборка

```bash
make test
make test-integration  # требуется Docker
make package
make checkstyle
```

Использованы Spring Web, Validation, Data JPA, Security, Caffeine, Liquibase, springdoc-openapi и Testcontainers.
