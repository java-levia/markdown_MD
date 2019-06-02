基于Spring Social实现QQ登陆

思路整理：

* 要实现qq第三方登陆，必须得有Connection对象，Connection对象由ConnectionFactory创建，而创建ConnectionFactory的条件是要由ServiceProvider通过OAuth协议连接第三方平台获取用户的信息（获取到的信息需要通过ApiAdapter转成标准的用户信息格式），所以，在创建ConnectionFactory工厂之前需要先创建通过ServiceProvider连接第三方平台经过授权获取资源服务器中的用户信息。

1. 实现Api接口（AbstractOAuth2ApiBinding）

   1. 创建一个QQApiBinding实现AbstractOAuth2ApiBinding接口

   * AbstractOAuth2ApiBinding源码简析：

     * 在AbstractOAuth2ApiBinding抽象类中有两个属性
       1. accessToken: 这个属性是用来存储获取到的第三方平台的令牌的。由于在AbstractOAuth2ApiBinding类中accessToken是一个类变量，但每个在第三方平台获取到的令牌都是不同的，所以AbstractOAuth2ApiBinding这个类的对象不能是单例的
       2. restTemplate：这个是用于向资源服务器发送http请求进行远程调用的对象

     ```java
     public class QQImpl extends AbstractOAuth2ApiBinding implements QQ{
         
         private Logger logger = LoggerFactory.getLogger(QQImpl.class);
         //获取openId需要请求的链接
         private static final String URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=%s";
         
         //获取用户信息需要请求的链接  oauth_consumer_key就是appId
         private static final String URL_GET_USERINFO = "https://graph.qq.com/user/get_user_info?
     oauth_consumer_key=%s&
     openid=%s";
         //这个参数需要到qq互联平台申请获得
         private String appid;
         //这个参数是每个qq用户都有一个独立的openid,是通过accessToken获取到的
         private String openId;
         
         private ObjectMapper objectMapper = new ObjectMapper();
         
         //创建构造函数给父类和本类的属性传值
         public QQImpl(String accessToken, String appId){
             //调用父类的构造函数将accessToken传入到父类属性中
             //在默认的Token策略中，会在发请求的时候将accessToken这个参数放在请求头中，但是在qq互联的规则中需要将accessToken这个参数放在请求参数中，显然默认的行为不符合我们的需求，所以传入TokenStrategy.ACCESS_TOKEN_PARAMETER这个参数的传入，是为了定义符合qq互联携带accessToken参数的要求
             super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
             
             this.appId = appId;
             //通过请求qq互联的接口获取openId
             String url = String.format(URL_GET_OPENID, accessToken);
             String result = getRestTemplate().getForObject(url, String.class)
                 
                 
             logger.info("获取到的openId相关信息："+ result);
             //从返回的字符串中截取openId
             this.openId = StringUtils.subStringBetween(result, "\"openid\":", "}");
         }
         
         @Overrider
         public QQUserInfo getUserInfo(){
             //进行获取用户信息的操作
             String url = String.format(URL_GET_USERINFO, appId, openId)
             //发送请求获取用户信息
             String result = getRestTemplate().getForObject(url, String.class)
                 
             logger.info("获取到的用户信息："+ result);
             
             //获取到的用户信息json字符串可以通过ObjectMapper对象（fasterxml.jackson包下的ObjectMapper）转成对象
             return objectMapper.readvalue(result, QQUserInfo.class);
         }
     }
     ```

     

2. 完成以上获取用户信息的步骤后，再使用OAuth2Operations的默认实现类OAuth2Template（这个也可以实现OAuth2Operations接口来自己做实现）就可以生成一个ServiceProvider对象了（通过继承抽象类AbstractOAuth2ServiceProvider）。

   ```java
   //继承AbstractOAuth2ServiceProvider类，这里AbstractOAuth2ServiceProvider有一个泛型，指的是Api（AbstractOAuth2ApiBinding）接口的类型
   public class QQServiceProvider extends AbstractOAuth2ServiceProvider<QQ>{
       
       private String appId;
       
       private static final String URL_AUTHORIZE = " https://graph.qq.com/oauth2.0/authorize";
       
       private static final String URL_ACCESS_TOKEN="https://graph.qq.com/oauth2.0/token";
       //实现抽象类AbstractOAuth2ServiceProvider必须要重写的一个构造函数，目的是向ServiceProvider传递一个OAuth2Operations的实现，在这里使用Social的一个默认实现OAuth2Template
       public QQServiceProvider(String appId, String appSecret){
           //使用OAuth2Template需要传递四个参数  1.clientId  通过注册qq互联获取到的appId 2.clientSecret  通过注册qq互联获取到的appSecret  3.authorzeUrl 这个链接是指将用户导向认证服务器时需要访问的链接（OAuth2协议认证流程图的步骤1）  4.accessTokenUrl 这个链接是指软件客户端拿着认证信息去认证服务器申请令牌时需要访问的链接（OAuth2协议认证流程图的步骤4）
           super(new OAuth2Template(appId, appSecret, URL_AUTHORIZE, URL_ACCESS_TOKEN))
               
           this.appId = appId;
       }
       
       //这个方法需要获取的就是我们对Api接口的实现，在前面分析可知api接口的实例不能是单例的，所以这里返回的是new出来的QQImpl对象
       @Overrider
       public QQ getApi(String accessToken){
           //
           return new QQImpl(accessToken, appId);
       }
   }
   ```

   

