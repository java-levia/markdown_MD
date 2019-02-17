## SpringCloud服务保护框架Hystrix

1. SpringCloud服务响应时间过长导致的服务无法响应问题

   1. 由于SpringCloud默认的响应时间是1秒，超过这个时间会报TimeOut错误（控制台可能不报这个错误），这时需要在调用方（这个1秒的超时时间是调用方决定的）设置ribbon的TimeOut参数（SpringCloud默认开启ribbon），在yml配置文件中设置如下

      ```yaml
      server:
        port: 8100
      spring:
        application:
          name: OrderImpl
      eureka:
        client:
          service-url:
            defaultZone: http://localhost:8001/eureka,http://localhost:8002/eureka
          register-with-eureka: true
          fetch-registry: true
      ###设置feign客户端超时时间
      ###SpringCloud feign默认开启ribbon
      ribbon:
      ###指的是建立连接所用的时间，适用于网络状况正常的情况下，连段连接所用的时间
        ReadTimeout: 5000
      ###值得是建立连接后从服务器读取到可用资源所用的时间
        ConnectTimeout: 5000
      ```

      

2. Hystrix服务保护框架简介

   1. 在微服务中，Hystrix能够为我们解决哪些事情？

      * 断路器
      * 服务降级（在tomcat中没有线程用于处理客户请求的时候，不应该让软件界面一直转圈等待）
        - [ ] 在高并发情况下，防止用户一直等待，使用服务降级方式（返回一个友好的提示给客户端，不会去处理请求，调用FallBack本地方法）
        - [ ] 服务降级的目的是为了提高用户体验
      * 服务雪崩效应   连环雪崩效应==》如果比较严重的话，可能会导致整个微服务接口无法访问
      * 服务熔断
      * 服务隔离机制

      