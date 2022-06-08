# REQS:
# Build a bare minimum -
# - Recipe: Image build
# - Recipe: Publish build
# - Recipe: App test
# - Dockerized deployment

# MAKE VARS ######################################################

# Meta information of app build:
APP_NAME=propactive-demo
VERSION?=DEV-SNAPSHOT
ENVIRONMENT?=dev

GIT_REPOSITORY?=`git config --get remote.origin.url`
GIT_BRANCH?=`git rev-parse --abbrev-ref HEAD`
GIT_COMMIT?=`git rev-parse HEAD`

# Volumes to mount:
GRADLE_DATA?=$(HOME)/.gradle
MAVEN_DATA?=$(HOME)/.m2
HOST_PROJECT_DIR:=$(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))

# Publishing vars:
DOCKER_REGISTRY_URL?=uroads
IMAGE=$(DOCKER_REGISTRY_URL)/$(APP_NAME)

# Toolchain image details:
# Liberica: https://hub.docker.com/r/bellsoft/liberica-openjdk-alpine
TOOLCHAIN_VERSION?=17
TOOLCHAIN_IMAGE=bellsoft/liberica-openjdk-alpine:$(TOOLCHAIN_VERSION)
TOOLCHAIN_CONTAINER_WORKING_DIR=/build/$(APP_NAME)

# RECIPES ######################################################

build-app:
	@echo "******** Building the application... ********"
	$(call toolchain_runner, "./gradlew clean build --info")

build-image:
	@echo "******** Building the image... ********"
	$(call image_builder)

publish-jars:
	@echo "******** Pushing build artifacts... ********"
	$(call toolchain_runner, "./gradlew publishToMavenLocal --info")

push-image:
	@echo "******** Pushing the image... ********"
	docker push $(IMAGE):$(VERSION)
	docker rmi -f $(IMAGE):$(VERSION)

# FUNCITONS ####################################################

# Toolchain steps
#  1. Remove any existing/lingering toolchain runner containers
#  2. Pull the toolchain image
#  3. Run a container named "$(APP_NAME)-$(GIT_BRANCH)-$(VERSION)-toolchain-run",
#     set the container's variables, volumes, toolchain image to use, and
#     any extra commands can be passed through $(1).
define toolchain_runner
    (docker rm -f $(APP_NAME)-$(GIT_BRANCH)-$(VERSION)-toolchain-run || true) && \
    docker pull $(TOOLCHAIN_IMAGE) && \
	docker run --rm --net host --name $(APP_NAME)-$(GIT_BRANCH)-$(VERSION)-toolchain-run \
		$(TOOLCHAIN_CONTAINER_ENV) \
		$(TOOLCHAIN_CONTAINER_VOLUMES) \
		-w $(TOOLCHAIN_CONTAINER_WORKING_DIR) \
		$(TOOLCHAIN_IMAGE) \
		$(1)
endef

# Use the DockerFile to build an image of the given application as described in Dockerfile
# This will a
define image_builder
	docker build --file=Dockerfile \
	--rm \
	--build-arg git_repository=$(GIT_REPOSITORY) \
	--build-arg git_branch=$(GIT_BRANCH) \
	--build-arg git_commit=$(GIT_COMMIT) \
	--build-arg version=$(VERSION) \
	-t $(IMAGE):$(VERSION) .
endef

# CONTAINER VARIABLES ####################################################

define TOOLCHAIN_CONTAINER_ENV
	-e VERSION=$(VERSION) \
	-e ENVIRONMENT=$(ENVIRONMENT)
endef

# CONTAINER VOLUMES ####################################################

define TOOLCHAIN_CONTAINER_VOLUMES
	-v $(HOST_PROJECT_DIR):$(TOOLCHAIN_CONTAINER_WORKING_DIR):rw \
	-v $(GRADLE_DATA):/build/.gradle:rw \
	-v $(MAVEN_DATA):/build/.m2:rw \
	-v /var/run/docker.sock:/var/run/docker.sock
endef
