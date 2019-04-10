## SpringClound服务注册与发现原理

1. RPC（Remote Procedure Call Protocol   远程调用协议）远程调用框架的核心设计思想：在于注册中心。因为注册中心管理每个服务与服务之间的依赖关系（即服务治理的概念）

2. 服务治理基本概念

   1. 在传统的远程调用框架中，管理每个服务与服务之间的依赖关系比较复杂。
      * 在传统的远程调用框架中，如果一个服务需要调用另外一个服务，需要使用HttpClient进行调用，（传统的做法是，在服务器中保存一个服务名称和服务地址的键值对，需要调用时查数据库获取对应服务的服务地址赋值到HTTPClient中，但是这样效率非常低）
   2. 在服务之间的依赖非常多的情况下，服务URL管理起来非常复杂，在这个时候可以使用服务治理技术，管理每个服务与服务之间的依赖关系，可以实现**本地**负载均衡、服务发现与注册、容错等。

3. 服务的注册与发现原理

   1. 在任何RPC远程框架中，都会有一个注册中心（SpringCloud支持三种注册中心：Eureka zookeeper consul）
      1. 注册中心的概念：存放服务地址相关信息（服务的提供者在启动的时候会将服务的基本信息，比如服务地址和端口，以别名的方式注册到注册中心上去）
   2. 微服务的负载均衡：
      1. 微服务的负载均衡不同于nginx实现的负载均衡，微服务的为本地负载均衡，与nginx的服务器负载均衡有区别。微服务是通过在注册中心获取服务提供者后在消费者本地实现的负载均衡

4. SpringCloud注册中心

   1. 依赖

      ```xml
      <project xmlns="http://maven.apache.org/POM/4.0.0"
      	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      	<modelVersion>4.0.0</modelVersion>
      	<groupId>com.Levia</groupId>
      	<artifactId>springcloud-eureka</artifactId>
      	<version>0.0.1-SNAPSHOT</version>
      
      	<parent>
      		<groupId>org.springframework.boot</groupId>
      		<artifactId>spring-boot-starter-parent</artifactId>
      		<version>2.0.1.RELEASE</version>
      	</parent>
      	<!-- 管理依赖 -->
      	<dependencyManagement>
      		<dependencies>
      			<dependency>
      				<groupId>org.springframework.cloud</groupId>
      				<artifactId>spring-cloud-dependencies</artifactId>
      				<version>Finchley.M7</version>
      				<type>pom</type>
      				<scope>import</scope>
      			</dependency>
      		</dependencies>
      	</dependencyManagement>
      	<dependencies>
      		<!-- springboot整合eureka客户端 -->
      		<dependency>
      			<groupId>org.springframework.cloud</groupId>
      			<artifactId>
      				spring-cloud-starter-netflix-eureka-server
      			</artifactId>
      		</dependency>
      	</dependencies>
      	<!-- 注意 这里必须要加 不然会有各种依赖问题 -->
      	<repositories>
      		<repository>
      			<id>spring-milestones</id>
      			<name>Spring Milestones</name>
      			<url>https://repo.spring.io/libs-milestone</url>
      			<snapshots>
      				<enabled>false</enabled>
      			</snapshots>
      		</repository>
      	</repositories>
      </project>
      ```

   2. 配置文件application.yml

      ```yaml
      #服务端口号
      server:
       port: 8100
      #eureka相关配置
      eureka:
       instance:
      #注册中心ip地址
        hostname: 127.0.0.1
       client:
        serviceUrl:
      #注册中心地址
         defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka
      #是否需要注册到注册中心
        register-with-eureka: false
      #是否需要到注册中心去检索信息（因为自己是注册中心，所以不需要去注册中心检索信息）
        fetch-registry: false    
      ```

      

