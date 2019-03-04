## SpringCloud Config分布式配置中心

1. 为什么要使用分布式配置中心
   * 在微服务中，如果使用传统的方式管理配置文件，配置文件管理起来非常复杂。因为一套微服务架构可能有数十个配置文件，如果每个配置文件都放置在各个服务的Resource文件夹下，在需要对配置文件做更改的时候会很费时间，分布式配置中心就是一个将配置文件集中到一起进行管理的一个框架。
   * 在将配置文件放在Resource文件夹下这种传统的方式中，更改了配置文件之后需要重新打war包，然后重新部署之后才能将配置文件的内容读取到jvm中
   * 使用分布式配置中心，可以在微服务中使用同一个服务器管理所有服务配置文件信息，能够通过一个后台管理所有的这些配置文件 。当服务器正在运行的时候，如果配置文件需要改变，可以在不重启服务器的情况下，通过这个微服务后台实时更改配置文件的信息
2. 目前有哪些常见的分布式配置中心框架
   * apollo 阿波罗  携程写的分布式配置中心，可以使用图形界面管理配置文件的信息，配置文件信息是存放在数据库里的
   * SpringCloudConfig  没有图形界面用于管理配置文件，配置文件信息存放在版本控制器里
   * 使用Zookeeper实现分布式配置中心，持久节点+事件通知
3. SpringCloud配置中心原理
   1. 分布式配置中心需要哪些组件
      * WEB管理系统 -- 后台可以使用图形界面管理配置文件（SpringCloud没有图形化界面用以管理配置文件）
      * 存放分布式配置文件的服务器（持久化存储服务器）-- 使用版本控制器存放配置文件信息（一般使用git）
      * ConfigServer缓存配置文件服务器（临时缓存，读取到内存中）（为什么需要设计ConfigServer缓存：目的是缓存git上的配置文件信息，如果每次都要到git上去拉取配置文件信息，效率太低）
      * ConfigClient读取ConfigServer配置文件信息

4. Apollo配置中心实例

   * 阿波罗配置中心整合非常简单，只需要在应用apollo配置的微服务中引入apollo的jar

     ```xml
     <!--导入Apollo的包-->
                 <dependency>
                     <groupId>com.ctrip.framework.apollo</groupId>
                     <artifactId>apollo-client</artifactId>
                     <version>1.1.0</version>
                 </dependency>
     ```

   * 然后在配置文件中引入apollo配置中心的相关配置

     ```yaml
     
     app:
     //apollo配置中心配置的应用id
       id: service-zuul
     apollo:
     //apollo客户端的ip地址
       meta: http://192.168.0.129:8080
     ```

   * 然后在启动类上加上@EnableApolloConfig注解  就可以获取apollo上的配置了(目前使用的版本只支持properties的配置方式，所以需要配置成 zuul.routes. api-a.path = /api-member/** 这种方式)

