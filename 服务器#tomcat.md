#服务器#
##Tomcat&Jetty##

1. Tomcat
	
	1. Tomcat依赖<CATALINA_HOME>/conf/server.xml这个配置文件启动server（一个tomcat实例，核心就是启动容器Catalina）。
	2. Tomcat部署Webapp时，依赖context.xml和web.xml<CATALINA_HOME>/conf/目录下的context.xml和web.xml在部署任何webapp时都会启动，他们定义一些默认行为，而具体每个webapp的  META-INF/context.xml  和  WEB-INF/web.xml  则定义了每个webapp特定的行为)两个配置文件部署应用。
		
	3. <CATALINA_HOME>/conf：存放不同的配置文件（如：server.xml和web.xml)
		1. server.xml文件:该文件用于配置和server相关的信息，比如tomcat启动的端口号、配置host主机、配置Context
		2. web.xml文件：部署描述文件，这个web.xml中描述了一些默认的servlet，部署每个webapp时，都会调用这个文件，**配置该web应用的默认servlet**。（其中比较有印象的就是定义了一个session过期时间为30分钟，还有其他很多servlet）
		3. tomcat-users.xml文件：配置tomcat的用户密码与权限。
		4. context.xml：定义web应用的默认行为。（其中定义了默认加载项目的web.xml和tomcat自带的web.xml

	
	4. Tomcat Service的组成部分
		1. Server
			- 一台服务器整个就是一个Catalina servlet容器
		
		2. service
			- Service是这样一个集合，它由一个或多个connector组成，以及一个Engine，负责处理所有Connector所获得的客户请求。

		3. Connector
			- 一个Connector将在某个指定端口上侦听客户端请求，并将获得的请求交给Engine处理，从Engine处获得回应并返回给
			- TOMCAT有两个典型的Connector，一个直接侦听来自browser的http请求，一个侦听来自其它WebServer的请求
			- 接到请求后，创建request和response的也是它
			- Coyote Http/1.1 Connector 在端口8080处侦听来自客户browser的http请求 
			- Coyote JK2 Connector 在端口8009处侦听来自其它WebServer(Apache)的servlet/jsp代理请求

		4. Engine
			- Engine下可以配置多个虚拟主机Virtual Host，每个虚拟主机都有一个域名，
			- 当Engine获得一个请求时，它把该请求匹配到某个Host上，然后把该请求交给该Host来处理
			- Engine有一个默认虚拟主机，当请求无法匹配到任何一个Host上的时候，将交给该Host处理

		5. Host
			- 代表一个Virtual Host，虚拟主机，每个虚拟主机和某个网络域名Domain Name相匹配
			- 每个虚拟主机下都可以部署(deploy)一个或者多个Web App，每个Web App对应于一个Context，有一个Context path
			- 当Host获得一个请求时，将把该请求匹配到某个Context上，然后把该请求交给该Context来处理
			- 匹配的方法是“最长匹配”，所以一个path==”"的Context将成为该Host的默认Context
			- 所有无法和其它Context的路径名匹配的请求都将最终和该默认Context匹配

		6. Context
			- 一个Context对应于一个Web Application，一个Web Application由一个或者多个Servlet组成
			- Context在创建的时候将根据配置文件$CATALINA_HOME/conf/web.xml和$WEBAPP_HOME/WEB-INF/web.xml载入Servlet类
			- 当Context获得请求时，将在自己的映射表(mapping table)中寻找相匹配的Servlet类
			- 如果找到，则执行该类，获得请求的回应，并返回

	5. Tomcat的启动过程
		1. Tomcat先根据/conf/server.xml下的配置启动Server，再加载Service，对于与Engine相匹配的Host，每个Host下面都有一个或多个Context。
		2. 注意：Context 既可配置在server.xml 下，也可配置成一单独的文件，放在conf\Catalina\localhost 下，简称应用配置文件。
		3. Web Application 对应一个Context，每个Web Application 由一个或多个Servlet 组成。当一个Web Application 被初始化的时候，它将用自己的ClassLoader 对象载入部署配置文件web.xml 中定义的每个Servlet 类：它首先载入在$CATALINA_HOME/conf/web.xml中部署的Servlet 类，然后载入在自己的Web Application 根目录下WEB-INF/web.xml 中部署的Servlet 类。
		4. web.xml 文件有两部分：Servlet 类定义和Servlet 映射定义。
			- 每个被载入的Servlet 类都有一个名字，且被填入该Context 的映射表(mapping table)中，和某种URL 路径对应。当该Context 获得请求时，将查询mapping table，找到被请求的Servlet，并执行以获得请求响应。

	6.  在tomcat 5.5之前
		- Context体现在/conf/server.xml中的Host里的<Context>元素，它由Context接口定义。每个<Context元素代表了运行在虚拟主机上的单个Web应用
		在tomcat 5.5之后
		- 不推荐在server.xml中进行配置，而是在/conf/context.xml中进行独立的配置。因为server.xml是不可动态重加载的资源，服务器一旦启动了以后，要修改这个文件，就得重启服务器才能重新加载。而context.xml文件则不然，tomcat服务器会定时去扫描这个文件。一旦发现文件被修改（时间戳改变了），就会自动重新加载这个文件，而不需要重启服务器。
