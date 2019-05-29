SpringSecurity：图形验证码/记住我功能

1. 开发生成图形验证码的接口

   1. 根据随机数生成图片
   2. 将随机数存到Session中
   3. 将生成的图片写到接口的响应中

   * 创建一个实体类用于保存验证信息

   ```java
   @Data
   @AllArgsConstract
   public class ImageCode{
       private BufferedImage image; //验证码图片
       private String code; //验证码中包含的信息
       private LocalDateTime expireTime; //过期时间点
       
       //由于在设置过期时间的时候一般都是设置一个时间段，而做是否过期的校验时一般是比对的时间点，所以在这里需要做一些处理
       public ImageCode(BufferedImage image, String code, int expireIn){
   		this.image = image;
           this.code = code;
           this.expireTime = LocalDateTime.now().plusSeconds(expireIn); //通过当前时间加上过期时间段计算出过期的时间点
       }
   }
   
   //写一个控制器类用于生成ImageCode并返回图片给登陆页
   public class ValidateCodeComtroller{
       //SESSION的ID
       private static final String SESSION_KEY = "SESSION_KEY_IMAGE_CODE";
       //Spring管理的一个用于处理session的对象
       private SessionStrategy sessionStrategy = new HttpSessionStrategy();
       @GetMapping("/code/image")
       public void createCode(HttpServletRequest request, HttpServletResponse response){
           //调用ImageCode生成方法生成一个ImageCode
           ImageCode imageCode = createImageCode(request);
           //调用sessionStrategy将ImageCode信息保存到Session中
           sessionStrategy.setAttribute(new ServletWebRequest(request), SESSION_KEY, imageCode);
           //把生成的验证码图片写回到注册登陆页
           ImageIO.write(imageCode.getImage(), "JPEG", response.getOutputStream());
       }
   }
   
   
   private ImageCode createImageCode(HttpServletRequest request){
       //这个方法在网上有各种实现
   }
   ```

   

2. 在认证流程中加入图形验证码校验

   SpringSecurity安全验证的核心就是一条过滤器链，对于我们自定义的验证方式（图形验证码就属于这种），security并没有提供默认的实现，所以需要我们自己在过滤器链上添加一个我们自定义的过滤器,在自己写的这个过滤器中做验证码的校验逻辑

   认证的流程是，先经过我们自定义的过滤器，如果验证通过再经过UsernamePasswordAuthenticationFilter

   ```java
   //实现一个自定义的过滤器,继承OncePerRequestFilter
   
   public class ValidateCodeFilter extends OncePerRequestFilter{
       
       @override
       protected void dofilterInternal(HttpServletRequest request, HttpServletResponse response){
           
           //在这里进行一些业务判断，比如：只有在登陆的时候这个过滤器才生效，并且必须是post请求
           if(StringUtils.equals("登陆表单请求路径"， request.getRequestURI()) 
              && StringUtils.equals(request.getMethod(), "post")
             ){
               try{
                   //校验验证码的逻辑
                   validate(new ServletWebRequest(request));
                   //如果校验失败会抛出一个自定义的ValidateCodeException异常（自定义异常继承AuthenticationException异常）
               }catch(ValidateCodeException e){
                   //在捕获到异常后，使用自定义的登陆失败处理器对这个异常进行处理（AuthenticationFailureHandler）
                   //这个authenticationFailureHandler错误处理器需要从外部注入（set/get方式）
                   authenticationFailureHandler.onAuthenticationFailure(request,  response, e)
                       return;
               }
           }
           filterChain.doFilter(request, response);
       }
       
       //校验验证码的方法
       private void validate(ServletWebRequest request){
           //使用SessionStrategy从Session中拿出之前放入其中的验证码信息
           sessionStrategy.getAttribute(request, ValidateCodeController.SESSION_KEY);
           //使用ServletRequestUtils从请求参数中获取输入的验证码值
           ServletRequestUtils.getStringParameter(request.getRequest, "验证码参数名")；
               
               //校验是否为空和是否匹配/过期
               
               //校验完成之后将验证码信息从Session中移除
       }
   }
   ```

   自定义的过滤器还需要加入到Security的过滤器链中才能生效

   ```java
   
   //在Security的配置类中加入
   //http.addFilterBefore("需要加入的过滤器实例"， "加入到哪个过滤器之前")
   http.addFilterBefore(validateCodeFilter， UsernamePasswordAuthenticationFilter.class)
   ```

   

   问题处理

   在登陆页面没有输入验证码的情况下，如果直接点登陆会有以下几个问题：

   1. 失败处理器除了会将错误信息返回给前端以外，还会将大量的堆栈信息返回回去，这种情况可以在失败处理器中进行处理，使之只返回错误信息（）

      ```java
      response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponse(exception).getMessage()))
      ```

   2. 在验证码认证失败后，security还是调用了后续的认证（在打印堆栈信息的时候把用户信息也打印出来了），这里的处理方式是直接在处理完认证失败的异常后直接return；就不会再调用下面的过滤器链了