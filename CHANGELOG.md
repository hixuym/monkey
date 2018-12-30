## Monkey Framework

###3.4.0 - 2018-12-27

    - add usefull httphandlers
    - add orm repository support
    - environment for guice configure, and commit before server start
    - fix jaxrs validation issue, good intergate resteasy
    - add server extra configuration items, include http2
    - add monkey-grpc and monkey-jaxrs-auth init module

###3.2.0 - 2018-12-25

    - monkey-serv-http use undertow-core
    - monkey-jaxrs bundle integrate resteasy
    - monkey-grpc init project
    - Bootstrap for configure guice modules, Enviroment ready use Injector.

###3.1.0 - 2018-12-23

    - monkey-ssl
    - use Conscrypt
    - ddd support
    - use guava cache instead of caffeine
    - rename module and packages
    - add docs

###3.0.0.Final - 2018-12-21

    - use HikariCP pool.
    - delete or merge unused modules.
    - upgrade thirdparty deps.
    - add monkey-jdbi3 module.

###2.1.0 - 2017-11-12

    - upgrade io.ebean.tile:enhancement:4.3 work with ebean 11.4.1
    - ebean classpath scanner not work

###2.0.0 - 2017-11-11

    - remove monkey-undertow module, framework just have fake server
    - remove monkey-myabtis module, recommend monkey-ebean orm 
    - add monkey-resteasy module as resteasy extension
    - add monkey-resteasy-netty module as default runtime
    - upgrade ebean from 11.3.1 to 11.4.1
  
###1.5.0 - 20170-11-03

    - undertow module configure method renamed
    - cleanup code

###1.4.2 - 2017-11-02

    - upgrade ebean 11.3.1
    - add support for ServiceUnavailableRetryStrategy
    - add module processor
    - cleanup code. 

###1.4.1 - 2017-10-29

    - upgrade ebean to 11.2.3
    - upgrade jackson to 2.9.2
    - cleanup code

###1.4.0 - 2017-10-28

    - add mode to monkey-guice
    - serverFactory add getMode and add AbstractServerFactory for code reuse

###1.3.1 - 2017-10-28

    - add some javadoc
    - reformat code using idea default style.

###1.3.0 - 2017-10-27

  - add injector processor
  - rename monkey-guicey to monkey-guice
  
###1.2.0 - 2017-10-21
  
  - add monkey basic archetypes
  - add ValidatorFactory to Environment instead of Validator
  - guice created use production stage
  - add javadoc to configuration
  
###1.1.0 - 2017-10-18

  - monkey-sec move to monkey-undertow
  - monkey-undertow become generic http Server

###1.0.0 - 2017-10-15

 - Initial release
