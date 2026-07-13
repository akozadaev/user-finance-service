SHELL := /bin/bash
MVNW := ./mvnw
COMPOSE := docker compose

.DEFAULT_GOAL := help

.PHONY: help clean compile test-compile test test-fast test-integration package package-fast run run-dev checkstyle infra-up infra-down infra-logs docker-up docker-down docker-logs

help:
	@printf "Доступные цели:\n"
	@printf "  make clean             Удалить артефакты сборки Maven\n"
	@printf "  make compile           Компилировать приложение\n"
	@printf "  make test-compile      Компилировать приложение и тесты\n"
	@printf "  make test              Запустить unit-тесты\n"
	@printf "  make test-integration  Запустить Testcontainers-тесты\n"
	@printf "  make package           Собрать jar с тестами\n"
	@printf "  make package-fast      Собрать jar без тестов\n"
	@printf "  make run               Запустить приложение\n"
	@printf "  make checkstyle        Проверить стиль кода\n"
	@printf "  make docker-up         Собрать и запустить весь стенд\n"
	@printf "  make docker-down       Остановить весь стенд\n"
	@printf "  make docker-logs       Показать логи стенда\n"
	@printf "  make infra-up          Поднять БД и OAuth2 для локального Java\n"
	@printf "  make infra-down        Остановить инфраструктуру\n"
	@printf "  make infra-logs        Показать логи инфраструктуры\n"

clean:
	$(MVNW) clean

compile:
	$(MVNW) -DskipTests compile

test-compile:
	$(MVNW) -DskipTests test-compile

test:
	$(MVNW) test

test-fast: test

test-integration:
	$(MVNW) -DexcludedGroups= -Dgroups=integration test

package:
	$(MVNW) package

package-fast:
	$(MVNW) -DskipTests package

run:
	$(MVNW) spring-boot:run

run-dev: run

checkstyle:
	$(MVNW) checkstyle:check

infra-up:
	$(COMPOSE) up -d --build finance-postgres oauth-postgres oauth2-server oauth-client-init

infra-down:
	$(COMPOSE) down

infra-logs:
	$(COMPOSE) logs -f finance-postgres oauth-postgres oauth2-server oauth-client-init

docker-up:
	$(COMPOSE) up -d --build

docker-down:
	$(COMPOSE) down

docker-logs:
	$(COMPOSE) logs -f
