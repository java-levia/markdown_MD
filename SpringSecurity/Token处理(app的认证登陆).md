Token处理(app的认证登陆)

1. 对SpringSecurity的默认Token进行替换

   ```java
   @Configuration
   @EnableAuthorizationServer
   //通过继承AuthorizationServerConfigurerAdapter类并重写其中的方法可以自定义认证服务器的行为
   public class MyAuthorizationConfig extends AuthorizationServerConfigurerAdapter {
       //由于SecuritySocial已经把四种认证模式都实现了
       // 所以实现认证服务器只需要加上@EnableAuthorizationServer这个注解就可以使用认证服务器
       @Autowired
       private AuthenticationManager authenticationManager;
       @Autowired
       private UserDetailsService userDetailsService;
       /**
        * 重写这个方法可以自定义登陆请求入口点的行为
        * 重写了这个方法后，需要手动指定AuthenticationManager和UserDetails
        * @param endpoints
        * @throws Exception
        */
       @Override
       public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
           endpoints.authenticationManager(authenticationManager)
                   .userDetailsService(userDetailsService);
       }
   
       /**
        * 重写这个方法可以自定义和客户端相关的逻辑，（客户端指的是通过认证服务器访问资源服务器资源的所有角色，简单来说就是会获取令牌的角色）
        * 重写了这个方法之后，原本配置在文件中的clientId和clientSecret就不再起作用了，而是通过重写的这个方法中的逻辑决定给哪些客户端发放令牌
        * @param clients
        * @throws Exception
        */
       @Override
       public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
           //这里Client有两种设置方式，一个是inMemory直接存储在内存中，这种方式适用于只有少量且不怎么变动的客户端访问资源服务的情况，还有一种方式
           //jdbc，这种方式适用于把部分资源数据开放给其他应用使用的第三方平台。
           clients.inMemory().withClient("levia_client")
                   .secret("levia_secret")
                   //发出的令牌的有效时间
                   .accessTokenValiditySeconds(7200)
                   //指定认证模式，加上这行配置后，认证服务器只支持所指定的认证方式，其他三种认证方式无法再使用
                   .authorizedGrantTypes("refresh_token","password")
                   //指定发放的权限,这里配置了这个参数后，认证请求就不需要带上scope参数，系统会直接使用这里所配置的scope参数，这里可以配置多个
                   .scopes("all");
       }
   }
   ```

2. 使用Redis存储Security的登陆令牌

   * Security中默认是将用户的登陆令牌存贮在内存中，这样的存储会有一个问题，一旦服务重启，之前存储在内存中的所有令牌都会消失，所以需要将令牌存储在一个具有持久化能力的容器中

   ```java
   /**
    * 认证令牌存储配置类
    Security中和令牌存储相关的类是TokenStore，通过给TokenStore注入一个Redis连接工厂，可以将Token存储到Redis中
    */
   @Configuration
   public class TokenStoreConfig {
       @Autowired
       private RedisTemplate redisTemplate;
   
       @Bean
       public TokenStore redisTokenStore(){
           return new RedisTokenStore(redisTemplate.getConnectionFactory());
       }
   }
   
   
   
   //然后在认证服务器中注入TokenStore
   
   @Configuration
   @EnableAuthorizationServer
   //通过继承AuthorizationServerConfigurerAdapter类并重写其中的方法可以自定义认证服务器的行为
   public class MyAuthorizationConfig extends AuthorizationServerConfigurerAdapter {
       //由于SecuritySocial已经把四种认证模式都实现了
       // 所以实现认证服务器只需要加上@EnableAuthorizationServer这个注解就可以使用认证服务器
       @Autowired
       private AuthenticationManager authenticationManager;
       @Autowired
       private UserDetailsService userDetailsService;
       @Autowired
       private TokenStore tokenStore;
   
       /**
        * 重写这个方法可以自定义登陆请求入口点的行为
        * 重写了这个方法后，需要手动指定AuthenticationManager和UserDetails
        * @param endpoints
        * @throws Exception
        */
       @Override
       public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
           endpoints.authenticationManager(authenticationManager)
               	//注入TokenStore
                   .tokenStore(tokenStore)
                   .userDetailsService(userDetailsService);
       }
   
   
       /**
        * 重写这个方法可以自定义和客户端相关的逻辑，（客户端指的是通过认证服务器访问资源服务器资源的所有角色，简单来说就是会获取令牌的角色）
        * 重写了这个方法之后，原本配置在文件中的clientId和clientSecret就不再起作用了，而是通过重写的这个方法中的逻辑决定给哪些客户端发放令牌
        * @param clients
        * @throws Exception
        */
       @Override
       public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
           //这里Client有两种设置方式，一个是inMemory直接存储在内存中，这种方式适用于只有少量且不怎么变动的客户端访问资源服务的情况，还有一种方式
           //jdbc，这种方式适用于把部分资源数据开放给其他应用使用的第三方平台。
           clients.inMemory().withClient("levia_client")
                   .secret("levia_secret")
                   //发出的令牌的有效时间
                   .accessTokenValiditySeconds(7200)
                   //指定认证模式，加上这行配置后，认证服务器只支持所指定的认证方式，其他三种认证方式无法再使用
                   .authorizedGrantTypes("refresh_token","password")
                   //指定发放的权限,这里配置了这个参数后，认证请求就不需要带上scope参数，系统会直接使用这里所配置的scope参数，这里可以配置多个
                   .scopes("all");
       }
   }
   
   
   ```

   