5. 服务提供者和消费者

   1. 依赖

      ```xml
      <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
        <groupId>com.levia</groupId>
        <artifactId>springcloud-eureka-consumer</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <parent>
      		<groupId>org.springframework.boot</groupId>
      		<artifactId>spring-boot-starter-parent</artifactId>
      		<version>2.0.1.RELEASE</version>
      	</parent>
      	<!-- 管理依赖 -->
      	<dependencyManagement>
      		<dependencies>
      			<dependency>
      				<groupId>org.springframework.cloud</groupId>
      				<artifactId>spring-cloud-dependencies</artifactId>
      				<version>Finchley.M7</version>
      				<type>pom</type>
      				<scope>import</scope>
      			</dependency>
      		</dependencies>
      	</dependencyManagement>
      	<dependencies>
      		<!-- springBoot整合web组件 -->
      		<dependency>
      			<groupId>org.springframework.boot</groupId>
      			<artifactId>spring-boot-starter-web</artifactId>
      		</dependency>
      		<!-- springboot整合eureka客户端 -->
      		<dependency>
      			<groupId>org.springframework.cloud</groupId>
      			<artifactId>
      				spring-cloud-starter-netflix-eureka-client
      			</artifactId>
      		</dependency>
      	</dependencies>
      	<!-- 注意 这里必须要加 不然会有各种依赖问题 -->
      	<repositories>
      		<repository>
      			<id>spring-milestones</id>
      			<name>Spring Milestones</name>
      			<url>https://repo.spring.io/libs-milestone</url>
      			<snapshots>
      				<enabled>false</enabled>
      			</snapshots>
      		</repository>
      	</repositories>
      </project>
      ```

      

   2. 配置文件

      ```yaml
      #服务提供者的端口号
      server:
       port: 8000
      # 服务别名  serverid
      spring:
       application:
        name: app-levia-member
      eureka:
       client:
        service-url:
      #当前会员服务注册到eureka服务（指定注册中心地址）
         defaultZone: http://localhost:8100/eureka
      #当前服务是否需要注册到注册中心
        register-with-eureka: true
      #需要去注册中心检索信息
        fetch-registry: true
      ```

      

6. 在SpringCloud中有两种调用服务提供者的方式，一种是由SpringBoot提供的模板RestTemplate，另一种是由SpringCloud提供的Fegin。在eureka中默认整合了负载均衡器ribbon

   1. RestTemplate底层是用的HttpClient，eureka底层也是使用的HttpClient（在依赖中由HttpClient的jar包）
   2. //在配置好别名后重启服务，立即访问该服务会报错，别名所代表的服务找不到，原因是restTemplate使用别名调用需要依赖Ribbon负载均衡器（需要在RestTemplate注册Bean时加上@LoadBalanced注解）

7. 消费者代码

   ```java
   //控制层
   package com.levia.api.controller;
   
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RestController;
   import org.springframework.web.client.RestTemplate;
   
   @RestController
   public class ConsumerController {
   	
   	@Autowired
   	private RestTemplate template;
   	
   	@GetMapping("/getMember")
   	public String getMember() {
   		//在这里有两种调用方式 一种是采用服务别名的调用方式，另一种是直接调用
   		//这种直接调用的方式是不合理的，一旦涉及到集群的方式，同时存在多个服务时，这种方式就无法达到负载均衡的目的了
   		//String result = template.getForObject("http://127.0.0.1:8000/getMember", String.class);
   		
   		//使用别名的方式调用
   		//在配置好别名后重启服务，立即访问该服务会报错，别名所代表的服务找不到，原因是restTemplate使用别名调用的时候默认是需要负载均衡的  所以需要使用@loadBalance开启负载均衡 需要依赖Ribbon负载均衡器（需要在RestTemplate注册Bean时加上@loadBalanced注解）
   		String url="http://app-levia-member/getMember";
   		String result = template.getForObject(url, String.class);
   		return result;
   	}
   }
   
   
   
   
   //启动器
   package com.levia.api.controller;
   
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.cloud.client.loadbalancer.LoadBalanced;
   import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
   import org.springframework.context.annotation.Bean;
   import org.springframework.web.client.RestTemplate;
   
   @SpringBootApplication
   @EnableEurekaClient
   public class ConsumerApp {
    
   	public static void main(String[] args) {
   		SpringApplication.run(ConsumerApp.class, args);
   	}
   	
   	//如果不将RestTemplate注册到SpringBoot容器中，在启动该服务的时候会报RestTemplate找不到的错误
   	//使用@Bean的方式注入的
       ////在配置好别名后重启服务，立即访问该服务会报错，别名所代表的服务找不到，原因是restTemplate使用别名调用的时候默认是需要负载均衡的  所以需要使用@loadBalance开启负载均衡 需要依赖Ribbon负载均衡器（需要在RestTemplate注册Bean时加上@loadBalanced注解）
   	@Bean
   	@LoadBalanced
   	RestTemplate restTemplate() {
   		return new RestTemplate();
   	}
   }
   
   
   ```

