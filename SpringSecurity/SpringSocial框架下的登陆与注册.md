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
8. 实现Social登陆时，必须重写SocialConfigurerAdapter子类中的getUsersConnectionRepository方法并将数据库注入，否则Social会默认从内存中获取用户信息，导致即便用户使用已注册的账号进行登陆，错误处理器依旧会将页面循环跳转到signup页。

```java
//
@Configuration
public class WeixinAutoConfig extends SocialConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private DataSource dataSource;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        connectionFactoryConfigurer.addConnectionFactory(this.createConnectionFactory());
    }

    public ConnectionFactory<?> createConnectionFactory() {
        WeixinProperties weixin = securityProperties.getSocial().getWeixin();
        return new WeixinConnectionFactory(weixin.getProviderId(),weixin.getAppId(), weixin.getAppSecret());
    }

    //必须重写这个方法
    @Override
    public UsersConnectionRepository getUsersConnectionRepository(
            ConnectionFactoryLocator connectionFactoryLocator) {
        return new JdbcUsersConnectionRepository(dataSource,connectionFactoryLocator, Encryptors.noOpText());
    }
}
```

9. 平台账号与第三方平台账号的绑定与解绑

   * 在平台账号与第三方平台的信息进行绑定与解绑的需求下，至少要实现三个功能：
     1. 平台账号与第三方账号的绑定
     2. 平台账号与第三方账号的解绑
     3. 平台账号与第三方账号当前的绑定关系

   * 在SpringSocial 的实现中，已经提供了这三个功能的默认实现，但是在实现的过程中只提供了数据，没有提供视图，所以开发者需要创建一个视图用于将数据展现到页面上
     * 提供这几个功能的服务是ConnectController这个控制器类，其中“/connect”所映射的控制器可以获取当前登陆账号的绑定关系（connectionStatus方法）。

   ```java
   @RequestMapping(
           method = {RequestMethod.GET}
       )
       public String connectionStatus(NativeWebRequest request, Model model) {
           this.setNoCache(request);
           this.processFlash(request, model);
           Map<String, List<Connection<?>>> connections = this.connectionRepository.findAllConnections();
           model.addAttribute("providerIds", this.connectionFactoryLocator.registeredProviderIds());
           model.addAttribute("connectionMap", connections);
           //这里返回的是一个字符串“connect/status”，Spring会尝试在程序中查找“connect/status”所对应的视图，所以如果需要对返回的数据进行封装然后创建视图返回给前端，需要写一个控制器（映射路径为“connect/status”）
           return this.connectView();
       }
   //从这个方法可以看到该方法获取到了该用户的所有账号连接信息，根据需求可以获取到我们需要任何已有的用户相关信息。可以只返回是否绑定，也可以返回其他相关信息
   
   //写一个方法继承AbstractView这个抽象类写一个视图，通过参数model获取到数据，然后通过respose将数据以流的形式返回到页面
   /**
    * 获取绑定关系
 */
   @Component("connect/status")
   public class BindStatusView extends AbstractView {
       @Autowired
       private ObjectMapper objectMapper;
       /**
        *
        * @param model 在之前的模型中，通过addAttribute方法将一些数据设置到了这个模型中，所以此时可以通过model的方法将这些数据获取出来
        * @param request
        * @param response
        * @throws Exception
        */
       @Override
       protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
           //connections是获取到的第三方平台上的用户信息
           Map<String, List<Connection<?>>> connections = (Map<String, List<Connection<?>>>)model.get("connectionMap");
           Map<String, Boolean> result = new HashMap<>();
           for (String s : connections.keySet()) {
               result.put(s, !CollectionUtils.isEmpty(connections.get(s)));
           }
           response.setContentType("application/json;charset=UTF-8");
           response.getWriter().write(objectMapper.writeValueAsString(result));
       }
   }
   
   ```
   
    *  尚未绑定的账号进行绑定
      
       * 第三方账号与平台账号的绑定逻辑，Social也已经帮我们实现了，具体的请求是ConnectController类中的“connect/{providerId}”这个POST请求。我们只需要在页面上发起“connect/{providerId}”的post请求，Social会帮我们将此时登陆的用户引导向绑定providerId对应的第三方平台。需要注意的是，Social同样没有帮我们实现视图，我们需要自己动手实现对应的视图。
       
         ```java
         /**
          * 这个视图是用于第三方平台账号与平台账号绑定成功后用于展示绑定结果的页面，为了保证这个视图被不同的平台绑定成功时所使用
          * 所以在这里我们不能像展示绑定状态的那个视图一样在这里使用Component进行写死，而是需要到各第三方平台的配置类中进行自定义
          */
         public class BindView extends AbstractView {
             @Override
             protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
         
                 response.setContentType("text/html;UTF-8");
                 response.getWriter().write("<h3>绑定成功<h3>");
             }
         }、
         
         @Bean("connect/weixinConnected")
             @ConditionalOnMissingBean(name = "weixinConnected")  //这个注解的使用，可以保证使用容器的人通过向Spring中注入一个名为weixinConnected的Bean自定义绑定成功展示
             public BindView weixinConnected(){
                 return new BindView();
             }
         ```
       
         
   
   * 单机session管理
   
     * SpringSecurity中的单机session管理很简单，只需要在配置文件中添加一行配置就可以
   
       ```properties
       #这个值不设置默认是30分钟，这里的10表示的是10秒，但是这10秒实际上不会生效，因为SpringSecurity中默认会将小于一分钟的session超时时间重置为1分钟
       server.session.timeout=10
       ```
   
     * 在SpringSecurity中可以对session超时后的行为做控制，通过在WebSecurityConfigurerAdapter子类的configure方法中添加一行
   
       ```java
       .and()
           .sessionManagement()
           .invalidSessionUrl("这里表示session超时后跳转的控制器路径，在控制器中定义session超时后的行文")
           //session并发控制 ,这里maximumSession设置为1表示系统中同一个账号只允许存在一个session，也就是说同一个时刻只允许一个账号进行登陆，效果就是后面用户登陆会把之前登陆的用户下线
           .maximumSession(1)
          //这个配置项可以对踢除用户后的行为做控制，比如对用户 踢出 这种行为做一些记录。新建一个类实现SessionInfomationExpiredStrategy接口并实现onExpiredSessionDetected方法，通过这个方法的参数SessionInfomationExpiredEvent可以获得一些相关的信息，比如说在原登陆页面显示被踢下线的理由
           .expiredSessionStrategy(new SessionInfomationExpiredStrategy的实现类对象)
           //如果要在用户登陆之后阻止同账号再登陆，可以再加上以下配置
           .maxSessionPreventLogin(true)
       ```
   
     * 
   
       ```
       
       ```
   
       
   
   * 