3. 通过以上步骤获取到的用户信息属性字段是由各第三方平台自己定义的，各个平台由各平台的规范，但是在Connection中，用户信息的格式是固定不变的，所以要生成Connection就必须把第三方平台的用户信息格式转换成标准的Connection格式，ApiAdapter扮演的就是这样一个适配器的角色

   ```java
   //这里ApiAdapter的泛型是最终需要适配的类型
   public class QQAdapter implements ApiAdapter<QQ>{
       
       //这里是测试qq的链接是否能调通
       @Overrider
       public boolean test(QQ api){
          return true; 
       }
       
       /**
       * ConnectionValues这个对象中包含了创建一个Connection对象需要的数据项
       * 这个方法就是真正对获取到的用户数据和Social标准的用户数据格式做对应
       */
       @Overrider
       public void setConnectionValues(QQ api, ConnectionValues values){
           //获取到用户信息
           QQUserInfo qqUserInfo = api.getUserInfo();
           
           //values这个参数有四个方法，这四个方法对应的就是四个标准的参数
           values.setDisplayName(userInfo.getNickname()); //昵称
           values.setImageUrl(userInfo.getFigureurl_qq_1()); //头像
           values.setProfileUrl(null); //个人主页（qq没有个人主页这个概念  为null）
           values.setProviderUserId(userInfo.getOpenId);
           
       }
       
       //这个方法和绑定解绑相关
       @Overrider
       public UserProfile fetchUserProfile(QQ api){
           return null;
       }
       
       //这个是类似于一个个人动态的东西，像微博的动态就属于这种消息  qq也互联中也没有开放这种数据
       @Overrider
       public void updateStatus(QQ api, String message){
           
       }
       
   }
   ```

   

4. 到这里为止，已经具备了创建ConnectionFactory的基本条件

   ```java
   
   public class QQConnectionFactory extends OAuth2ConnectionFactory<QQ>{
       
       //这里是重写了构造函数，因为appId appSecret都是需要从外部传过来的参数，这两个参数是创建serviceProvider和apiAdapter的必要参数
       public QQconnectionFactory(String providerId,String appId, String appSecret){
           
           //参数1 providerId  资源供应商的唯一标志
           //参数2 serviceProvider 就是之前的步骤创建的ServiceProvider
           //参数3 apiAdapter 前面实现的Api适配器
           super(providerId, serviceProvider, apiAdapter);
       }
   }
   ```

   

5. ConnectionFactory创建出来之后，会自动构建Connection，不需要我们手动进行处理。在将第三方数据保存到数据库之前，还需要构建UserConnectionRepository用于对数据库中的第三方用户信息进行增删改查，这个Repository Spring已经给构建好了，开发人员只需要配置相关参数就可以

   ```java
   
   @Configuration
   @EnableSocial
   public class SocialConfig extends SocaialConfigurerAdapter{
       
       //注入数据库资源
       @Autowired
       private DataSource dataSource;
       
       @Overrider
       public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator){
           
           //参数1 dataSource是指用户数据存贮所在的数据库资源
           //参数2 connectionFactoryLocator 直接用传进来的这个值就可以了，作用是根据条件查找目标ConnectionFactory（Spring中可能存在多个ConnectionFactory 因为有可能要连微信  同时也要连接qq）
           //参数3 textEncryptor 是一个对插入数据库的数据进行加解密的工具  用于保证安全性的  Encryptors.noOpText()的意思是不做加解密
           JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
           repository.setTablePrefix("这个设置是用于对数据库表设置前缀，如果数据库表用的表名是 UserConnection 就不需要设置这一项")
           return repository;
       }
   }
   ```

   

