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

   