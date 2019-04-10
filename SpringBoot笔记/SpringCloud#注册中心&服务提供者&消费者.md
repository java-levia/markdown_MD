#Spring Cloud#
#Springboot 2.0.0+#
##配置注册中心&服务提供者&服务消费者##

1注册中心
	使用eclipse的springboot插件创建一个springboot的基础工程（不需要start-web）
	在Pom文件中引入cloud的相关依赖

		<!-- 2.0.0以上版本的springboot整合cloud成功案例 注册中心 -->
		<parent>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-parent</artifactId>
			<version>2.0.3.RELEASE</version>
		</parent>
	
		<dependencyManagement>
			<dependencies>
				<dependency>
					<groupId>org.springframework.cloud</groupId>
					<artifactId>spring-cloud-dependencies</artifactId>
					<version>Finchley.RELEASE</version>
					<type>pom</type>
					<scope>import</scope>
				</dependency>
			</dependencies>
		</dependencyManagement>
	
		<properties>
			<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
			<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
			<java.version>1.8</java.version>
		</properties>
	
		<build>
			<plugins>
				<plugin>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-maven-plugin</artifactId>
				</plugin>
			</plugins>
		</build>
	
		<dependencies>
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter-test</artifactId>
				<scope>test</scope>
			</dependency>
	
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>
					spring-cloud-starter-netflix-eureka-server
				</artifactId>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter</artifactId>
			</dependency>
		</dependencies>


	在springBoot项目的启动类上添加注解@EnableEurekaServer

	在配置文件中配置好注册中心的相关参数。配置如下：
	#eureka.client.serviceUrl.defaultZone=http://${eureka.ip}:${eureka.port}/eureka/   //注册中心地址
    #server.port=7775   //服务端口号 
    #spring.application.name=face  //服务名称
    #eureka.instance.status-page-url=http://${spring.cloud.client.ipAddress}:${server.port}/swagger-ui.html   //swagger接口可视化
    #eureka.instance.preferIpAddress=true   //显示服务ip地址
    #eureka.instance.instance-id=${spring.cloud.client.ipAddress}:${server.port}  //显示服务端口号
    #spring.cloud.config.enabled=false  //远程配置中心关闭
    #eureka.client.register-with-eureka=false  //避免自己在服务中心注册（调试中没有启动服务注册中心时候用）
    #eureka.client.fetch-registry=false   //表示是否从Eureka Server获取注册信息，默认为true。因为这是一个单点的Eureka Server，不需要同步其他的Eureka Server节点的数据，故而设为false。

2 服务提供者
	
	1 服务提供者所需要的cloud依赖和注册中心是一样的，但服务提供者还需要其他依赖  例如WEB等
	2 在springBoot项目的启动类上添加注解@EnableDiscoveryClient
	3 配置文件中配置好提供者的相关参数  包括端口  服务名称  注册中心地址

3 服务消费者
	1 服务消费者所需要的cloud依赖和注册中心是一样的，但服务消费者者还需要其他依赖  例如WEB等
	2 在springBoot项目的启动类上添加注解@EnableDiscoveryClient  同时注册一个RestTemplate在容器中
	3 配置文件中配置好提供者的相关参数  包括端口  服务名称  注册中心地址
	4 在controller中获取到restTemplate ，利用
		#restTemplate.getForEntity("http://ip(域名):端口/方法/"+id, Student.class).getBody();调用服务

4 负载均衡（feign实现）
	引入依赖
		<dependency> 
			<groupId>org.springframework.cloud</groupId> 
			<artifactId>spring-cloud-starter-openfeign</artifactId> 
		</dependency>

	在启动类上添加注解@EnableFeignClients开启负载均衡

	创建一个接口，接口方法对应服务提供者的controller方法，并在可接口上使用@FeignClient(value="eureka-provider")注解，value值为服务名称

		@FeignClient(value="eureka-provider")
		public interface GetInfo {
			@RequestMapping(value = "/findbyid/{id}", method=RequestMethod.GET)
			public Student findById(@PathVariable("id") Integer id);
		}

	