#SpringBoot#
##日志配置##

springBoot支持多种日志框架：Java Util Logging, Log4J2, Logback,springBoot默认使用的是Logback。配置日志框架的方式也有多种，默认配置文件配置和引用外部配置文件配置。


1 默认配置文件配置（不建议使用，不够灵活，对log4J等不够友好）

	# 日志文件名，比如：roncoo.log，或者是 /var/log/roncoo.log
	logging.file=roncoo.log 
	# 日志级别配置，比如： logging.level.org.springframework=DEBUG
	logging.level.*=info
	logging.level.org.springframework=DEBUG

2 引入外部配置文件

	2.1 logback配置方式：
	spring boot默认会加载classpath:logback-spring.xml或者classpath:logback-spring.groovy

	如果不想使用springBoot默认的名字，可以使用自定义配置文件名字，配置方式为：
	logging.config=classpath:logback-roncoo.xml
	注意：不要使用logback这个来命名，否则spring boot将不能完全实例化
	
	使用基于spring boot的配置
	<?xml version="1.0" encoding="UTF-8"?>
	<configuration>
	<include resource="org/springframework/boot/logging/logback/base.xml"/>
	<logger name="org.springframework.web" level="DEBUG"/>
	</configuration>
	
	#详细的日志配置见与笔记同名文件夹中的logback-spring.xml
	#logback的日志配置有一个特点，日志配置文件中<springProfile name="test"> name可以和properties中的多环境配置配合使用，可以在切换环境的同时更改日志环境。这种方式只有logback有效。

	2.2 log4j2配置

		2.2.1去除logback的依赖包，添加log4j2的依赖包
		<！--这行配置的添加位置是start-web依赖配置中-->
		<exclusions>
			<exclusion>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter-logging</artifactId>
			</exclusion>
		</exclusions>
		
		<!-- 使用log4j2 -->
				<dependency>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-starter-log4j2</artifactId>
				</dependency>
		2.2.2 在classpath添加log4j2.xml或者log4j2-spring.xml（spring boot 默认加载）
		
		#log4j2的详细配置文件见笔记同名文件夹中的log4j2-dev.xml
		#log4j2日志的多环境配置无法采用logback那种同一个文件夹中配置的方式，只能在classpath下配置多个独立的日志文件，然后在相应的环境properties中使用  logging.config 属性进行配置。
	

	推荐使用logback作为日志框架。