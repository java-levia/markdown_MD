# JWT Token的使用

1. Jwt简介

   JWT(json web token)是为了在网络应用环境间传递声明而执行的一种基于JSON的开放标准。

   JWT的声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，以便于从资源服务器获取资源。比如用在用户登录上。

   传统的使用cookie+session的模式通常是保存在内存中，而且服务从单服务到多服务面临着session共享的问题，用户量增多，开销就会非常大。而JWT不是这样的，只需要服务端生成token，客户端保存这个token，每次请求携带这个token，服务端认证解析就可以了（也就是说服务端只生成和解析token，并不保存。客户端保存token，每次请求携带token）

2. JWT的构成

   第一部分我们称它为头部（header),第二部分我们称其为载荷（payload)，第三部分是签证（signature)。

   **header**

   jwt的头部承载两部分信息：

   - 声明类型，这里是jwt
   - 声明加密的算法 通常直接使用 HMAC SHA256

   完整的头部就像下面这样的JSON：

   {

   "typ": "JWT",

   "alg": "HS256"

   }

   然后将头部进行base64加密（该加密是可以对称解密的),构成了第一部分：

   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9

   **playload**

   载荷就是存放有效信息的地方。这个名字像是特指飞机上承载的货品，这些有效信息包含三个部分

   - 标准中注册的声明
   - 公共的声明
   - 私有的声明

   **标准中注册的声明** (建议但不强制使用) ：

   - iss: jwt签发者
   - sub: jwt所面向的用户
   - aud: 接收jwt的一方
   - exp: jwt的过期时间，这个过期时间必须要大于签发时间
   - nbf: 定义在什么时间之前，该jwt都是不可用的.
   - iat: jwt的签发时间
   - jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。

   **公共的声明 ：**

   公共的声明可以添加任何的信息，一般添加用户的相关信息或其他业务需要的必要信息.但不建议添加敏感信息，因为该部分在客户端可解密.

   **私有的声明 ：**

   私有声明是提供者和消费者所共同定义的声明，一般不建议存放敏感信息，因为base64是对称解密的，意味着该部分信息可以归类为明文信息。

   定义一个payload：

   {

   "name":"Free码农",

   "age":"28",

   "org":"今日头条"

   }

   然后将其进行base64加密，得到Jwt的第二部分：

   eyJvcmciOiLku4rml6XlpLTmnaEiLCJuYW1lIjoiRnJlZeeggeWGnCIsImV4cCI6MTUxNDM1NjEwMywiaWF0IjoxNTE0MzU2MDQzLCJhZ2UiOiIyOCJ9

   **signature**

   jwt的第三部分是一个签证信息，这个签证信息由三部分组成：

   - header (base64后的)
   - payload (base64后的)
   - secret

   这个部分需要base64加密后的header和base64加密后的payload使用.连接组成的字符串，然后通过header中声明的加密方式进行加盐secret组合加密，然后就构成了jwt的第三部分：

   49UF72vSkj-sA4aHHiYN5eoZ9Nb4w5Vb45PsLF7x_NY

   密钥secret是保存在服务端的，服务端会根据这个密钥进行生成token和验证，所以需要保护好。



   **Java的实现方式**

   ```java
   public class JWTDemo {
   	
   	private static String SECRET = "Levia";
   	public static void main(String[] args) throws Exception{
   		String token = JWTDemo.createToken(); 
   		System.out.println(token);
   		
   		//解密
   		try {
   			Map<String, Claim> map = JWTDemo.verifyToken(token);
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		
   		/*String otToken="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ"
   				+ "9.eyJvcmciOiJzYW54aW4iLCJuYW1lIjoiTGV2aWEiLCJleHAiOjE1NDUzNTkwNDcsImlhdCI6MT"
   				+ "U0NTM1ODk4NywiYWdlIjoyOH0.ARQBmIeLRpRuWaC8nJzJulBocAmnUjuos71CoVnc4tw";*/
   		/**
   		 *Token超时报错
   		 * com.auth0.jwt.exceptions.InvalidClaimException: The Token has expired on Fri Dec 21 10:24:07 CST 2018.
   			at com.auth0.jwt.JWTVerifier.assertValidDateClaim(JWTVerifier.java:434)
   			at com.auth0.jwt.JWTVerifier.verifyClaims(JWTVerifier.java:366)
   			at com.auth0.jwt.JWTVerifier.verify(JWTVerifier.java:342)
   			at com.levia.demo.JWTDemo.verifyToken(JWTDemo.java:75)
   			at com.levia.demo.JWTDemo.main(JWTDemo.java:38)
   
   		 */
   		
   		String erToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJvcmciOiJzYW54aW4iLCJuYW1lIjoiTGV2aWEiLCJleHAiOjE1NDUzNjEyNjgsImlhdCI6MTU0NTM2MTIwOCwiYWdlIjoyN30.SC1vt62ZuTqSrF6_9DToFXBrJ7VtDjHS_aJHv_E1new";
   		try {
   			Map<String, Claim> exTest = JWTDemo.verifyToken(erToken);
   			System.out.println(exTest.get("name").asString());
   			System.out.println(exTest.get("age").asInt());
   			System.out.println(exTest.get("org").asString());
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		
   		
   		/**
   		 * 更改第二段报错
   		 * com.auth0.jwt.exceptions.SignatureVerificationException: The Token's Signature resulted invalid when verified using the Algorithm: HmacSHA256
   	at com.auth0.jwt.algorithms.HMACAlgorithm.verify(HMACAlgorithm.java:49)
   	at com.auth0.jwt.JWTVerifier.verifySignature(JWTVerifier.java:349)
   	at com.auth0.jwt.JWTVerifier.verify(JWTVerifier.java:341)
   	at com.levia.demo.JWTDemo.verifyToken(JWTDemo.java:84)
   	at com.levia.demo.JWTDemo.main(JWTDemo.java:47)
   
   		 */
   		
   		/**
   		 * 更改第一段报错
   		 * com.auth0.jwt.exceptions.AlgorithmMismatchException: The provided Algorithm doesn't match the one defined in the JWT's Header.
   	at com.auth0.jwt.JWTVerifier.verifyAlgorithm(JWTVerifier.java:354)
   	at com.auth0.jwt.JWTVerifier.verify(JWTVerifier.java:340)
   	at com.levia.demo.JWTDemo.verifyToken(JWTDemo.java:94)
   	at com.levia.demo.JWTDemo.main(JWTDemo.java:47)
   
   		 */
   		
   		//可以捕获InvalidClaimException这个异常，对token进行刷新
   		//使用异常处理，捕获到超时异常时，返回一个特殊状态码，app端接收到这个状态码之后知道是token超时，重新获取一次token
   		//捕获到非法异常时，返回给app端另一个状态码，app端获得之后知道需要重新登陆。
   	}
   	
   	//加密
   	public static String createToken() throws Exception {
   		Date iatDate = new Date();
   		
   		Calendar nowTime = Calendar.getInstance();
   		nowTime.add(Calendar.MINUTE, 1);
   		Date exDate = nowTime.getTime();
   		
   		Map<String, Object> map = new HashMap<>();
   		map.put("alg", "HS256");
   		map.put("typ", "JWT");
   		String token = JWT.create()
   				.withHeader(map)
   				.withClaim("name", "Levia")
   				.withClaim("age", 27)
   				.withClaim("org", "sanxin")
   				.withExpiresAt(exDate)
   				.withIssuedAt(iatDate)
   				.sign(Algorithm.HMAC256(SECRET));
   		
   		return token;
   	}
   	
   	//解密
   	public static Map<String, Claim> verifyToken(String token) throws Exception{
   		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
   		
   		DecodedJWT jwt = null;
   		
   		jwt = verifier.verify(token);
   		
   		return jwt.getClaims();
   	}
   }
   
   ```
