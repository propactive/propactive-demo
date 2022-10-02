# NOTICE ############################################################
# The MAKE recipes for propactive-demo should be as platform
# independent as possible to allow users run the app locally
# as long as they have GNU [make][1] & [docker][2] installed...
#
# [1]: https://www.gnu.org/software/make/manual/make.html
# [2]: https://docs.docker.com/
#
# MAKE VARS #########################################################

APP_NAME=propactive-demo

# Required App Vars:
VERSION?=DEV-SNAPSHOT
ENVIRONMENT?=dev

# Docker user information (Current user's UID)
# This avoids running as ROOT when the user is not ROOT...
DOCKER_USER_UID=`id -u`
DOCKER_USER_GID=`id -g`

# Volumes to mount:
HOST_PROJECT_DIR:=$(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))
GRADLE_DATA?=$(HOME)/.gradle
MAVEN_DATA?=$(HOME)/.m2

# Propactive
PROPACTIVE_PROPERTIES_ENV_TO_GENERATE?=$(ENVIRONMENT)
PROPACTIVE_PROPERTIES_DIRECTORY?=properties
PROPACTIVE_PROPERTIES_LOCATION?=./build/$(PROPACTIVE_PROPERTIES_DIRECTORY)
PROPACTIVE_PROPERTIES_FILENAME?="application.properties"

# App publishing details:
DOCKER_REGISTRY_URL?=propactive
IMAGE=$(DOCKER_REGISTRY_URL)/$(APP_NAME)
APP_IMAGE=$(IMAGE):$(VERSION)-$(ENVIRONMENT)
GIT_REPOSITORY?=`git config --get remote.origin.url`
GIT_BRANCH?=`git rev-parse --abbrev-ref HEAD`
GIT_COMMIT?=`git rev-parse HEAD`

# Toolchain image details:
# Liberica: https://hub.docker.com/r/bellsoft/liberica-openjdk-alpine
TOOLCHAIN_VERSION?=17
TOOLCHAIN_IMAGE=bellsoft/liberica-openjdk-alpine:$(TOOLCHAIN_VERSION)
TOOLCHAIN_CONTAINER_WORKING_DIR=/build/$(APP_NAME)

# App image details:
APP_CONTAINER_WORKING_DIR=/app
APP_IMAGE_SAVE_LOCATION?=./build/images

# RECIPES ###########################################################

test-app:
	@echo "## Testing the application ..."
	$(call toolchain_runner, ./gradlew test --info)

lint:
	@echo "## Ktlint check ..."
	$(call toolchain_runner, ./gradlew ktCh --continue)

build-app:
	@echo "## Building the application ..."
	$(call toolchain_runner, ./gradlew build -x test --info)

build-app-properties:
	@echo "## Building the application properties for \"$(ENVIRONMENT)\" ..."
	$(call toolchain_runner, ./gradlew generateApplicationProperties --info \
		-Penvironments=$(PROPACTIVE_PROPERTIES_ENV_TO_GENERATE) \
		-Pdestination=$(PROPACTIVE_PROPERTIES_DIRECTORY) \
		-PfilenameOverride=$(PROPACTIVE_PROPERTIES_FILENAME))

build-image:
	@echo "## Building the image as described in Dockerfile ..."
	docker build --file=Dockerfile \
    	--rm \
    	--build-arg git_repository=$(GIT_REPOSITORY) \
    	--build-arg git_branch=$(GIT_BRANCH) \
    	--build-arg git_commit=$(GIT_COMMIT) \
    	--build-arg version=$(VERSION) \
    	--build-arg properties=$(PROPACTIVE_PROPERTIES_LOCATION) \
    	-t $(APP_IMAGE) .

save-image:
	@echo "## Saving latest image generated ..."
	mkdir -p $(APP_IMAGE_SAVE_LOCATION) && \
	docker save -o $(APP_IMAGE_SAVE_LOCATION)/$(APP_NAME)-$(VERSION)-$(ENVIRONMENT).tar $(APP_IMAGE)

# This Recipe banner is suppressed during
run-image:
	@echo "## Summary for \`$(ENVIRONMENT)\` environment"
	docker run --rm --net host --name $(APP_NAME)-$(VERSION)-local-run \
		$(CONTAINER_USER) \
		$(APP_REQUIRED_ENV) \
		-w $(APP_CONTAINER_WORKING_DIR) \
		$(APP_IMAGE)

# FUNCITONS #########################################################

# Toolchain steps
#  1. Remove any existing/lingering toolchain runner containers
#  2. Pull the toolchain image
#  3. Run the container with set variables, volumes, toolchain image,
#     and command given. (Note that the toolchain is mounted to project
#     directory so any output generated will be written there...)
#
#  Usage example:
#    $(call toolchain_runner, ./gradlew build -x test --info)
#    $(call toolchain_runner, ./gradlew tasks)
#
#  See: https://www.gnu.org/software/make/manual/html_node/Call-Function.html
define toolchain_runner
	if [ -z "$(CI)" ]; then \
		(docker rm -f $(APP_NAME)-$(GIT_BRANCH)-$(VERSION)-toolchain-run || true) && \
		docker pull $(TOOLCHAIN_IMAGE) && \
		docker run --rm --net host --name $(APP_NAME)-$(GIT_BRANCH)-$(VERSION)-toolchain-run \
			$(CONTAINER_USER) \
			$(APP_REQUIRED_ENV) \
			$(TOOLCHAIN_CONTAINER_VOLUMES) \
			$(TOOLCHAIN_CONTAINER_ENV) \
			-w $(TOOLCHAIN_CONTAINER_WORKING_DIR) \
			$(TOOLCHAIN_IMAGE) \
			$(1); \
    else \
		echo "Toolchain runner is disabled as we are running on CI, which relies on it's own VM..."; \
		$(1); \
	fi
endef

# CONTAINER USER ####################################################

define CONTAINER_USER
	--user $(DOCKER_USER_UID):$(DOCKER_USER_GID)
endef

# TOOLCHAIN CONTAINER VOLUMES #################################################

# HOST_PROJECT_DIR:rw - persist build output
# GRADLE_DATA:rw      - cache
# MAVEN_DATA:rw       - cache
# docker.sock         - The UNIX socket that Docker daemon is listening to.
#                       It's the main entry point for Docker API. We need it as our CI runs
#                       within a docker container, and our toolchain is a docker container.
#                       (i.e. docker in docker, see: https://stackoverflow.com/a/35110344 , https://jpetazzo.github.io/2015/09/03/do-not-use-docker-in-docker-for-ci/)
define TOOLCHAIN_CONTAINER_VOLUMES
	-v $(HOST_PROJECT_DIR):$(TOOLCHAIN_CONTAINER_WORKING_DIR):rw \
	-v $(GRADLE_DATA):/build/.gradle:rw \
	-v $(MAVEN_DATA):/build/.m2:rw \
	-v /var/run/docker.sock:/var/run/docker.sock
endef

# APP REQUIRED ENV VARIABLES #####################################################

define APP_REQUIRED_ENV
	-e VERSION=$(VERSION) \
	-e ENVIRONMENT=$(ENVIRONMENT)
endef

# TOOLCHAIN CONTAINER VARIABLES ###############################################

# GRADLE_USER_HOME
#   Override Gradle's user's home variable as it defaults to user's
#   home ($USER_HOME/.gradle). See: https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_environment_variables
define TOOLCHAIN_CONTAINER_ENV
	-e GRADLE_USER_HOME=/build/.gradle
endef
