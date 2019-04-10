#SpringBoot#
##FastJson/热部署##

1.Spring Boot完美使用FastJson解析JSON数据
	
	1 引入fastjson依赖库
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>1.2.15</version>
		</dependency>
		
	这里要说下很重要的话，官方文档说的1.2.10以后，会有两个方法支持HttpMessageconvert，一个是FastJsonHttpMessageConverter，支持spring 4.2以下的版本，一个是FastJsonHttpMessageConverter4支持spring 4.2以上的版本，具体有什么区别暂时没有深入研究。这里也就是说：低版本的就不支持了，所以这里最低要求就是1.2.10+。

	2 配置fastjson（支持两种方法）
		* 启动类继承  extends WebMvcConfigurerAdapter
		++++++++++++++++++++++++++++++++++++++++++++++++++++

		@SpringBootApplication
		public class ApiCoreApp  extends WebMvcConfigurerAdapter {
	
		@Override
		public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    		super.configureMessageConverters(converters);
		
        	FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        	FastJsonConfig fastJsonConfig = new FastJsonConfig();
        	fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat
        	);
        	fastConverter.setFastJsonConfig(fastJsonConfig);
		
    		converters.add(fastConverter);
			}
		}
		+++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		* 覆盖方法configureMessageConverters
		* 在App.java启动类中注入Bean：HttpMessageConverters
		++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
	// 1、需要先定义一个 convert 转换消息的对象;
	FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
	
	//2、添加fastJson 的配置信息，比如：是否要格式化返回的json数据;
	FastJsonConfig fastJsonConfig = new FastJsonConfig();
	fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
	
	//3、在convert中添加配置信息.
	fastConverter.setFastJsonConfig(fastJsonConfig);
	
	
	HttpMessageConverter<?> converter = fastConverter;
	return new HttpMessageConverters(converter);
	}
	 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		

2 Spring-Boot 添加devtools实现热部署
	
	1 spring-boot-devtools 是一个为开发者服务的一个模块，其中最重要的功能就是自动应用代码更改到最新的App上面去。原理是在发现代码有更改之后，重新启动应用，但是速度比手动停止后再启动还要更快，更快指的不是节省出来的手工操作的时间。

	2 其深层原理是使用了两个ClassLoader，一个Classloader加载那些不会改变的类（第三方Jar包），另一个ClassLoader加载会更改的类，称为  restart ClassLoader

	3 这样在有代码更改的时候，原来的restart ClassLoader 被丢弃，重新创建一个restart ClassLoader，由于需要加载的类相比较少，所以实现了较快的重启时间（5秒以内）。
	
	4 添加依赖包

		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
           <scope>true</scope>
		</dependency>

		<build>
			<plugins>
		    	<plugin>
	            	<groupId>org.springframework.boot</groupId>
	           		<artifactId>spring-boot-maven-plugin</artifactId>
	            	<configuration>
	          <!--fork :  如果没有该项配置，这个devtools不会起作用，即应用不会restart -->
	                	<fork>true</fork>
	            	</configuration>
	        	</plugin>
			</plugins>
		</build>

	5 最终效果
		修改类-->保存：应用会重启
		修改配置文件-->保存：应用会重启
		修改页面-->保存：应用会重启，页面会刷新（原理是将spring.thymeleaf.cache设为false）
