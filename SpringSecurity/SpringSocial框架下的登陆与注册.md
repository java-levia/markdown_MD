SpringSocial框架下的登陆与注册

1. 在SpringSocial中，当第三方登陆的用户是首次登陆时，Social的异常处理机制会将用户导向默认的注册页路径"/signup",但是由于这个路径在安全配置类中并没有放行，所以最终会被导向登陆引导。面对以上情况，只需要做如下处理：

   * 根据业务需求自定义一个注册页面，
   * 设置springSocialConfigurer的属性signupUrl为自定义的登录页路径
   * 在安全配置路径中对注册路径进行放行
   * 注册的逻辑是由自定义的控制器进行处理，SpringSocial并不介入

2. 在注册页上进行注册与绑定第三方信息的时，可以展示一些第三方账号的信息，关于这部分的实现，Social有一个工具类用于实现这种类型的需求

   * providerSignUtils 这个类可以用于在注册/绑定时获取到之前拿到的第三方平台的用户信息并于新注册的（或者已注册的）用户信息做绑定

   ```java
   //这个方法与getUsersConnectionRepository位于同一个类下（SocialConfigurerAdapter的子类）
   
   @Bean
   public ProviderSignUtils providerSignUtils(ConnectionFactoryLocator connectionFactoryLocator) {
       return new ProviderSignupUtils(connectionFactoryLocator, getUsersConnectionRepository(connectionFactoryLocator))
   }
   ```

3. 创建SocialUserInfo实体类，用于获取并向页面传递第三方用户信息（相当于一个标准的第三方用户信息类）

   ```java
   @Data
   public class SocialUserInfo{
       private String providerId; //用于标记第三方平台
       private String providerUserId; //用户在第三方平台的唯一id
       private String nickname; //用户昵称
       private String headimg; //第三方平台的头像信息
   }
   ```

   

4. 在Browser安全相关的控制器类中提供一个方法用于获取到用户第三方平台的用户信息。（通过ProviderSignupUtils）

```java
//因为在之前我们将ProviderSignupUtils当作Bean注入到了SPring中，所以可以使用@autowired进行自动注入到控制器类中
@getMapping("/social/user")
public SocialUserInfo getSocialInfo(HttpServletRequest request){
    SocialUserInfo userInfo = new SocialUserInfo();
    //这里能从Session中拿到Connection信息是因为在SocialAuthenticationFilter中（250行左右）将Connection信息设置到了Session中
    Connection<?> connection = providerSignupUtils.getConnectionFromSession(new ServletWebRequest(request));
    //然后通过Connection获取到相应的用户信息设置到userInfo中
}
```

5. 在用户的注册或者绑定逻辑中都是需要用到ProviderSignupUtils这个对象的，因为直到目前为止，用户在第三方平台获取到的信息依旧没有存储到数据库中，所以需要在注册或者绑定逻辑中，将平台的用户唯一标识与获取到的第三方平台用户信息关联并插入到数据库中。

```java
//

public void regist(User user, HttpServletRequest request){
    //获取到用户信息中中唯一标识（这里用的是用户名）
    String userId = user.getUserName();
    //将用户唯一标识传入到providerSignupUtils中与第三方信息绑定起来并注册到第三方信息表中
    providerSignupUtils.doPostSignUp(userId, new ServletWebRequest(request));
}
```

6. 最后，注册的请求也要配置到Security安全配置中进行放行。
7. mysql8.0中将rank这个字段设为了关键字，所以在使用springSocial时最好使用mysql8.0以下的数据库