3. Security中使用JWTtoken替换Security的原生Token

   JWT具有自包含、密签、可扩展特性；

   自包含指的是在JWT中可以包含一部分信息：
   
   1.  使用Redis存储Security默认的令牌，最终存储的同时还有令牌所对应的一些用户的信息，这种方式的一个特点是它会依赖存储，一旦存储出现问题，客户端所持有的令牌也将毫无用处；JWTtoken则不一样，JWT本身是可以包含一部分信息的，它并不依赖于存储，只要服务端解析客户端发送过来的JWTtoken就可以获得token中的这部分信息；
   2. 密签：JWTtoken中是不能包含敏感信息的，比如登陆密码之类的；但是JWTtoken可以使用密签防止token中的信息被篡改，但是并不具备加密功能；
   3. 可拓展：JWT中的信息是可以自定义进行添加的，具有拓展性
   
   配置JwtToken替换Security自带Token
   
   ```java
   
   
   @Configuration
   public class TokenStoreConfig {
       @Autowired
       private RedisTemplate redisTemplate;
   
       @Bean
       @ConditionalOnProperty(prefix = "levia.security.oauth2", name = "storeType", havingValue = "redis")
       public TokenStore redisTokenStore(){
           return new RedisTokenStore(redisTemplate.getConnectionFactory());
       }
   
       @Configuration
       //添加一个静态内部类用于定义JwtTOKEN的配置
       @ConditionalOnMissingBean
       //下面的这个配置的意思是：检查前缀为 levia.security.oauth2 的配置，如果 storeType = jwt,以下配置生效，matchIfMissing = true 表示如果根本没配置这个参数，以下配置也生效
       @ConditionalOnProperty(prefix = "levia.security.oauth2", name = "storeType", havingValue = "jwt", matchIfMissing = true)
       public static class JwtTokenConfig{
           @Autowired
           private SecurityProperties securityProperties;
   
           //使用JWTtoken需要配置多个Bean
           @Bean
           public TokenStore jwtTokenStore(){
               return new JwtTokenStore(jwtAccessTokenConverter());
           }
   
           //这个Bean用于JwtToken的生成
           @Bean
           public JwtAccessTokenConverter jwtAccessTokenConverter(){
               JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
               //设置用于Token签名的密钥
               accessTokenConverter.setSigningKey(securityProperties.getOAuth2AppProperties().getJwtSecretKey());
               return accessTokenConverter;
           }
       }
   }
   
   
   //另外还得在MyAuthorizationConfig中判断是否存在jwtAccessTokenConverter，如果存在，则将accessTokenConverter设置为jwtAccessTokenConverter
   @Configuration
   @EnableAuthorizationServer
   //通过继承AuthorizationServerConfigurerAdapter类并重写其中的方法可以自定义认证服务器的行为
   public class MyAuthorizationConfig extends AuthorizationServerConfigurerAdapter {
       //由于SecuritySocial已经把四种认证模式都实现了
       // 所以实现认证服务器只需要加上@EnableAuthorizationServer这个注解就可以使用认证服务器
       @Autowired
       private AuthenticationManager authenticationManager;
       @Autowired
       private UserDetailsService userDetailsService;
       @Autowired
       private TokenStore tokenStore;
       @Autowired
       private SecurityProperties securityProperties;
       @Autowired(required = false)
       private JwtAccessTokenConverter jwtAccessTokenConverter;
   
       /**
        * 重写这个方法可以自定义登陆请求入口点的行为
        * 重写了这个方法后，需要手动指定AuthenticationManager和UserDetails
        * @param endpoints
        * @throws Exception
        */
       @Override
       public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
           endpoints.authenticationManager(authenticationManager)
                   .tokenStore(tokenStore)
                   .userDetailsService(userDetailsService);
   
           if(jwtAccessTokenConverter!=null){
               //这里配置的意思是，如果jwtAccessTokenConverter不为空，则使用jwtAccessTokenConverter作为TokenConverter
               endpoints.accessTokenConverter(jwtAccessTokenConverter);
           }
       }
   
   
       /**
        * 重写这个方法可以自定义和客户端相关的逻辑，（客户端指的是通过认证服务器访问资源服务器资源的所有角色，简单来说就是会获取令牌的角色）
        * 重写了这个方法之后，原本配置在文件中的clientId和clientSecret就不再起作用了，而是通过重写的这个方法中的逻辑决定给哪些客户端发放令牌
        * @param clients
        * @throws Exception
        */
       @Override
       public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
           //这里Client有两种设置方式，一个是inMemory直接存储在内存中，这种方式适用于只有少量且不怎么变动的客户端访问资源服务的情况，还有一种方式
           //jdbc，这种方式适用于把部分资源数据开放给其他应用使用的第三方平台。
           for (int i = 0; i < securityProperties.getOAuth2AppProperties().getJwtSecretKey().length(); i++) {
               InMemoryClientDetailsServiceBuilder builder = clients.inMemory();
               if(!ArrayUtils.isEmpty(securityProperties.getOAuth2AppProperties().getClients())){
                   for (MyOAuth2ClientProperties client : securityProperties.getOAuth2AppProperties().getClients()) {
                       builder.withClient(client.getClientId())
                               .secret(client.getClientSecret())
                               //发出的令牌的有效时间
                               .accessTokenValiditySeconds(client.getAccessTokenValiditySeconds())
                               //指定认证模式，加上这行配置后，认证服务器只支持所指定的认证方式，其他三种认证方式无法再使用
                               .authorizedGrantTypes("refresh_token","password")
                               //指定发放的权限,这里配置了这个参数后，认证请求就不需要带上scope参数，系统会直接使用这里所配置的scope参数，这里可以配置多个
                               .scopes("all");
                   }
               }
           }
   
       }
   }
   
   ```
   
   往返回的JWTtoken里面添加更多信息

