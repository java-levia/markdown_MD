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

   * 使用过滤器的弊端：由于过滤器是java的Servlet规范定义的，调用和初始化都是由容器进行的，所以在Filter里面无法获得任何Spring的资源，这就意味着在Filter中我们无法获知我们的请求具体是由哪个控制器处理的，如果由这方面的需求我们就无法使用Filter来达到 

2. 使用Interceptor进行拦截

   ```java
   @Component
   public class MyInterceptor implements HandlerInterceptor {
       
       // 这个方法在Controller执行之前执行， 
       @Override
       public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
           //可以使用request域在Interceptor的这几个方法之间传递信息
           //Interceptor拦截器相对于Filter的优势在于可以获得请求的控制器的信息（方法名等信息，但是不包括请求参数），这部分信息被保存在handler（类型是 HandlerMethod）这个属性中
           return false;
       }
   
       //这个方法在Controller执行之后执行，如果在执行Controller方法的过程中抛出异常或者preHandler方法的返回值为false，这个方法不会执行
       @Override
       public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
   
       }
   
       //这个方法在Controller执行完毕之后一定会被调用，不管Controller是否正常执行
       @Override
       public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
   
           //这个方法中，如果在执行Controller的过程中抛出了异常，通过这个方法的参数Exception可以获取到异常的信息，如果Controller正常执行，Exception为空
           //另外需要注意的是，如果我们通过@ControllerAdvice机制对Controller抛出来的异常进行了处理，那么在这个方法中是获取不到异常信息的
       }
   }
   
   //SpringBoot中，不仅需要使用@Component将拦截器扫描进Spring容器，还需要将拦截器注册到Spring中，在Spring2.0之前是在WebMvcConfigurerAdpter 的实现类中进行注册，Spring2.0之后需要实现的类是WebMvcConfigurer，同时重写addInterceptor(InterceptorRegistry registry)方法
   ```

   ```java
   @Configuration
   public class WebConfig implements WebMvcConfigurer {
       @Autowired
       private MyInterceptor myInterceptor;
   
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
   
       //通过这个方法将Interceptor注册到Spring中
       @Override
       public void addInterceptors(InterceptorRegistry registry) {
           registry.addInterceptor(myInterceptor);
           
       }
   }
   ```

   使用拦截器的弊端：使用拦截器也有一定的局限性，表现在于在拦截器中无法获取请求的参数，原因可以通过阅读Spring的源码得知。如果我们的需求是不仅仅想获得方法名之类的信息，还想获取请求参数的相关信息，那么使用Inteceptor就无法达到目的了

3. 使用切面