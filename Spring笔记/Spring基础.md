#### Spring基础

1. 切面编程
   1. 基本概念

      * 切面（Aspect）：切面是通知和切入点的结合。通知说明了干什么和什么时候干，切入点说明了在哪干。
      * 连接点（JoinPoint）：就是Spring允许织入通知的地方，基本就是每个方法的前后，或者抛出异常时和正常返回时。其他如AspectJ（一个AOP框架）还可以让你构造器或者属性注入时都可以执行织入。
      * 切入点（PointCut）：切入点是在连接点的基础上定义的，一个类里有多个方法，这些方法都可以作为连接点，但实际上需要用到通知的方法并没有这么多。在这些连接点中，真正需要在附近执行织入通知的方法就是切入点。

   2. 实际使用场景

      1. 在数据库事务控制中，定义切点

         ```xml
         	
         	<aop:config expose-proxy="true">
                 <!-- apo切点   切点表达式  
         		第一个 * 表示任意返回值  
         		.. 表示延续service路径并包括其所有子包，
         		* 匹配任意单词，表示任意类
          		其后的 . 就是调用方法的那个点  
         		* 表示任意方法，其后括号中的俩个点表示任意参数-->
         		<aop:pointcut id="servicePointcut"
         			expression="(execution(* com.curio.app.service..*.*(..)) or execution(* com.curio.service..*.*(..)))" />
                 <!--这是其中的一种通知注入方式，另一种是 < aop:aspect> -->
         		<aop:advisor advice-ref="txAdvice" pointcut-ref="servicePointcut" />
                 <!-- 两种注入方式的底层原理是一样的，只是在应用的时候有不同的便利性，
          < aop:aspect>定义切面时，只需要定义一般的bean就行，而定义< aop:advisor>中引用的通知时，通知必须实现Advice接口。 < aop:advisor> 是通过通知接口的实现确定通知类型，< aop:aspect>是通过配置将普通Bean中的方法定义为通知 -->
         	</aop:config>
         
         ```

      2. 通知定义

         ```xml
         <!--< aop:advisor> 定义通知（事务控制中的实例）-->
         <tx:advice id="txAdvice" transaction-manager="transactionManager">
         		<tx:attributes>
         			<!-- 指定哪些方法需要加入事务，可以使用通配符来只加入需要的方法 -->
         			<tx:method name="save*" propagation="REQUIRED"/>
         			<tx:method name="add*" propagation="REQUIRED"/>
         			<tx:method name="create*" propagation="REQUIRED"/>
         			<tx:method name="change*" propagation="REQUIRED"/>
         			<tx:method name="update*" propagation="REQUIRED"/>
         			<tx:method name="remove*" propagation="REQUIRED"/>
         			<tx:method name="delete*" propagation="REQUIRED"/>
         			<tx:method name="insert*" propagation="REQUIRED"/>
         			<tx:method name="handle*" propagation="REQUIRED"/>
         		</tx:attributes>
         </tx:advice>
         <!--< aop:advisor> 定义通知（非事务控制中的实例）-->
         <aop:config>
             <aop:pointcut expression="execution(* *.sleep(..))" id="sleepPointcut"/>
             <aop:advisor advice-ref="sleepHelper" pointcut-ref="sleepPointcut"/>
         </aop:config>
         
         <!--< aop:aspect> 定义通知-->
         <aop:config>
             <aop:pointcut expression="execution(* *.sleep(..))" id="sleepPointcut"/>
             <aop:aspect ref="sleepHelperAspect">
                 <!--前置通知-->
                 <aop:before method="beforeSleep" pointcut-ref="sleepPointcut"/>
                 <!--后置通知-->
                 <aop:after method="afterSleep" pointcut-ref="sleepPointcut"/>
             </aop:aspect>
         </aop:config>
         
         
         ```

         

      

