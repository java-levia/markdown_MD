#SpringBoot#
#Spring注解#
	1. @Configuration :该注解用于定义配置类，可替换Xml配置文件，被注解的类内部包含有一个或多个被@Bean注解的方法，这些方法将会被AnnotationConfigApplicationContext进行扫描，并用于构建bean定义，初始化Spring容器。
	2. 