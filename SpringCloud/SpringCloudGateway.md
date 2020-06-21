# SpringCloudGateway

1. ## SpringCloudGateway特征

   > SpringCloud官方，对SpringCloud Gateway 特征介绍如下：
   >
   > （1）基于 Spring Framework 5，Project Reactor 和 Spring Boot 2.0
   >
   > （2）集成 Hystrix 断路器
   >
   > （3）集成 Spring Cloud DiscoveryClient
   >
   > （4）Predicates 和 Filters 作用于特定路由，易于编写的 Predicates 和 Filters
   >
   > （5）具备一些网关的高级功能：动态路由、限流、路径重写
   >
   > 从以上的特征来说，和Zuul的特征差别不大。SpringCloud Gateway和Zuul主要的区别，还是在底层的通信框架上。
   >
   > 简单说明一下上文中的三个术语：
   >
   > **（**1**）**Filter**（过滤器）**：
   >
   > 和Zuul的过滤器在概念上类似，可以使用它拦截和修改请求，并且对上游的响应，进行二次处理。过滤器为org.springframework.cloud.gateway.filter.GatewayFilter类的实例。
   >
   > （2）**Route**（路由）：
   >
   > 网关配置的基本组成模块，和Zuul的路由配置模块类似。一个**Route模块**由一个 ID，一个目标 URI，一组断言和一组过滤器定义。如果断言为真，则路由匹配，目标URI会被访问。
   >
   > **（**3**）**Predicate**（断言）**：
   >
   > 这是一个 Java 8 的 Predicate，可以使用它来匹配来自 HTTP 请求的任何内容，例如 headers 或参数。**断言的**输入类型是一个 ServerWebExchange。

