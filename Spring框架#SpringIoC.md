#Spring框架#
#SpringIOC#
	1.SpringIOC阐述
		* SpringIOC是一种通过描述(在Java中可以是XML或者注解)并通过第三方去产生或者获取特定对象的方式。而在Spring中实现控制反转（Inversion of Control）的是IoC容器，其实现方法是依赖注入（Dependency Injection, DI）。
		
		* 部分开发者对Ioc和DI的概念有些混淆，认为二者是对等的，其实不是。Ioc其实有两种方式，一种是依赖注入（DI），另一种是依赖查找（Dependency Lookup, DL）。前者是当前软件实体被动接受其依赖的其他组件被IoC容器注入，而后者是当前的软件实体主动去某个服务注册地查找器依赖的那些服务。

	2.SpringIOC容器
		* SpringIOC容器的设计主要是基于BeanFactory和ApplicationContext两个接口，其中ApplicationContext是BeanFactory的子接口之一，BeanFactory是Spring IOC定义的最底层接口，ApplicationContext是其高级接口之一，并且对BeanFactory功能做了许多有用的扩展。

		* 我们通常说的Spring IoC实际上是指Spring框架提供的Ioc容器实现。
	
		2.1 Spring的Ioc容器中发生的事情其实也很简单，总结下来分为两个阶段：
		2.1.1 收集和注册
			* 第一个阶段可以认为是构建和收集Bean定义的阶段，在这个阶段中我们可以通过XML或者Java代码的方式定义一些Bean，然后通过手动组装或者让容器基于某些机制自动扫描的形式，将这些bean定义收集到Ioc容器中。

			*Bean的定义可以分为三步：
				1 Resource定位，这步时SpringIoc容器根据开发者的配置，进行资源的定位。在开发中常用的方式是XML或者注解的方式。
				2 BeanDefinition的载入，这个时候只是将BeanDefiniton的信息保存到Spring IoC容器中，此时不会创建Bean的实例
				3 BeanDefinition的注册，这个过程就是将BeanDefiniton的信息发布到Spring IoC容器中，，此时仍旧不会创建Bean的实例
			这三步之后Bean就在Spring Ioc容器中被定义了，但是没有初始化，更没有完成依赖注入，也就是还没有注入器配置的资源给Bean，那么它还不能使用。

		2.1.2 分析和组装
			*当阶段一的工作完成后，可以认为IoC容器中充斥着一个个独立的Bean，他们之间没有任何关系。但实际上，他们之间是有依赖关系的，所以，Ioc容器在第二阶段要做的就是分析Ioc容器中的Bean，然后根据他们之间的关系先后组装他们。如果Ioc容器发现某个Bean依赖另一个Bean，它就会将这另一个Bean注入给依赖它的那个Bean，直到所有Bean的依赖都注入完成，这个Ioc容器的工作即算完成。

	3.依赖注入的三种方式 