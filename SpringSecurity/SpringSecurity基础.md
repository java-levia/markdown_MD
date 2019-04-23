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

3. SpringSecurity中通过查找数据库中的用户账户信息进行认证

   * SpringSecurity中通过查找数据库中的用户账户信息进行认证是通过SpringSecurity提供的UserDetailsService接口实现的，实现这个接口并重写其中的loadUserByUsername(String username)方法

   * 在loadUserByUsername这个方法中，会将根据用户在登陆过程中输入的用户名到数据库中查找用户信息并将用户信息封装在UserDetails的一个实现类中，SpringSecurity会通过UserDetail中的用户信息去做校验（校验是SpringSecurity做的，我们只需要提供用户信息）

     ```java 
     //自定义用户账户进行认证，实现UserDetailsService这个接口
     //关于Authorities：这个是用户授权的角色，在SpringSecurity中，Authorities是将用户与权限管理起来的桥梁，换句话说，真正拥有权限的是角色，将用户赋予某个角色，这个用户就拥有了这个角色的权限
     @Component
     public class MyUserDetailsService implements UserDetailsService{
         
         @Overrider
         public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
             //这个方法会将权限字符串转换成对应的角色对象，并赋予用户相应的权限
             ArrayList<Authorities> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("这里传入一个角色的字符串");
             //这里返回的User是UserDetails的一个实现类，由SpringSecurity实现的
             return new User(username, password(数据库中查到的密码), authorities(用户的授权角色))
         }
     }
     ```

   * UserDetails接口是SpringSecurity用户登陆校验中很重要的接口

     ```java
     public interface UserDetails extends Serializable {
         //上面的三个抽象方法就是在校验用户的流程中获取User对象信息的三个方法
         Collection<? extends GrantedAuthority> getAuthorities();
     
         String getPassword();
     
         String getUsername();
     
         //以下这几个方法是用于实现我们自定义的认证逻辑的方法
         
         //你的账户没有过期（返回true表示没有过期）
         boolean isAccountNonExpired();
     
         //你的账户是否被锁定了（true表示没有锁定或冻结）
         boolean isAccountNonLocked();
     
         //你的密码是否过期（这个方法用于一些需要定期修改密码的网站，如果过期需要重新设置密码）
         boolean isCredentialsNonExpired();
     
         //这个是用于校验用户是否被删除（用于做假删除，true表示没有被删除）
         boolean isEnabled();
     }
     
     ```

4. SpringSecurity密码校验

   * 在SpringSecurity中进行密码校验这部分功能的是PasswordEncoder,在SpringSecurity的发展过程中，定义过两个PasswordEncoder，其中一个是在crypto包中的是新版本，另一个是在authentication这个包中的老版本，我们的代码中需要使用的是crypto包中的这个接口

     ```java
     //密码校验接口
     public interface PasswordEncoder {
         //这个方法是用于密码加密的方法
         String encode(CharSequence var1);
     
         //这个方法用于校验数据库中的密码和用户登陆输入的密码是否匹配
         boolean matches(CharSequence var1, String var2);
     
         default boolean upgradeEncoding(String encodedPassword) {
             return false;
         }
     }
     
     ```

   * 密码的加密，SpringSecurity中对PasswordEncoder接口有默认实现，但是在使用之前需要将加密（encode）和匹配密码（matches）的实现配置到WebSecurityConfigurerAdapter中

     ```java
     @Configuration
     public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
     
     	@Bean
         public PasswordEncoder passwordEncoder(){
             //这个类是PasswordEncoder的实现类
             //如果需要自定义密码的加密和校验方式，也可以通过实现PasswordEncoder这个接口然后用同样的注入方式注入进SpringSecurity
         	return new BcryPasswordEncoder();    
         }
         
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

     

