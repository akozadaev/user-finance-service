SHELL := /bin/bash
MVNW := ./mvnw
COMPOSE := docker compose

.DEFAULT_GOAL := help

.PHONY: help clean compile test-compile test test-fast test-integration package package-fast run run-dev checkstyle infra-up infra-down infra-logs

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
	@printf "  make infra-up          Поднять PostgreSQL\n"
	@printf "  make infra-down        Остановить PostgreSQL\n"
	@printf "  make infra-logs        Показать логи PostgreSQL\n"

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
	$(COMPOSE) up -d

infra-down:
	$(COMPOSE) down

infra-logs:
	$(COMPOSE) logs -f postgres