2. SpringCloud Gateway 匹配规则

   > ​	Spring Cloud Gateway 的功能很强大，我们仅仅通过 Predicates 的设计就可以看出来，前面我们只是使用了 predicates 进行了简单的条件匹配，其实 Spring Cloud Gataway 帮我们内置了很多 Predicates 功能。
   >
   > ​	Spring Cloud Gateway 是通过 Spring WebFlux 的 HandlerMapping 做为底层支持来匹配到转发路由，Spring Cloud Gateway 内置了很多 Predicates 工厂，这些 Predicates 工厂通过不同的 HTTP 请求参数来匹配，多个 Predicates 工厂可以组合使用。

   1. Predicate 断言条件介绍

      > Predicate 来源于 Java 8，是 Java 8 中引入的一个函数，Predicate 接受一个输入参数，返回一个布尔值结果。该接口包含多种默认方法来将 Predicate 组合成其他复杂的逻辑（比如：与，或，非）。可以用于接口请求参数校验、判断新老数据是否有变化需要进行更新操作。
      >
      > 在 Spring Cloud Gateway 中 Spring 利用 Predicate 的特性实现了各种路由匹配规则，有通过 Header、请求参数等不同的条件来进行作为条件匹配到对应的路由。网上有一张图总结了 Spring Cloud 内置的几种 Predicate 的实现。
      > ![在这里插入图片描述](https://upload-images.jianshu.io/upload_images/19816137-bb046dbf19bee1b4.gif?imageMogr2/auto-orient/strip)
      > 说白了 Predicate 就是为了实现一组匹配规则，方便让请求过来找到对应的 Route 进行处理，接下来我们接下 Spring Cloud GateWay 内置几种 Predicate 的使用。

   2. 通过请求参数匹配

      **Query Route Predicate** 支持传入两个参数，一个是属性名一个为属性值，属性值可以是正则表达式。

      ```yaml
      server:
        port: 8080
      spring:
        application:
      ​    name: api-gateway
        cloud:
      ​    gateway:
      ​      routes:
      ​        -id: gateway-service
      ​          uri: https://www.baidu.com
      ​          order: 0
      ​          predicates:
      ​            -Query=smile
      
      #这样配置，只要请求中包含 smile 属性的参数即可匹配路由。使用 curl 测试，命令行输入:curl localhost:8080?smile=x&id=2,经过测试发现只要请求汇总带有 smile 参数即会匹配路由，不带 smile 参数则不会匹配。
      ```

      还可以将 Query 的值以键值对的方式进行配置，这样在请求过来时会对属性值和正则进行匹配，匹配上才会走路由。

      ```yaml
      server:
        port: 8080
      spring:
        application:
      ​    name: api-gateway
        cloud:
      ​    gateway:
      ​      routes:
      ​        -id: gateway-service
      ​          uri: https://www.baidu.com
      ​          order: 0
      ​          predicates:
      ​            -Query=keep, pu.
      #这样只要当请求中包含 keep 属性并且参数值是以 pu 开头的长度为三位的字符串才会进行匹配和路由。使用 curl 测试，命令行输入:curl localhost:8080?keep=pub,测试可以返回页面代码，将 keep 的属性值改为 pubx 再次访问就会报 404,证明路由需要匹配正则表达式才会进行路由。
      ```

   3. 通过 Cookie 属性匹配

      **Cookie Route Predicate** 可以接收两个参数，一个是 Cookie name ,一个是正则表达式，路由规则会通过获取对应的 Cookie name 值和正则表达式去匹配，如果匹配上就会执行路由，如果没有匹配上则不执行。

      ```yaml
      server:
        port: 8080
      spring:
        application:
      ​    name: api-gateway
        cloud:
      ​    gateway:
      ​      routes:
      ​        -id: gateway-service
      ​          uri: https://www.baidu.com
      ​          order: 0
      ​          predicates:
      ​            - Cookie=sessionId, test
      #使用 curl 测试，命令行输入:curl http://localhost:8080 --cookie "sessionId=test", 则会返回页面代码，如果去掉--cookie "sessionId=test"，后台汇报 404 错误。
      ```

   4. 通过 Header 属性匹配

      ​	**Header Route Predicate** 和 Cookie Route Predicate 一样，也是接收 2 个参数，一个 header 中属性名称和一个正则表达式，这个属性值和正则表达式匹配则执行。

      ```yaml
      server:
        port: 8080
      spring:
        application:
      ​    name: api-gateway
        cloud:
      ​    gateway:
      ​      routes:
      ​        -id: gateway-service
      ​          uri: https://www.baidu.com
      ​          order: 0
      ​          predicates:
      ​            - Header=X-Request-Id, \d+
      #使用 curl 测试，命令行输入:curl http://localhost:8080 -H "X-Request-Id:88",则返回页面代码证明匹配成功。将参数-H "X-Request-Id:88"改为-H "X-Request-Id:spring"再次执行时返回404证明没有匹配。
      ```

   5. 通过 Host 匹配

      ​	**Host Route Predicate** 接收一组参数，一组匹配的域名列表，这个模板是一个 ant 分隔的模板，用.号作为分隔符。它通过参数中的主机地址作为匹配规则。

      ```yaml
      server:
        port: 8080
      spring:
        application:
      ​    name: api-gateway
        cloud:
      ​    gateway:
      ​      routes:
      ​        -id: gateway-service
      ​          uri: https://www.baidu.com
      ​          order: 0
      ​          predicates:
      ​            - Host=**.baidu.com
      
      #使用 curl 测试，命令行输入:curl http://localhost:8080 -H "Host: www.baidu.com",curl http://localhost:8080 -H "Host: md.baidu.com",经测试以上两种 host 均可匹配到 host_route 路由，去掉 host 参数则会报 404 错误。
      ```

   6.  通过请求方式匹配

      ​	可以通过 POST、GET、PUT、DELETE 等不同的请求方式来进行路由。

      ```yaml
      server:
        port: 8080
      spring:
        application:
      ​    name: api-gateway
        cloud:
      ​    gateway:
      ​      routes:
      ​        -id: gateway-service
      ​          uri: https://www.baidu.com
      ​          order: 0
      ​          predicates:
      ​            - Method=GET
      
      #使用 curl 测试，命令行输入:
      # curl 默认是以 GET 的方式去请求
      curl http://localhost:8080
      测试返回页面代码，证明匹配到路由，我们再以 POST 的方式请求测试。
      # curl 默认是以 GET 的方式去请求
      curl -X POST http://localhost:8080
      返回 404 没有找到，证明没有匹配上路由
      ```

   7. 请求路径匹配

      ​	**Path Route Predicate** 接收一个匹配路径的参数来判断是否走路由。

      ```yaml
      server:
        port: 8080
      spring:
        application:
      ​    name: api-gateway
        cloud:
      ​    gateway:
      ​      routes:
      ​        -id: gateway-service
      ​          uri: http://ityouknow.com
      ​          order: 0
      ​          predicates:
      ​            -Path=/foo/{segment}
      
      #如果请求路径符合要求，则此路由将匹配，例如：/foo/1 或者 /foo/bar。
      使用 curl 测试，命令行输入:
      curl http://localhost:8080/foo/1
      curl http://localhost:8080/foo/xx
      curl http://localhost:8080/boo/xx
      经过测试第一和第二条命令可以正常获取到页面返回值，最后一个命令报404，证明路由是通过指定路由来匹配。
      ```

   8. 通过请求IP地址进行匹配

      ​	Predicate 也支持通过设置某个 ip 区间号段的请求才会路由，RemoteAddr Route Predicate 接受 cidr 符号(IPv4 或 IPv6 )字符串的列表(最小大小为1)，例如 192.168.0.1/16 (其中 192.168.0.1 是 IP 地址，16 是子网掩码)。

      ```yaml
      server:
        port: 8080
      spring:
        application:
      ​    name: api-gateway
        cloud:
      ​    gateway:
      ​      routes:
      ​        - id: gateway-service
      ​          uri: https://www.baidu.com
      ​          order: 0
      ​          predicates:
      ​            - RemoteAddr=192.168.1.1/24
      
      #可以将此地址设置为本机的 ip 地址进行测试。
      curl localhost:8080
      如果请求的远程地址是 192.168.1.10，则此路由将匹配。
      1.5.10 组合使用
      ```

   9. 组合使用

      ```yaml
      server:
        port: 8080
      spring:
        application:
          name: api-gateway
        cloud:
          gateway:
            routes:
              - id: gateway-service
                uri: https://www.baidu.com
                order: 0
                predicates:
                  - Host=**.foo.org
                  - Path=/headers
                  - Method=GET
                  - Header=X-Request-Id, \d+
                  - Query=foo, ba.
                  - Query=baz
                  - Cookie=chocolate, ch.p
                  
      #各种 Predicates 同时存在于同一个路由时，请求必须同时满足所有的条件才被这个路由匹配。
      一个请求满足多个路由的断言条件时，请求只会被首个成功匹配的路由转发            
      ```

3. Springcloud Gateway 高级功能

   1. 实现熔断降级

      > 为什么要实现熔断降级？
      >
      > ​	在分布式系统中，网关作为流量的入口，因此会有大量的请求进入网关，向其他服务发起调用，其他服务不可避免的会出现调用失败（超时、异常），失败时不能让请求堆积在网关上，需要快速失败并返回给客户端，想要实现这个要求，就必须在网关上做熔断、降级操作。
      >
      > 为什么在网关上请求失败需要快速返回给客户端？
      >
      > ​	因为当一个客户端请求发生故障的时候，这个请求会一直堆积在网关上，当然只有一个这种请求，网关肯定没有问题（如果一个请求就能造成整个系统瘫痪，那这个系统可以下架了），但是网关上堆积多了就会给网关乃至整个服务都造成巨大的压力，甚至整个服务宕掉。因此要对一些服务和页面进行有策略的降级，以此缓解服务器资源的的压力，以保证核心业务的正常运行，同时也保持了客户和大部分客户的得到正确的相应，所以需要网关上请求失败需要快速返回给客户端。

      ```yaml
      server:
       port: 8082
      spring:
        application:
          name: gateway
        redis:
            host: localhost
            port: 6379
            password: 123456
        cloud:
          gateway:
            routes:
              - id: rateLimit_route
                uri: http://localhost:8000
                order: 0
                predicates:
                  - Path=/test/**
                filters:
                  - StripPrefix=1
                  - name: Hystrix
                    args:
                      name: fallbackCmdA
                      fallbackUri: forward:/fallbackA
      
        hystrix.command.fallbackCmdA.execution.isolation.thread.timeoutInMilliseconds: 5000
        
      ###################################
      这里的配置，使用了两个过滤器：
      
      （1）过滤器StripPrefix，作用是去掉请求路径的最前面n个部分截取掉。
      
      StripPrefix=1就代表截取路径的个数为1，比如前端过来请求/test/good/1/view，匹配成功后，路由到后端的请求路径就会变成http://localhost:8888/good/1/view。
      
      （2）过滤器Hystrix，作用是通过Hystrix进行熔断降级
      
      当上游的请求，进入了Hystrix熔断降级机制时，就会调用fallbackUri配置的降级地址。需要注意的是，还需要单独设置Hystrix的commandKey的超时时间
        
      ```

      ```java
      //fallbackUri配置的降级地址的代码如下：
      @RestController
      public class FallbackController {
      
          @GetMapping("/fallbackA")
          public Response fallbackA() {
              Response response = new Response();
              response.setCode("100");
              response.setMessage("服务暂时不可用");
              return response;
          }
      }
      ```

      

   2. 分布式限流

      > ​	从某种意义上讲，令牌桶算法是对漏桶算法的一种改进，桶算法能够限制请求调用的速率，而令牌桶算法能够在限制调用的平均速率的同时还允许一定程度的突发调用。在令牌桶算法中，存在一个桶，用来存放固定数量的令牌。算法中存在一种机制，以一定的速率往桶中放令牌。每次请求调用需要先获取令牌，只有拿到令牌，才有机会继续执行，否则选择选择等待可用的令牌、或者直接拒绝。放令牌这个动作是持续不断的进行，如果桶中令牌数达到上限，就丢弃令牌，所以就存在这种情况，桶中一直有大量的可用令牌，这时进来的请求就可以直接拿到令牌执行，比如设置qps为100，那么限流器初始化完成一秒后，桶中就已经有100个令牌了，这时服务还没完全启动好，等启动完成对外提供服务时，该限流器可以抵挡瞬时的100个请求。所以，只有桶中没有令牌时，请求才会进行等待，最后相当于以一定的速率执行。

      在Spring Cloud Gateway中，有Filter过滤器，因此可以在“pre”类型的Filter中自行实现上述三种过滤器。但是限流作为网关最基本的功能，Spring Cloud Gateway官方就提供了RequestRateLimiterGatewayFilterFactory这个类，适用在Redis内的通过执行Lua脚本实现了令牌桶的方式。具体实现逻辑在RequestRateLimiterGatewayFilterFactory类中，lua脚本在如下图所示的文件夹中：
      ![在这里插入图片描述](https://upload-images.jianshu.io/upload_images/19816137-0456652619daecaa?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

      限流配置如下:

      ```yaml
      server:
        port: 8081
      spring:
        cloud:
          gateway:
            routes:
            - id: limit_route
              uri: http://httpbin.org:80/get
              predicates:
              - After=2017-01-20T17:42:47.789-07:00[America/Denver]
              filters:
              - name: RequestRateLimiter
                args:
                  key-resolver: '#{@userKeyResolver}'
                  redis-rate-limiter.replenishRate: 1
                  redis-rate-limiter.burstCapacity: 3
        application:
          name: cloud-gateway
        redis:
          host: localhost
          port: 6379
          database: 0
          
      ############################
      在上面的配置文件，指定程序的端口为8081，配置了 redis的信息，并配置了RequestRateLimiter的限流过滤器，该过滤器需要配置三个参数：
      
      burstCapacity，令牌桶总容量。
      
      replenishRate，令牌桶每秒填充平均速率。
      
      key-resolver，用于限流的键的解析器的 Bean 对象的名字。它使用 SpEL 表达式根据#{@beanName}从 Spring 容器中获取 Bean 对象。
      ```

      这里根据用户ID限流，请求路径中必须携带userId参数

      ```java
      @Bean
      KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("user"));
      }
      
      /******************************/
      KeyResolver需要实现resolve方法，比如根据userid进行限流，则需要用userid去判断。实现完KeyResolver之后，需要将这个类的Bean注册到Ioc容器中。
      ```

      如果需要根据IP限流，定义的获取限流Key的bean为

      ```java
      @Bean
      public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
      }
      /**********其他限流方式***********************/
      //url
      @Bean
      KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
      }
      ```

      

   

   

   