# User Finance Service

REST-сервис для поиска пользователей, управления собственными email/телефонами и денежных переводов. Реализован на Java 17 и Spring Boot в трех слоях: controller (API), service, repository (DAO).

## Запуск

Требования: Java 17+, Docker с Compose.

```bash
make infra-up
make run
```

Swagger UI: `http://localhost:8080/swagger-ui.html`.

JWT выдаёт внешний [go_oauth2_server](https://github.com/akozadaev/go_oauth2_server), который следует запустить отдельно на порту 8081.
Адрес интроспекции настраивается переменной `OAUTH2_INTROSPECTION_URI` (по умолчанию `http://localhost:8081/introspect`).
Получение access token выполняется на OAuth2-сервере:

```bash
curl -X POST http://localhost:8081/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password&username=ivan@example.com&password=password&client_id=CLIENT_ID&client_secret=CLIENT_SECRET'
```

Для входа по телефону на OAuth2-сервере регистрируется второй OAuth-пользователь с телефоном в качестве `username`; оба внешних субъекта связываются с одним финансовым пользователем. Полученный токен передается как `Authorization: Bearer <token>`.

OAuth2-сервер кладет UUID пользователя в `sub`, а интроспекция возвращает его в `user_id`. Таблица `oauth_identity` связывает один или несколько таких UUID с локальным `users.id`. В демонстрационных данных стоят UUID-заглушки; после регистрации OAuth-пользователей их следует заменить фактическими значениями, возвращенными `POST /users`, например: `UPDATE oauth_identity SET subject = '<uuid>' WHERE id = 1`.

## API

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

Использованы Spring Web, Validation, Data JPA, Security, Caffeine, JJWT, Liquibase, springdoc-openapi и Testcontainers.
