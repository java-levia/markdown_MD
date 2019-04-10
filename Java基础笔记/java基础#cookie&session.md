#java基础#
##cookie&session##

1 cookie
​	
	cookie是一种客户端技术，程序把每个用户的数据以cookie的形式写给用户各自的浏览器。当用户浏览器再去访问服务器中的web资源时，就会带上各自的数据去。这样，web资源处理的就是用户各自的数据了。
		* java提供的操作cookie的API中几个比较关键的方法
			* 构造方法Cookie(String name, String value) 
			* setMaxAge(int expiry)  
				- 设置cookie的最大保存时间，即cookie的有效期，当服务器给浏览器送回一个cookie时，如果没有调用该方法设置有效期，那么cookie的有效期只在一次会话过程中有效。（一次会话指的是用户打开浏览器点击多个超链接，访问服务器的多个web资源，然后关闭，这整个过程称为一次会话。）如果设置了有效期，cookie就会在浏览器上保存相应的时间，超时后cookie会消失。
			* setPath（String url）
				- 设置cookie的有效路径，比如把cookie的有效路径设置为“/Levia”，那么浏览器访问levia目录下的web资源都会带上cookie，再比如把cookie的有效路径设为“/Levia/java”,那么浏览器只有访问“java”目录下的web资源时才会带上cookie，而访问“levia”下的资源是不带cookie的。
	
		* response接口也中定义了一个addCookie方法，它用于在其响应头中增加一个相应的Set-Cookie头字段。 同样，request接口中也定义了一个getCookies方法，它用于获取客户端提交的Cookie。
		
		* 　要想在cookie中存储中文，那么必须使用URLEncoder类里面的encode(String s, String enc)方法进行中文转码，例如：
	
				# Cookie cookie = new Cookie("userName", URLEncoder.encode("孤傲苍狼", "UTF-8"));
				# response.addCookie(cookie);
				
			在获取cookie中的中文数据时，再使用URLDecoder类里面的decode(String s, String enc)进行解码，例如：
				
				# URLDecoder.decode(cookies[i].getValue(), "UTF-8")


2 session
​	
	在WEB开发中，服务器可以为每个用户浏览器创建一个会话对象（session对象），注意：默认情况下，一个浏览器独占一个session对象。因此，在需要保存用户数据时，服务器程序可以把用户数据写到用户浏览器独占的session中，当用户使用浏览器访问其他程序时，其他程序可以从用户的session中取出该用户的数据，为用户服务。
		
		* session和cookie的主要区别
			1. Cookie是把用户数据写给用户的浏览器
			2. Session技术是把用户的数据写到用户独占的session中（在服务器上）
			3. Session对象是由服务器创建，开发人员可以调用request对象的getSession方法得到session对象。
	
		* 服务器创建session出来后，会把session的id号，以cookie的形式回写给客户机，这样，只要客户机的浏览器不关，再去访问服务器时，都会带着session的id号去，服务器发现客户机浏览器带session id过来了，就会使用内存中与之对应的session为之服务
	
		* session对象的销毁机制
			* session对象默认30分钟没有使用，服务器会自动销毁session，在web.xml文件中可以手工配置session的失效时间
				# 14     <session-config>
				# 15         <session-timeout>15</session-timeout>
				# 16     </session-config>
			
		* 当需要在程序中手动设置Session失效时，可以手工调用session.invalidate方法，摧毁session。	

3 利用Session防止表单重复提交
​	
	具体做法是：
		* 在服务器端生成一个唯一的随机标识号，专业术语称为Token(令牌)，同时在当前用户的Session域中保存这个Token。然后将Token发送到客户端的Form表单中，在Form表单中使用隐藏域来存储这个Token，表单提交的时候连同这个Token一起提交到服务器端，然后在服务器端判断客户端提交上来的Token与服务器端生成的Token是否一致，如果不一致，那就是重复提交了，此时服务器端就可以不处理重复提交的表单。如果相同则处理表单提交，处理完后清除当前用户的Session域中存储的标识号。
		
		　　在下列情况下，服务器程序将拒绝处理用户提交的表单请求：
				1. 存储Session域中的Token(令牌)与表单提交的Token(令牌)不同。
				2. 当前用户的Session中不存在Token(令牌)。
				3. 用户提交的表单数据中没有Token(令牌)。


​		    
​		    