6. 在UserConnection这个据库表中是通过userId这个字段和ProviderId\providerUserId这两个字段的关联关系将我们自己的业务系统中的用户和第三方平台中的用户信息关联起来的

   * 不管是通过账号密码登陆  还是通过第三方登陆，在登陆成功后都需要向SecurityContext中注入完整的用户信息，在表单登陆中，这个功能是通过实现UserDetailsService实现的，SpringSocial要向SecurityContext中注入用户信息需要实现另外一个接口----SocialUserDetailsService

   ```java
   //自定义用户账户进行认证，实现UserDetailsService这个接口
   //关于Authorities：这个是用户授权的角色，在SpringSecurity中，Authorities是将用户与权限管理起来的桥梁，换句话说，真正拥有权限的是角色，将用户赋予某个角色，这个用户就拥有了这个角色的权限
   @Component
   public class MyUserDetailsService implements UserDetailsService, SocialUserDetailsService{
       
       //重写 loadUserByUserId方法通过用户id查找用户信息
       //这里的参数 userId 并不表示一定要用数据库表的主键，只要是用户的唯一标识就行
       @Overrider
       public SocialUserDetails loadUserByUserId(String userId){
           
           //通过用户唯一标识从数据库中查找用户信息添加到SecurityContext中
           return new SocialUser(username, password(数据库中查到的密码), authorities(用户的授权角色));
       }
       
       //重写loadUserByUsername方法是通过用户名查找用户信息
       @Overrider
       public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
           //这个方法会将权限字符串转换成对应的角色对象，并赋予用户相应的权限
           ArrayList<Authorities> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("这里传入一个角色的字符串");
           //这里返回的User是UserDetails的一个实现类，由SpringSecurity实现的
           return new User(username, password(数据库中查到的密码), authorities(用户的授权角色));
       }
   }
   ```

   

7. 到这位置，Social实现qq登陆基本完成了，只差两个配置
   1. 第三方登陆的页面
   2. Social的相关配置

```java
//通过继承SocialProperties配置qq第三方登陆的相关信息
//继承SocialProperties 会继承到两个属性  appId 和 appSecret
//然后通过添加自定义配置的方式将这个配置添加到自定义配置中就可以在配置文件中进行参数的相关配置了

public class QQProperties extends SocialProperties{
    //第三方平台的标识  固定为qq
    private String providerId="qq";
}

//设定好自定义参数的相关配置之后，还需要使用配置类将配置的参数注入到Social中
//@ConditionalOnProperty注解是限制  只有当满足某个条件的配置项有值时 这个配置类才生效
//在SpringBoot2.0中移除了SocialAutoConfigurerAdapter,直接继承SocialConfigurerAdapter类并重写createConnectionFactory方法就行
// 后补：做到处理注册逻辑的时候发现的一个bug：登录完成后，数据库没有数据，但是再次登录却不用注册了
// 就怀疑是否是在内存中存储了。结果果然发现这里父类的内存ConnectionRepository覆盖了SocialConfig中配置的jdbcConnectionRepository
// 这里需要返回null，否则会返回内存的 ConnectionRepository
 /*  @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return null;
*/
@Configuration
@ConditionalOnProperty（prefix= "...security.social.qq", name="app-id"）
public class QQAutoConfig extends SocialAutoConfigAdapter{
    @Autowired
    private SecurityProperties securityProperties;
    
    @Overrider
    protected ConnectionFactory<?> createConnectionFactory(){
        //以下的三个参数都通过 securityProperties 获取（自定义配置中）
        return new QQConnectionFactory(providerId, appId, appSecret);
    }
}

```

8.  在以上配置都完成之后，还有很关键的一步，因为SpringSecurity是通过过滤器链进行权限控制的，所以需要将Social的过滤器添加到过滤器链中

```java
//在这个配置类中添加一个SpringSocial的Bean

@Configuration
@EnableSocial
public class SocialConfig extends SocaialConfigurerAdapter{
    
    //注入数据库资源
    @Autowired
    private DataSource dataSource;
    
    @Overrider
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator){
        
        //参数1 dataSource是指用户数据存贮所在的数据库资源
        //参数2 connectionFactoryLocator 直接用传进来的这个值就可以了，作用是根据条件查找目标ConnectionFactory（Spring中可能存在多个ConnectionFactory 因为有可能要连微信  同时也要连接qq）
        //参数3 textEncryptor 是一个对插入数据库的数据进行加解密的工具  用于保证安全性的  Encryptors.noOpText()的意思是不做加解密
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
        repository.setTablePrefix("这个设置是用于对数据库表设置前缀，如果数据库表用的表名是 UserConnection 就不需要设置这一项")
        return repository;
    }
    
    @Bean
    public SpringSocialConfigurer imoocSocialSecurityConfig(){
        return new SpringSocialConfigurer();
    }
}

//然后还需要配置往过滤器链上添加一个过滤器

//在默认情况下 “/auth”开头的URL会由 SocialAuthenticationFilter这个过滤器来进行拦截，然后通过providerID确定链接的后半段  所以请求“/auth/qq”就会跳转到qq的第三方授权页面
```

