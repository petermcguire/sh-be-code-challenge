-include .env.$(or $(APP_ENV),dev)
export

mkfile_path := $(abspath $(lastword $(MAKEFILE_LIST)))
current_dir := $(notdir $(patsubst %/,%,$(dir $(mkfile_path))))

.PHONY: db.reset
db.reset: ## Resets the DB
	docker-compose down
	docker-compose up -d

.PHONY: run-tests
run-tests: ## Sets up and runs integration tests
	docker-compose up --no-start
	docker-compose stop db
	docker-compose stop api-server
	docker-compose start db
	POSTGRES_HOST=localhost POSTGRES_PORT=$(POSTGRES_PORT) POSTGRES_DB=$(POSTGRES_DB) \
		POSTGRES_USER=$(POSTGRES_USER) POSTGRES_PASSWORD=$(POSTGRES_PASSWORD) ./gradlew clean test

#################
# Docker targets
#################

.PHONY: docker.db-prompt
docker.db-prompt: ## Jumps into the Postgres DB psql prompt
	docker-compose exec db psql -U $(POSTGRES_USER) -d $(POSTGRES_DB)

help: ## Prints this help message
	@grep -h -E '^[a-zA-Z0-9\._-]+:.*?## .*$$' $(MAKEFILE_LIST) |\
		sort | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
.DEFAULT_GOAL := help