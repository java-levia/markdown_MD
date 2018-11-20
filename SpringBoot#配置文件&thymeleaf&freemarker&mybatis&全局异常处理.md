#SpringBoot#
##配置文件&thymeleaf&freemarker&全局异常处理&mybatis##

1 配置文件

	1 配置文件的生效顺序会对值进行覆盖
		1 开发者工具 `Devtools` 全局配置参数；
		2 单元测试上的 `@TestPropertySource` 注解指定的参数；
		3 单元测试上的 `@SpringBootTest` 注解指定的参数；
		4 命令行指定的参数，如 `java -jar springboot.jar --name="Java技术栈"`；
		5 命令行中的 `SPRING_APPLICATION_JSONJSON` 指定参数, 如 `java -Dspring.application.json='{"name":"Java技术栈"}' -jar springboot.jar`
		6 `ServletConfig` 初始化参数；
		7 `ServletContext` 初始化参数；
		8 JNDI参数（如 `java:comp/env/spring.application.json`）；
		9 Java系统参数（来源：`System.getProperties()`）；
		10 操作系统环境变量参数；
		11 `RandomValuePropertySource` 随机数，仅匹配：`ramdom.*`；
		12 JAR包外面的配置文件参数（`application-{profile}.properties（YAML）`）
		13 JAR包里面的配置文件参数（`application-{profile}.properties（YAML）`）
		14 JAR包外面的配置文件参数（`application.properties（YAML）`）
		15 JAR包里面的配置文件参数（`application.properties（YAML）`）
		16 `@Configuration`配置文件上 `@PropertySource` 注解加载的参数；
		17 默认参数（通过 `SpringApplication.setDefaultProperties` 指定）；
		数字小的优先级越高，即数字小的会覆盖数字大的参数值。

	2 配置随机值
		roncoo.secret=${random.value}
		roncoo.number=${random.int}
		roncoo.bignumber=${random.long}
		roncoo.number.less.than.ten=${random.int(10)}
		roncoo.number.in.range=${random.int[1024,65536]}
		读取使用注解：@Value(value = "${roncoo.secret}")
		注：出现黄点提示，是要提示配置元数据，可以不配置

	3 属性占位符
		当application.properties里的值被使用时，它们会被存在的Environment过滤，所以你能够引用先前定义的值（比如，系统属性）。
		roncoo.name=www.roncoo.com
		roncoo.desc=${roncoo.name} is a domain name

	4 Application属性文件，按优先级排序，位置高的将覆盖位置低的
		1 当前目录下的一个/config子目录
		2 当前目录
		3 一个classpath下的/config包
		4 classpath根路径（root）
		这个列表是按优先级排序的（列表中位置高的将覆盖位置低的）

	5 配置应用端口和其他配置的介绍
		#端口配置：
		server.port=8090
		#时间格式化
		spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
		#时区设置
		spring.jackson.time-zone=Asia/Chongqing
		#更多配置可以查看springBoot的官方文档，里面有详细介绍

	6 使用YAML代替Properties
		注意写法：1 冒号后要加个空格
				 2 同样的上级标识符只能出现一次  比如
						spring:
							name
						spring:
							age
						这样的写法会导致报错。

	7 多环境配置
		不同的环境可以配置不同的参数，便于部署，提高效率，减少出错。
		
		1 properties文件的多环境配置
			写好application-dev.properties文件
			然后在application.properties中配置以下参数
			spring.profiles.active=dev  //配置这行参数可以激活application-dev.properties这个配置文件，从而替换application.properties文件的配置

		2 YAML多环境配置
			1 配置激活选项
				spring:
			  	  profiles:
					active: dev
			YAML文件多环境配置不需要书写多个文件  在配置文件添加三个英文状态下的短横线即可区分不同的配置环境

		3 两种配置方式的比较
			1. Properties配置多环境，需要添加多个配置文件，YAML只需要一个配件文件
			2. 书写格式的差异，yaml相对比较简洁，优雅
			3. YAML的缺点：不能通过@PropertySource注解加载。如果需要使用@PropertySource注解的方式加载值，那就要使用properties文件。

		4 多环境切换方式
			通过命令行 ： java -jar myapp.jar --spring.profiles.active=dev

2 配置server信息

	1 配置端口号
	Spring boot默认的端口是8080，如果想要进行更改的话，只需要修改application.properties文件，在配置文件中加入：
		server.port=8081

	2 配置context-path
	在application.properties进行配置：
		server.context-path=/spring-boot
		2.0之后的配置变为 server.servlet.context-path=/spring-boot
		访问地址就是http://ip:port/spring-boot

	# 实际使用中发现了一个问题：在springBoot 2.0+   spring security 5.0+的环境下，如果配置server.servlet.context-path会导致静态资源加载不上，原因是静态资源在加载的时候没有加上这个项目名

	3 其他server配置
	#server.port=8080
	#server.address= # bind to a specific NIC
	#server.session-timeout= # session timeout in seconds
	#the context path, defaults to '/'
	#server.context-path=/spring-boot
	#server.servlet-path= # the servlet path, defaults to '/'
	#server.tomcat.access-log-pattern= # log pattern of the access log
	#server.tomcat.access-log-enabled=false # is access logging enabled
	#server.tomcat.protocol-header=x-forwarded-proto # ssl forward headers
	#server.tomcat.remote-ip-header=x-forwarded-for
	#server.tomcat.basedir=/tmp # base dir (usually not needed, defaults to tmp)
	#server.tomcat.background-processor-delay=30; # in seconds
	#server.tomcat.max-threads = 0 # number of threads in protocol handler
	#server.tomcat.uri-encoding = UTF-8 # character encoding to use for URL decoding

