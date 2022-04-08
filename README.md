## Getting started

First you can read the accessible documentation
https://ulegalize.atlassian.net/wiki/spaces/UC/pages/793378817/Open+source

Don't hesitate to create PR in order to change something in the project

### Workspace

change `gradle.properties` version  
commit your change add a new tag and push all

```
git tag 2.0.3
git push origin 2.0.3

```

#### DEV

run gradle to deploy

#### PROD

tag and push execute to build image change `gradle.properties` version

```
gradle clean  
gradle bootJar   
```

#### docker compose

change `docker-compose.yml` file with the right version

```
cd ~
vi docker-compose.yml  
docker stop debian_ulegalize-reports_1  
docker rm debian_ulegalize-reports_1  
docker rmi $(docker images finauxa/ulegalize-reports -q)    
docker-compose up -d

```

`
docker rmi debian_ulegalize-reports_1:1.4.9  
`
##### Environment

#### prod

`
docker run --name ulegalize-reports --restart always -p 127.0.0.1:5555:5555 -it finauxa/ulegalize-reports:2.0.3 --spring.profiles.active=prod --server.use-forward-headers=true
`
`
docker run --name ulegalize-reports --restart always -p 127.0.0.1:5555:5555 -it finauxa/ulegalize-reports:2.0.3 --spring.profiles.active=prod --server.use-forward-headers=true
`

#### test

`
docker run --name ulegalize-reports --net="host" --restart always -p 127.0.0.1:5555:5555 -it finauxa/ulegalize-reports:2.0.3 --spring.profiles.active=test --server.use-forward-headers=true
`

#### dev

`
docker run --name ulegalize-reports --restart always -p 127.0.0.1:5555:5555 -it finauxa/ulegalize-reports:2.0.3 --spring.profiles.active=devDocker --server.use-forward-headers=true
`

## more info

https://ulegalize.atlassian.net/l/c/AY0FkzHX

#### docker engine

build the app

```
docker build -t finauxa/ulegalize-reports:2.0.3 .  
docker push finauxa/ulegalize-reports:2.0.3  
```

```
docker stop ulegalize-reports   
docker rm ulegalize-reports  
docker rmi $(docker images finauxa/ulegalize-reports -q)  
docker pull finauxa/ulegalize-reports:2.0.3  
```

## mysql 5.6

cd /Applications/MAMP/bin/ ./stopMysql.sh  
brew install mysql@5.6  
brew services stop mysql@5.6  
brew services start mysql@5.6

Enjoy