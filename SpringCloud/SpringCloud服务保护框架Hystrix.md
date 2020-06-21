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
      ###指得是建立连接后从服务器读取到可用资源所用的时间
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
        * 目的是为了保护服务，在高并发的情况下，如果请求达到了一定的极限（可以自己设置一个阈值），如果流量超出了设置的阈值，自动开启保护服务功能，使用服务降级方式返回一个友好的提示。服务熔断机制和服务降级一起使用
      * 服务隔离机制

   2. Hystrix Demo

      ```java
      @RestController
      public class OrderController {
          @Autowired
          private MemberApi memberApi;
      
          @RequestMapping(value="/getMember/{name}/{age}",produces = {"application/json;charset=UTF-8"})
          public Member getMember(@PathVariable(name = "name") String name,
                                  @PathVariable(name="age") Integer age){
              return memberApi.getMember(name, age);
          }
      
          //未解决服务雪崩效应的接口
          @RequestMapping("/orderToMemberUserInfo")
          public ResultResponse orderToMemberUserInfo(){
              System.out.println("orderToMemberUserInfo"+"线程池名称"+Thread.currentThread().getName());
              return memberApi.orderToMemberUserInfo();
          }
      
          //服务降级调用的方法
          public ResultResponse userInfoFallback(){
              System.out.println("userInfoFallback"+"线程池名称"+Thread.currentThread().getName());
              return BaseApi.resultSuccess("业务繁忙，请稍后再试");
          }
      
          //解决服务雪崩效应的接口
          //fallbackMethod 方法的作用：服务降级执行
          //@HystrixCommand默认开启线程池隔离方式、服务降级、服务熔断机制
          //启用断路器需要多个配置  1 配置文件中配置 feign hystrix
        	// 2. controller加上@HystrixCommand并配置好服务降级方法
          // 3. 启动方法上添加注解 @EnableHystrix  启动Hystrix
         
          @HystrixCommand(fallbackMethod = "userInfoFallback")
          @RequestMapping("/orderToMemberUserInfoHystrix")
          public ResultResponse orderToMemberUserInfoHystrix(){
              System.out.println("orderToMemberUserInfoHystrix"+"线程池名称"+Thread.currentThread().getName());
             return memberApi.orderToMemberUserInfo();
          }
      
      }
      
      ```

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
      
      ###开启Hystrix断路器
      ###  
      feign:
        hystrix:
          enable: true
      
      ###hystrix禁止服务超时时间
      ###Hystrix默认开启了服务降级。默认情况下如果请求服务在1秒内没有得到返回值，Hystrix会默认进入服务降级，这时候会走fallback方法，配置以下参数可以禁用这种机制
      hystrix:
        command:
          default:
            execution:
              timeout:
                enabled: false
      ```

      