经过以上处理可以通过扫qq的二维码进行登陆了，但是在登录的过程中可能会遇到三个问题：

1. redirect_url非法：引发这个问题的原因是在qq互联上配置的回调链接和程序内部的回调链接不匹配，导致带着授权码信息的请求无法进行回调，
2. 在我们进行认证请求的时候对OAuth2Operations接口的实现使用的是social提供给我们的默认实现OAuth2Template，在这个实现中对资源服务器的返回信息是当作Json数据处理的，但是qq平台返回的信息实际上是xml格式的，需要对某些类做重写。添加一个可以处理xml格式的响应数据

```java
//OAuth2Template中的exchangeForAccess方法进行获取AccessToken的处理，内部调用了postForAccessGrant这个方法

	public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        if (this.useParametersForClientAuthentication) {
            params.set("client_id", this.clientId);
            params.set("client_secret", this.clientSecret);
        }

        params.set("code", authorizationCode);
        params.set("redirect_uri", redirectUri);
        params.set("grant_type", "authorization_code");
        if (additionalParameters != null) {
            params.putAll(additionalParameters);
        }

        return this.postForAccessGrant(this.accessTokenUrl, params);
    }


    protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
        //这里使用getRestTemplate发送post请求并将返回的数据转成MAP，这里期望获取的是JSON格式的数据，但实际qq平台返回的是【text/html】格式的数据
        return this.extractAccessGrant((Map)this.getRestTemplate().postForObject(accessTokenUrl, parameters, Map.class, new Object[0]));
    }

//在上面的方法无法正常处理返回的数据，通过层层异常的抛出，最终异常处理器会将请求跳转到默认的失败路径（/signin） ,因为/sign



//为了解决以上问题，需要添加一个处理【text/html】格式数据的转换器

public QQOAuth2Template extends OAuth2Template{
    //OAuth2Template这个类中有一个属性useParametersForClientAuthentication，只有这个属性为true的时候，才会带上client_id/client_secret，所以需要在子类构造函数对这个属性默认为true
    //此外需要将QQServiceProvider中的OAuth2Template替换为QQOAuth2Template
    public QQOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String authenticateUrl, String accessTokenUrl){
        super(clientId, clientSecret, authorizeUrl, authenticateUrl, accessTokenUrl);
        setUseParametersForClientAuthentication(true);
    }
    
    //重写createRestTemplate方法
    @Override
    protected RestTemplate createRestTemplate(){
        //继承父类的RestTemplate并添加转换【text/html】格式数据的转换器
        RestTemplate restTemplate = super.createRestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTempate;
    }
    
    //在qq互联的文档中，可以看到最终返回的AccessToken数据并不是理想的JSON格式的数据，而是用&符号隔开的一个字符串，对accessToken的处理是在OAuth2Template类的postAccessGrant方法中，所以还需要重写这个方法
    @Override
     protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
         //在这个类中发送Rest请求获取qq互联返回的字符串数据
         String responseStr = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
         String[] items = StringUtils.splitByWholeSeparatorPreserveAllTokens(responseStr. "&");
         
         String accessToken = StringUtils.substringAfterLast(item[0], "=");
         Long expiresIn = new Long(StringUtils.substringAfterLast(item[1], "="));
         String refreshToken = StringUtils.substringAfterLast(item[2], "=");
        return new AccessGrant(accessToken, null, refreshToken, expiresIn);
    }
}
```



1. 在qq互联返回的accessToken信息时，返回的并不是JSON格式的数据，而是一串用&符号隔开的字符串，所以在对accessToken进行处理时，默认实现的处理方式并不能得到正确的accessToken，所以这里也需要我们根据实际的返回情况进行处理



在通过Social获取qq的用户信息成功后，用户依旧无法登陆，因为在认证逻辑中，如果没有系统用户与qq用户的绑定关系，则Security框架会将页面导向注册页（默认的注册页路径是“/signup”），又因为/signup这个路径没有访问权限，最终用户会被提示请先登录。解决这个问题的方式就是配置一个注册页。

* 根据需要获取的用户信息定义一个注册页面，并创建一个Controller用于处理注册逻辑
* 告知过滤器在查找不到用户信息的时候将用户导向我们自定义的注册页面（需要在对SpringSocialConfigurer的属性signupUrl进行设置，这个属性在SocialConfig类中）



在我们自定义的注册页面上，不管是使用已有账号与qq账号信息进行绑定还是新注册账号并绑定qq绕不过去的一点就是我们都得拿到从qq资源服务器获取到的用户信息并将其插入到数据库中。Social提供了一个工具类ProviderSignInUtils用于完成这部分功能。

```java
//首先需要将ProviderSignInUtils这个类注册到Spring中

public ProviderSignInUtils providerSignInUtils(){
    return new ProviderSignInUtils();
}
```

