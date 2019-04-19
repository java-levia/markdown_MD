SpringBoot Api拦截

1. 使用Filter进行拦截

   * 最简单的创建过滤器的方式就是实现Filter接口，然后通过@Component注解将Filter扫描进Spring进行管理，但是这种方式存在两个弊端，其一是这种方式定义的过滤器默认是对所有方法进行拦截的，其二是，对于第三方框架提供的过滤器，由于无法获取到源码，无法通过在实现类上添加@Component注解将过滤器添加到我们的业务中。解决这两个问题就需要用到FilterRegistrationBean这个对象

   * 通过FilterRegistrationBean这个对象注册Filter并设定过滤器的过滤路径

     ```java
     @Configuration
     public class WebConfig {
     
         @Bean
         public FilterRegistrationBean timeFilter(){
             FilterRegistrationBean registrationBean = new FilterRegistrationBean();
             TimeFilter filter = new TimeFilter();
             registrationBean.setFilter(filter);
     
             List<String> urls = new ArrayList<>();
             urls.add("/*");
     
             registrationBean.setUrlPatterns(urls);
     
             return registrationBean;
         }
     }
     
     ```

   * 使用拦截器的弊端：由于拦截器是java的Servlet规范定义的，调用和初始化都是由容器进行的，所以在Filter里面无法获得任何Spring的资源，这就意味着在Filter中我们无法获知我们的请求具体是由哪个控制器处理的，如果由这方面的需求我们就无法使用Filter来达到 

2. 使用Interceptor进行拦截

3. 使用切面进行拦截