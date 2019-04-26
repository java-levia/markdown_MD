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
         public QQImpl(String accessToken, String openId){
             //调用父类的构造函数将accessToken传入到父类属性中
             //在默认的Token策略中，会在发请求的时候将accessToken这个参数放在请求头中，但是在qq互联的规则中需要将accessToken这个参数放在请求参数中，显然默认的行为不符合我们的需求，所以传入TokenStrategy.ACCESS_TOKEN_PARAMETER这个参数的传入，是为了定义符合qq互联携带accessToken参数的要求
             super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
             
             this.openId=openId;
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

   

   

   ​	