IMAGE=spring-boot-template-service
IMAGE_DEV=${IMAGE}-dev
REGISTRY=template
TAG=latest
COMPOSE=docker compose -f ./.development/docker-compose.yml -p spring-boot-template-service

db:
	$(COMPOSE) up database -d

logs:
	${COMPOSE} logs -f --tail=450 app

stop:
	docker kill ${IMAGE}

clean:
	$(COMPOSE) down
	docker ps -a | awk '{ print $1,$2 }' | grep ${IMAGE} | awk '{ print$1 }' | xargs -I {} docker rm -vf {}
	docker rmi -f $(REGISTRY)/$(IMAGE):$(TAG); docker rmi -f $(IMAGE_DEV):$(TAG)

run: --docker-build
	docker build --rm --target runner -t $(REGISTRY)/$(IMAGE):$(TAG) .
	$(COMPOSE) up --force-recreate

build: --docker-build
	docker run --rm --name $(IMAGE_DEV) $(IMAGE_DEV):$(TAG) gradle assemble

--docker-build:
	docker build --rm --target builder -t $(IMAGE_DEV):$(TAG) .