8. Eureka集群服务

   1. 注册中心集群

      ```yaml
      #服务端口号
      server:
       port: 8200
       #配置注册中心的别名
      spring:
       application:
        name: app-levia-server8200
      #eureka相关配置
      eureka:
       instance:
      #注册中心ip地址
        hostname: 127.0.0.1
       client:
        serviceUrl:
      #注册中心地址(在集群的时候，defaultZone的port是其他注册中心)
         defaultZone: http://${eureka.instance.hostname}:8100/eureka
      #是否需要注册到注册中心如果时单机注册中心，不需要去注册中心检索信息；如果是集群，则需要）
        register-with-eureka: true
      #是否需要到注册中心去检索信息（如果时单机注册中心，不需要去注册中心检索信息；如果是集群，则需要）
        fetch-registry: true         
      
      ```

   2. 服务提供者集群

      ```yaml
      #服务提供者的端口号
      server:
       port: 8000
      # 服务别名  serverid
      spring:
       application:
        name: app-levia-member
      eureka:
       client:
        service-url:
      #当前会员服务注册到eureka服务（指定注册中心地址,如果由多个注册中心则需要指定多个）
         defaultZone: http://localhost:8100/eureka,http://localhost:8200/eureka
      #当前服务是否需要注册到注册中心
        register-with-eureka: true
      #需要去注册中心检索信息
        fetch-registry: true
      
      ```

   3. 注意事项：

      1. 注册中心集群启动之后，会发现只有一台注册中心有全部的服务注册信息（这台注册中心被称为主机），其他注册中心（从机）上**只有主机的注册信息**
      2. 服务在注册中心集群中注册之后，只有一台**主机**会获得所有服务的注册信息，另外的**从机**获得的服务注册信息并不完全，只有当主机宕机之后，服务的注册信息才会从主机转移到从机（这个时间默认是30秒）。
      3. 如果主机在宕机之后重新启动（主机1），在主机1宕机期间作为主机的主机2和主机1上都会有完整的注册信息 

9. Eureka自我保护机制

   1. 为了防止在EurekaClient可以正常运行，但是与EurekaServer网络不通的情况下，EurekaServer不会将EurekaClient剔除。具体表现是，在某个服务提供者宕机之后，消费方在一段时间内依然可以在注册中心获取到它的注册信息并访问（访问会报连接失败的错误）。

   2. 自我保护机制原理：

      1. 默认情况下EurekaClient定时（默认5秒）向EurekaServer端发送心跳包，如果EurekaServer在一定时间内没有收到EurekaClient发送的心跳包，在丢失了大量心跳的情况下，EurekaServer会开启自我保护机制，不会立即剔除丢失心跳的服务，但是在过了保护机制的时间之后（默认90秒），会直接将丢失心跳的服务从注册列表中剔除。

   3. 自我保护机制的作用：

      1. 由于服务与服务，服务与注册中心之间都是通过网络通信，有可能出现网络状况不好的情况，这时候服务本身并没有故障，在网络恢复之后依旧可以正常调用，有了这个自我保护机制，就可以防止注册中心将这种能正常运行的服务剔除。

   4. 什么环境下开启自我保护机制

      1. 本地环境建议关闭自我保护机制，保证不可用的服务被及时剔除掉，这样在重新启动服务之后能马上将服务注册到注册中心，而不会和注册中心中自我保护机制下的注册信息发生冲突

         * 开启自我保护机制的配置 在**注册中心**配置文件中--> 

           ```yaml
           server: 
           	#表示关闭自我保护 
            anable-self-preservation: false 
            	#表示间隔两秒剔除一次
            enviction-interval-timer-in-ms: 2000
           ```

         * 在**服务配置文件**中--> 

           ```yaml
           #心跳检测与续约时间
           #测试时将值设置小些,保证服务关闭后注册中心能及时踢出
           instance:
           	#Eureka客户端向服务端发送心跳的时间间隔,单位为秒
            lease-renewal-interval-in-seconds: 1 #客户端告诉服务端自己会按照这个规则发送心跳
            	#Eureka服务端在收到最后一次心跳之后等待的时间上限,单位为秒,超过则剔除
            lease-expiration-duration-in-seconds: 2 # 客户端告诉服务端按照这个规则剔除丢失心跳的服务  
           ```

           

      2. 生产环境建议开启自我保护机制

