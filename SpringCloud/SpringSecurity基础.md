SpringSecurity基础

1. SpringSecurity配置类

   ```java
   @Configuration
   public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
   
       /**
        * 配置security的基本安全行为
        * @param http
        * @throws Exception
        *
        * SpringSecurity的认证流程本质上是一个拦截器链，在这个链上有多个拦截器，包括但不限于UsernamePasswordAuthenticationFilter，
        * BasicAuthenticationFilter，ExceptionTranslationFilter，FilterSecurityInterceptor，在这些过滤器中，有些是可以通过配置
        * 决定该过滤器是否生效的（UsernamePasswordAuthenticationFilter，BasicAuthenticationFilter就属于这一类），有些是必然会生效的
        * （ExceptionTranslationFilter，FilterSecurityInterceptor属于这一类），
        * 一个http请求过来，如果该请求是需要对资源进行访问，请求必然会到达FilterSecurityInterceptor这个过滤器，
        * 该过滤器会根据WebSecurityConfigurerAdapter中的配置决定是否允许对资源进行访问，如果访问的是需要认证的资源而请求没有访问权限，
        * 会抛出异常，异常会被ExceptionTranslationFilter捕获异常并进行处理，会将请求重定向到认证页面（即登陆页）
        * 用户输入认证信息（账号/密码）后，认证请求会根据WebSecurityConfigurerAdapter中的配置经过相应的过滤器
        * （WebSecurityConfigurerAdapter中配置http.formLogin()，会经过UsernamePasswordAuthenticationFilter过滤器，
        * 配置http.httpBasic()则会经过BasicAuthenticationFilter过滤器）
        * 正常通过认证过滤器后请求中携带了认证信息，再次到达FilterSecurityInterceptor时不会再抛出异常，请求又会跳转到之前的资源请求进行正常的资源访问
        */
       @Override
       protected void configure(HttpSecurity http) throws Exception {
           //以下代码定义的是： 使用表单登陆的方式认证，且任何请求都需要身份认证
           //在这里可以配置表单登陆或者httpBasic的方式认证
           //http.httpBasic()
           http.formLogin()
               //.loginPage("自定义登陆登陆页面")
               //.loginProcessingUrl("登陆请求")  登陆请求的路径
               .and()
               .authorizeRequests()
                   //.antMatcher("自定义登陆登陆页面").permitAll()   表示登陆页面不需要认证
               .anyRequest()
               .authenticated()
               .and()
               .csrf().disable(); //关闭跨站请求伪造防护
   
       }
   }
   
   ```

   

2. 自定义用户认证流程

   * 对网页端的应用来说，将登陆页面在SpringSecurity中作为可配置项是比较灵活的操作方式，要达到这个目的loginPage()方法中的参数就不能写死为一个固定的html链接，而是需要请求一个Controller控制器通过一些逻辑进行处理。代码如下

     ```java
     //
     @RestController
     
     public class BrowserSecurityController{
         //Spring会将请求的源链接地址缓存在这个对象中
         private RequestCache requestCache = new HttpSessionRequestCache();
         //这是Spring的一个用来做跳转的对象
         private RedirectStrategy redirectStrategy = new RedirectStrategy();
         @autowired
         private SecurityProperties securityProperties;
         /**
         * 当需要身份认证时，就需要跳转到这个控制器，这个控制器中的逻辑是，如果请求的原链接是html结尾	 * 的，则将请求重定向到登陆页，如果不是以html结尾的，则向前台返回错误信息
         * 
         */
         
         @RequestMapping("/authentication/require")
         @ResponseStatus(code = HttpStatus.UNAUTHORZED)//这个注解是给方法定义了一个返回的状态码 表示未授权；另外，Restful的方法应该返回的是Json，所以还可以将返回值包装成一个对象
         public String RequestAuthentication(HttpServletRequest request, HttpServletResponse response){
             //通过requestCache对象获取到请求源路径
             SavedRequest savedRequest = requestCache.getRequest(request, response);
             
             if(StringUtils.endsWithIgnoreCase(targetUrl, ".html")){
                 //使用Spring的一个跳转工具对象将请求跳转到登陆页面
                 //securityProperties.getBrowser().getLoginPage()这段代码从配置文件中获取自定义的登录页链接
                 redirectStrategy.sendRedirect(request, response, securityProperties.getBrowser().getLoginPage());
             }
             //如果请求不是html结尾的，则返回一段错误信息，引导用户去登陆页面
             return "错误信息";
         }
     }
     ```

     

   * 如果要在配置文件中自定义配置项进行登陆页的配置，需要创建几个配置类做一些行为的配置，这种创建配置类的方式很灵活，值得借鉴，在这里做下记录

     ```java
     //以下的配置都是为了让配置文件中的 custom.security.browser.loginPage这段配置被读取到Spring容器中
     
     //这个注解的作用是，SecurityProperties 这个类会读取配置文件中以 custom.security 开头的配置项，
     @ConfigurationProperties(prefix="custom.security")
     public class SecurityProperties{
         //这个属性的存在，表示配置文件中以 custom.security.browser这个开头的配置项都会读取到browser这个对象中
         private BrowserProperties browser = new BrowserProperties();
         
         》》》》》》》》set  get方法省略
     }
     
     //创建BrowserProperties类
     public class BrowserProperties{
         
         private String loginPage;
         
          》》》》》》》》set  get方法省略
     }
     ```

     如果需要以上的配置项生效，还需要一个配置器类	

     ```java
     //这个配置类的关键在于EnableConfigurationProperties这个注解，它的存在使配置类SecurityProperties生效
     @Configuration
     @EnableConfigurationProperties(SecurityProperties.class)
     public class SecurityCoreConfig{
         
     }
     
     //完成以上配置后，在目标类中注入 SecurityProperties对象就可以获取自定义的配置
     ```

     

3. SpringSecurity整合JWT Token进行认证