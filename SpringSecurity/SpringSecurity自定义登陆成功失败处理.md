SpringSecurity自定义登陆成功/失败处理



​	SpringSecrutiy登陆成功后默认会跳转到登陆之前引发登陆的请求上，但是在很多场景下，登陆并不是一个表单提交的同步方式完成的，而是通过异步的ajax请求进行访问，这时候前端希望拿到的是一些用户的相关信息而不是去处理引发登陆的请求

1. 自定义登陆成功处理

   * 在SpringSecurity中自定义登陆成功处理只需要实现AuthenticationSuccessHandler这个接口

   ```java
   
   //实现这个接口中的onAuthenticationSuccess方法
   @Component
   public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler{
       
       //这个方法在用户登陆成功后会被调用
       @Overrider
       public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ...{
           //authentication这个参数会封装登陆成功后的认证信息，这部分信息包括发起的认证请求里的信息，包括认证请求的ip，session是什么之类的，在用户的登陆信息认证成功后，返回的登陆信息也是包装在Authentication这个对象中的 
       }
   }
   ```

   

   * 在实现AuthenticationSuccessHandler接口并注册到Spring中之后，还需要在WebSecurityConfigurerAdapter这个配置类中将自定义的这个认证成功处理器注册到Security中

   ```java
   private AuthenticationSuccessHandler myAuthenticationSuccessHandler;
   
   Http.formLogin()
       .loginPage("")
       .successHandler(myAuthenticationSuccessHandler);
   ```

   

2. 自定义登陆失败处理

   * 在SpringSecurity中自定义登陆失败处理只需要实现AuthenticationFailureHandler这个接口

   ```java
   //实现这个接口中的onAuthenticationFailure方法
   @Component
   public class MyAuthenticationFailureHandler implements AuthenticationfailureHandler{
       
       //这个方法在用户登陆失败后会被调用
       @Overrider
       public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws ...{
           //authenticationException这个参数会封装登陆失败后的异常信息，authenticationException这个类有很多子类对应用户认证异常的各种原因
       }
   }
   ```

   

