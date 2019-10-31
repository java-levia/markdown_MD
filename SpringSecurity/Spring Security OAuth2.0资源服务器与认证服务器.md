Spring Security OAuth2.0资源服务器与认证服务器



1. 微服务环境下，认证服务与资源服务分离

   * 认证服务器（发放Token）：

     1. 配置Security用于控制请求（继承WebSecurityConfigurerAdapter 类）
     2. 创建JwtToken配置类，配置jwtTokenStore作为TokenStore
     3. 配置认证服务器用于认证用户登录信息（继承AuthorizationServerConfigurerAdapter ）
        1. 重写configure(ClientDetailsServiceConfigurer clients)方法用于定义客户端client信息的获取（从内存inMemory /从数据库 jdbc）
        2. 重写configure(AuthorizationServerEndpointsConfigurer endpoints)方法用于定义端点的行为（使用什么类型的token/增强Token）
     4. 在successHandler里面自定义进行token发放

   * 资源服务器：

     1. 创建JwtToken配置类，配置jwtTokenStore作为TokenStore，并配置与认证服务器相同的signingKey.
2. 创建配置类集成ResourceServerConfigurerAdapter并重写configure(HttpSecurity http)用于配置鉴权FilterChain的行为；重写configure(ResourceServerSecurityConfigurer resources)用于定义认证Token的配置，并将步骤1定义的jwtTokenStore配置到其中，并且定义认证失败后的行为（默认跳转到认证服务器的登陆页面）
     
     ​	