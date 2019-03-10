## Zuul网关

1. 如果需要设计一台公司项目的接口，你会如何设计
   * 接口权限（开放接口|内部接口）
   * 考虑幂等性
   * 安全性（Https协议）
   * 防止篡改数据（验证签名）
   * 使用网关拦截接口实现黑名单和白名单
   * 接口使用http协议+json格式  目的是为了跨平台
   * 考虑高并发，对接口服务实现保护，降级、熔断、隔离
   * 使用统一的api管理平台  如swagger

2. 网关的概念
   * 相当于客户端请求统一先请求到网关服务器上，再由网关服务器进行转发请求，类似于nginx
   * 网关的作用
     * 网关可以拦截客户端所有请求，对该请求进行权限控制、负载均衡、日志管理、接口调用监控
   * 过滤器与网关区别是什么
     * 过滤器适用于拦截单个tomcat服务器进行拦截请求
     * 网关是拦截整个微服务的所有请求

3. 在网关的请求中，如果服务做了集群，如果不做任何负载均衡的配置，在请求做了集群的接口时网关会报错

   ```java
   com.netflix.client.ClientException: Load balancer does not have available server for client：xxx
   ```

   这时只需要在客户端添加如下配置：

   ​	

   ```yml
   ribbon:
     eureka:
       enabled: true
   ```

   

4. Zuul网关的使用

   1. zuul网关过滤器

   ```java
   @Component
   public class TokenFilter extends ZuulFilter {
   
       /**
        * 编写过滤器拦截业务逻辑代码
        * @return
        */
       @Override
       public Object run() throws ZuulException {
           //案例 拦截所有服务接口，判断服务接口上是否有传递userToken参数
   
           //获取上下文
           RequestContext currentContext = RequestContext.getCurrentContext();
           //获取request
           HttpServletRequest request = currentContext.getRequest();
           //获取Token（在实际的应用中，token一般放在请求头中）
           String userToken = request.getParameter("userToken");
           if(StringUtils.isEmpty(userToken)){
               //为空的情况下，不会再调用服务接口，网关直接响应给客户端一个
               currentContext.setSendZuulResponse(false);
               currentContext.setResponseBody("userToken is null" );
               currentContext.setResponseStatusCode(401);
               return null;
               //返回一个错误提示
           }
           //正常执行调用  继续向下执行
           return null;
       }
   
       /**
        * 过滤器的执行顺序
        * 当一个请求在同一个阶段遭遇多个过滤器时，这多个过滤器的执行顺序
        * @return
        */
       @Override
       public int filterOrder() {
           return 0;
       }
   
       /**
        * 表示过滤器是否生效
        * true 生效
        * false 失效
        * @return
        */
       @Override
       public boolean shouldFilter() {
           return true;
       }
   
       /**
        *  配置过滤器类型
        *  pre  表示在请求之前执行
        *
        * @return
        * @throws ZuulException
        */
       @Override  
       public String  filterType()  {
           return "pre";
       }
   }
   
   ```

   

5. Zuul网关集群
   1. 网关集群的搭建：使用Nginx+Zuul进行搭建
   2. 网关集群的话，一般至少是一主一备或者采用轮询机制
   3. 在微服务中，所有服务请求都会统一请求到Zuul网关上
   4. 在集群的Zuul网关之前，还会有Nginx作为反向代理和负载均衡服务器，从客户端发送过来的请求先统一请求到Nginx上，然后通过Nginx实现反向代理和负载均衡，nginx采用轮询算法将请求转发到各个网关

6. Nginx负载均衡的配置

   ```conf
   #上游服务器集群  默认轮询机制
   upstream backServer{
   	#第一个网关的配置
       server 127.0.0.1:81;
       #第二个网关的配置
       server 127.0.0.1:82；
   }
   server{
       listen 80;
       #这个域名可以在本地的hosts文件中  将127.0.0.1虚拟到java.levia.com
       server_name java.levia.com;
       location/{
           #指定上有服务器负载均衡服务器
           proxy_pass http://backServer/;
           index index.html index.htm;
       }
   }
   ```

   