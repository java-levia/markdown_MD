#SpringBoot#
##SpringBoot初探##
1.MAVEN构建spring项目
		* 访问http://start.spring.io/
		* 选择构建工具Maven project，SpringBoot版本以及一些基本的工程信息。点击“Switch to the full version” 
		* 选择java版本
		* 点击Generate Project下载项目压缩包
		* 解压后，使用eclipse，Import-->Existing Maven Projects->next-->选择解压后的文件夹-->Finish


2.Spring中的一些常用注解
		* @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss a", locale="zh", timezone="GTM+8") 作用是将时间格式化
		* @JsonIgnore  作用是json序列化时将java bean中的一些属性忽略掉,序列化和反序列化都受影响
		* @JsonInclude（Include.NON_NULL）可以设置属性值是否显示。其中Include是一个枚举类，其中包含ALWAYS NON_NULL等多个属性，可用于设置被注解属性的显示状态

3.Spring环境的热部署
		* 使用devtoos进行热部署
		* 在springBoot项目的pom文件中加入如下配置即可支持应用的快速重启

```xml
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-devtools</artifactId>
	  </dependency>
```

4.springBoot整合
	