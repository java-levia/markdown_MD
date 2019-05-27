SpringSecurity Oauth2 开发App认证框架

笔记需要结合  wps图  SpringSecurity开发App认证框架2 一起看



1. 从请求参数中获取客户端的相关信息（ClientId）
   1. 客户端的相关信息是放在请求头中，并经过了Base64编码，这样如果需要获取到原本的ClientId则需要通过转码工具类进行处理。在securityOAuth2.0框架中处理请求参数中的客户端相关信息是BasicAuthenticationFilter这个类，所以可以到BasicAuthenticationFilter这个类中复制相关代码逻辑获取ClientId

```java
//最终登陆成功处理器中的代码如下

@Component
public class MyAuthenticationSuccess implements AuthenticationSuccessHandler {

    Logger logger = LoggerFactory.getLogger(MyAuthenticationSuccess.class);

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        logger.info("登陆成功");
		//下面这一块的代码是从BasicAuthenticationFilter这个类中复制过来的
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Basic ")) {
            //extractAndDecodeHeader这个方法将编码后的字符串进行解码
            String[] tokens = this.extractAndDecodeHeader(header, request);

            assert tokens.length == 2;

            String username = tokens[0];
        } else {
            throw new UnapprovedClientAuthenticationException("请求头中无Client信息");
        }

        //把通过认证的信息返回给前端
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(authentication));
    }


    private String[] extractAndDecodeHeader(String header, HttpServletRequest request) throws IOException {
        byte[] base64Token = header.substring(6).getBytes("UTF-8");

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException var7) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, "UTF-8");
        int delim = token.indexOf(":");
        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        } else {
            return new String[]{token.substring(0, delim), token.substring(delim + 1)};
        }
    }

}
```