3 spring boot使用thymeleaf

	1 在pom.xml中引入thymeleaf
	<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
	</dependency>
	
	2 如何关闭thymeleaf缓存
	########################################################
	###THYMELEAF (ThymeleafAutoConfiguration)
	########################################################
	#spring.thymeleaf.prefix=classpath:/templates/
	#spring.thymeleaf.suffix=.html
	#spring.thymeleaf.mode=HTML5
	#spring.thymeleaf.encoding=UTF-8
	# ;charset=<encoding> is added
	#spring.thymeleaf.content-type=text/html 
	# set to false for hot refresh
	#开发环境下建议关闭thymeleaf的缓存
	spring.thymeleaf.cache=false 

	3 编写模板文件.html
		thymeleaf 3.0之前，所有标签都是需要闭合的
		thymeleaf 3.0之后  标签不强制要求闭合
	编写模板文件src/main/resouces/templates/hello.html:
	
	<!DOCTYPE html>
	<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
	      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
	    <head>
	        <title>Hello World!</title>
	    </head>
	    <body>
	        <h1 th:inline="text">Hello.v.2</h1>
	        <p th:text="${hello}"></p>
	    </body>
	</html>

4 Spring Boot 使用freemarker

	1 在pom.xml中引入freemarker
	<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-freemarker</artifactId>
	</dependency>

	2 关闭freemarker缓存
	########################################################
	###FREEMARKER (FreeMarkerAutoConfiguration)
	########################################################
	spring.freemarker.allow-request-override=false
	spring.freemarker.cache=true
	spring.freemarker.check-template-location=true
	spring.freemarker.charset=UTF-8
	spring.freemarker.content-type=text/html
	spring.freemarker.expose-request-attributes=false
	spring.freemarker.expose-session-attributes=false
	spring.freemarker.expose-spring-macro-helpers=false
	#spring.freemarker.prefix=
	#spring.freemarker.request-context-attribute=
	#spring.freemarker.settings.*=
	#spring.freemarker.suffix=.ftl
	#spring.freemarker.template-loader-path=classpath:/templates/ #comma-separated list
	#spring.freemarker.view-names= # whitelist of view names that can be resolved

	3 编写模板文件.ftl
	<!DOCTYPE html>
	<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Hello World!</title>
    </head>
    <body>
        <h1>Hello.v.2</h1>
        <p>${hello}</p>
    </body>
	</html>

5 springBoot集成mybatis

	1 在添加完spring Boot的基础依赖之后，再添加mysql驱动，mybatis依赖包，mybatis分页的PageHelper：
			<!--mysql数据库驱动-->
			<dependency>
				<groupId>mysql</groupId>
				<artifactId>mysql-connector-java</artifactId>
			</dependency>

			<!-- spring-boot mybatis依赖：请不要使用1.0.0版本，因为还不支持拦截器插件， 1.1.1 是博主写帖子时候的版本，大家使用最新版本即可 -->
			<dependency>
				<groupId>org.mybatis.spring.boot</groupId>
				<artifactId>mybatis-spring-boot-starter</artifactId>
				<version>1.1.1</version>
			</dependency>

			<!-- MyBatis提供了拦截器接口，我们可以实现自己的拦截器，将其作为一个plugin装入到SqlSessionFactory中。 Github上有位开发者写了一个分页插件，我觉得使用起来还可以，挺方便的。 Github项目地址： https://github.com/pagehelper/Mybatis-PageHelper -->
			<dependency>
				<groupId>com.github.pagehelper</groupId>
				<artifactId>pagehelper</artifactId>
				<version>4.1.0</version>
			</dependency>
	2 书写Mapper接口  在接口方法上方使用@Select（“select*from ... where name=#{name}”）书写sql语句进行增删改查

	3 在启动类需要添加一个注解@MapperScan("mybatis映射文件所在包的父包")

	4 获取自增长的ID
		在Mapper接口中的插入方法上方添加注解@Options(useGenertedKeys=true,keyProperty="id",keyColumn="id")

6 全局异常处理

	在一个项目中有时候需要对异常进行统一的处理，那么如何进行处理呢？
		1 新建一个类 GlobalDefaultExceptionHandler,

		2 在class注解上@ControllerAdvice

		3 在方法上注解上@ExceptionHandler(value=Exception.class)
		具体代码如下：
		@ControllerAdvice
		@ResponseBody
		public class GlobalDefaultExceptionHandler{
			
			@ExceptionHandler(value = Exception.class)
			public void defaultErrorHandler(HttpServletRequest req, Exception e)  {
			//如果是直接返回string字符串，需要在方法上添加@ResponseBody注解
				return "服务器繁忙！"；
		}

7 动态获取请求路径中的参数
	@RequestMapping("/get/{id}/{name}")
	然后在Controller方法的参数前面使用@PathVariable Integer id ,就可以将url中的参数传递到方法中。