10. 使用ZooKeeper\ consul作为注册中心搭建微服务

  1. 更换注册中心只需要变动MAVEN配置文件和项目的application.yml配置文件,服务的调用方式依旧没有变化(因为底层都是httpclient)

11. RestTemplate这种调用方式了解就好，做开发时常用的是Feign的调用方式

   1. 添加feign依赖

   2. Feign的书写方式是以SpringMvc接口形式书写

   3. @FeignClient调用服务接口， 属性name就是需要调用的那个服务的别名

   4. 代码

     

```xml
		<!--依赖-->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-openfeign</artifactId>
		</dependency>	
```

```java
package com.levia.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

//name=服务别名
@FeignClient(name="app-levia-member")
public interface ConsumerApiFeign {
	
    //接口
	@RequestMapping(value="/getMember")
	public String getMember();
}

//在启动类上加上@EnableEurekaClient开启Feign权限
```

12. 微服务包结构
    1. 对feign客户端来说，调用只需要接口即可，所以可以将每个服务的feign接口集中管理，如果其他服务需要用到这些接口可以将这个接口包作为依赖导入到服务中就可以直接进行调用。这种集中管理的方式避免了一个服务被多个其他服务调用时重复写feign接口的问题。
    2. 包结构举例如下：

```java
//maven知识补充：有实现类的工程创建为jar工程，可能被其他工程所继承的创建为Pom工程，pom里面只写共同的依赖信息。war是需要打成web工程进行访问的。
//在项目的结构中也是能看出显著区别的，pom工程里面只有pom文件没有代码，另外的都是依赖这个pom工程的子工程的代码，jar里面是各种类的代码（接口也是一种特殊类），可以作为其他工程的依赖。war是最终需要打成war放到容器中运行的

//创建Parent聚合工程的时候，因为这个工程存放的是所有工程共同的依赖，这个工程建为maven Project,因为该工程下有多个子工程，packaging创建为Pom而不是jar
--SpringCloud2.0.Levia.Parent 
// 其他的工程作为Parent的子工程，不能再新建为Maven Project，而是应该右键Parent工程，在其下创建maven Module，又因为该工程下还有两个子工程，所以packaging类型还是不能选择为jar，而应该是Pom类型
--SpringCloud2.0.Levia.Service.Api
//服务的接口包作为SpringCloud2.0.Levia.Service.Api的子工程，还是右键SpringCloud2.0.Levia.Service.Api工程，创建Maven Module，由于这个工程下面没有子工程，所以packaging可以选择为jar
---SpringCloud2.0.Levia.Service.Api.provider001
---SpringCloud2.0.Levia.Service.Api.provider002
--SpringCloud2.0.Levia.Service.Impl.Provider001
--SpringCloud2.0.Levia.Service.Impl.Provider002

//涉及到实体类的存放，可以单独建立工程进行存放，也可以放在接口项目里面（因为接口会作为其他工程的依赖，而接口的实现这种项目因为解耦的关系，一般不会作为依赖）
```

