使用SpringSocial开发第三方登陆

首先明确一点：遵循Oauth2协议开发第三方登陆大体上分为七个步骤：

 1. 将用户导向认证服务器

 2. 用户同意授权

 3. 返回client并携带授权码

 4. 申请令牌

 5. 发放令牌

 6. 获取用户信息

 7. 根据用户信息构建Authentication并放入SecurityContext

    

1. ServiceProvider接口
   * 这个接口是一个针对服务提供商的抽象，针对每一个不同的服务提供商，都需要提供一个ServiceProvider接口的实现。Spring Social为我们提供了AbstractOAuth2ServiceProvicer抽象类，这个类实现了ServiceProvider中一些不同服务提供上都共有的东西，在我们自己动手实现各个服务提供商的ServiceProvider实现类时，都可以先继承这个抽象类  
   * 在通过OAuth协议从第三方获取用户信息的过程中，前面的几个步骤如获取授权码、获取令牌等都是相同的，但在获取用户信息的时候，由于各服务提供商提供的信息格式或字段都是不一样的，所以到最终获取信息的时候需要开发者自己完成一些代码进行这些信息的获取。针对以上的情况，在ServiceProvider里分别有两个封装：
     *  OAuth2Operations接口（这个接口是帮开发者完成了如获取授权码、获取令牌等一些OAuth2协议的标准步骤   步骤1-5的实现 ）(实现类OAuth2Template已经帮助开发者完成了部分OAuth协议的执行流程)
     * Api接口（这个接口实际上没有一个明确的接口，因为每个服务提供商对用户信息的提供都是有区别的，这种方式我们得自己写接口来封装获取用户信息的行为。Spring Social也为我们提供了一个抽象类AbstractOAuth2ApiBinding来帮助开发者更快实现     步骤6的实现 ）
     
   * 整个ServiceProvider的实现类封装的是和服务提供商相关的东西（也就是步骤1-6的行为），到步骤7的时候就和服务提供商没有关系了，第七步整个是在我们的第三方应用内部完成的
   
   