### Eureka的自我保护机制

1. 在某些情况下，注册在Eureka的服务已经挂掉，但是服务的注册信息还留在Eureka的服务列表中。

2. Eureka服务端的配置

   ```yaml
   eureka:
     instance:
       hostname: 127.0.0.1
     client:
       registerWithEureka: false
       fetchRegistry: false
       serviceUrl:
         defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
     server:
       # 关闭自我保护机制
       #在开发环境下可以关闭自我保护机制，生产环境建议打开
       enable-self-preservation: false
       # 每隔10s扫描服务列表，移除失效服务
       # 默认情况下，Eureka会将90秒内未发送心跳的服务从列表移除，设置以下参数可以将缩短或延长这个时间
       eviction-interval-timer-in-ms: 10000
   ```

   

3. 默认情况下，如果Eureka Server在一定时间内（默认90秒）没有接收到某个微服务实例的心跳，Eureka Server将会移除该实例。但是当网络分区故障发生时，微服务与Eureka Server之间无法正常通信，而微服务本身是正常运行的，此时不应该移除这个微服务，所以引入了自我保护机制。

   自我保护模式正是一种针对网络异常波动的安全保护措施，使用自我保护模式能使Eureka集群更加的健壮、稳定的运行。

4. 自我保护模式工作机制：

   自我保护机制的工作机制是如果在15分钟内超过85%的客户端节点都没有正常的心跳，那么Eureka就认为客户端与注册中心出现了网络故障，Eureka Server自动进入自我保护机制，此时会出现以下几种情况：

   * Eureka Server不再从注册列表中移除因为长时间没收到心跳而应该过期的服务。
   * Eureka Server仍然能够接受新服务的注册和查询请求，但是不会被同步到其它节点上，保证当前节点依然可用
   * 当网络稳定时，当前Eureka Server新的注册信息会被同步到其它节点中。

5. Eureka客户端配置

   ```yaml
   eureka:
     instance:
       # 每隔10s发送一次心跳(Eureka默认是30s)
       lease-renewal-interval-in-seconds: 10
       # 告知服务端30秒还未收到心跳的话，就将该服务移除列表（默认90s）
       lease-expiration-duration-in-seconds: 30
     client:
       serviceUrl:
         defaultZone: http://localhost:9501/eureka/
   ```

   

