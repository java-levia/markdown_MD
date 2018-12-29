# 代码规范#SSM

1. 关于静态类

   1. 多数用于表示各种状态的属性被作为常量定义在该静态类中，一方面可以集中进行管理，另一方面在调用时可以很方便地得知该属性的业务意义。
   2. 对程序中涉及到的所有url路径进行统一管理，也可以使用静态类。

2. 同样是用数字表示状态，枚举和静态的取舍规则：

     1. 对于需要在页面上显示给用户看的状态信息，使用枚举类可以更方便地在页面显示的时候进行对应
     2. 对于一些系统状态（仅仅需要存储在数据库中，作为一个表示数据状态或者操作状态的数据，这种数据一般用于对操作进行定性，比如com.jyk.common下的StaticUtil类中的静态常量），可以使用静态常量定义在类中。

3. 注意错误处理，尽量考虑周全，对程序中可能出现的异常进行处理（这里的意义包括不符合业务逻辑的情况，举例如下）

   ```java
   public String handleQtyReturn(Integer pdetailid,Integer qty,Integer type,String ext){
   
   	//
   
   	int i=procDetailsMapper.updateQtyById( new UpdateQtyVo(pdetailid, type,qty));
   		if(i<1) {
   			return PropertiesUtil.getVal("shop_qty_error", ext);
   		}
   		return "";
   	}
   ```

   在以上代码中，释放库存意味着操作数据库纪录条数>=1，所以如果返回值<1则说明在操作数据库的过程中出错了。

4. 代码中的sign都定义在StaticUtil中进行统一管理，接口的访问路径定义在MappingUtils中进行统一管理，使用时做静态调用。

5. 页面中可能需要国际化显示的文字（包括提示信息和页面导航等的文字）都整理成properties文件（按实际情况决定需要配置的语言），调用时使用PropertiesUtil  中的方法调用（PropertiesUtil  中有相应的方法根据环境切换对应的配置文件获取提示信息）

6. 对集合做遍历操作之前，先进行非零判断

   ```java
   if (list != null && list.size() > 0) {
   			}
   ```

7. 对输入框中传过来的值进行后端验证，其中的验证包括但不限于：

   1. 非空验证（为空或者空格使用StringUtils.isBlank验证）
   2. 有格式限定的值使用正则表达式验证（RpxUtils类中定义正则）
   * 如果数据非法，使用rest风格向前端返回提示信息（从目前的代码来看，返回的这些提示都使用了GetRest工具类，且提示信息都采用的是硬编码的方式）

   * FunctionUtils类中定义了ReturnInfo方法，且有两个重载方法，可以针对不同的情况选择最高效的返回方式

     ```java
     public static RestResponse ReturnInfo(int i, String successMsg, String errorMsg) {
     		if (i > 0) {
     			return GetRest.getSuccess(successMsg);
     		} else {
     			return GetRest.getFail(errorMsg);
     		}
     	}
     	public static RestResponse ReturnInfo(boolean flag, String successMsg, String errorMsg) {
     		if (flag) {
     			return GetRest.getSuccess(successMsg);
     		} else {
     			return GetRest.getFail(errorMsg);
     		}
     	}
     ```

8. 在编码中适当使用flag可以优化程序的可读性。（在一些流程性的代码中效果显著，使用flag记录各流程方法的返回状态（一般为boolean类型））

9. 为查询语句的结果集新建一个结果集类，对结果集进行封装，可以对方法进行重载以适应不同类型的返回值。

10. 数据库中的大部分数据的删除其实都不是真删除，只是将基础状态进行更改（基础状态在StaticUtil中进行定义）

11. 对修改/添加这两个操作，如果能合并的进行合并（一般添加操作没有id，修改操作有id，以此区别两种操作）。

12. 程序中利用切面编程，使用自定义注解的方式记录平台的操作日志（spring配置中扫描service层）

   ```java
   @Target({ElementType.PARAMETER, ElementType.METHOD})  //用于描述注解的使用范围
   @Retention(RetentionPolicy.RUNTIME)		//用于描述注解的生命周期
   @Documented	//在默认的情况下javadoc命令不会将我们的annotation生成到doc中去的，所以使用该标记就是告诉jdk让它也将annotation生成到doc中去   
   public @interface SystemLog {
       String module()  default "";
       String methods()  default "";
   }
   
   //实际使用时
   	@SystemLog(methods = "产品规格明细 添加/修改 ", module = "产品库管理")	 
   ```

   ```java
   
   //日志切面类
   @Aspect
   public class LogAopAction {
       //获取开始时间
       private long BEGIN_TIME ;
   
       //获取结束时间
       private long END_TIME;
   
       //定义本次log实体
       private SysLogs log = new SysLogs();
       
       @Pointcut("execution(* com.jyk.platform.service..*.*(..))")
        private void controllerAspect(){}
       @Autowired
       private SysLogsMapper sysLogsMapper;
   
       /**
        * 方法开始执行
        */
       @Before("controllerAspect()")
       public void doBefore(){
           BEGIN_TIME = new Date().getTime();
       }
   
       /**
        * 方法结束执行
        */
       @After("controllerAspect()")
       public void after(){
           END_TIME = new Date().getTime();
       }
   
       /**
        * 方法结束执行后的操作
        */
       @AfterReturning("controllerAspect()")
       public void doAfter(){
       		if(log!=null) {
       			if(log.getState()!=null) {
       				 if(log.getState()==1||log.getState()==-1){
       					 if(RequestContextHolder.getRequestAttributes()!=null) {
       						 HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
       	     	             //获取当前登陆用户信息
       	     	             SysUser loginUser = PlatSession.getSysuser(request);
       	     	             if(loginUser==null){
       	     	                 log.setLoginaccount("—— ——");
       	     	             }else{
       	     	                 log.setLoginaccount(loginUser.getLogin()+"|"+loginUser.getName());
       	     	             }
       	     	            log.setActiontime(END_TIME-BEGIN_TIME);
       	     	            log.setGmtcreate(new Date(BEGIN_TIME));
       	     	            sysLogsMapper.insertSelective(log);
       		     	        //删除除了最新的1000条记录
       		     	        sysLogsMapper.deleteOld();
       					 }
        	        }
       			}
       		}
       }
   
       /**
        * 方法有异常时的操作
        */
       @AfterThrowing("controllerAspect()")
       public void doAfterThrow(){
       }
   
   
       /**
        * 方法执行
        * @param pjp
        * @return
        * @throws Throwable
        */
       @Around("controllerAspect()")
       public Object around(ProceedingJoinPoint pjp) throws Throwable{
           //日志实体对象
       		ServletRequestAttributes attributes=(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
           Object object = null;
           if(attributes!=null) {
            	HttpServletRequest request =attributes.getRequest();
           	 // 拦截的实体类，就是当前正在执行的controller
               Object target = pjp.getTarget();
               // 拦截的方法名称。当前正在执行的方法
               String methodName = pjp.getSignature().getName();
               // 拦截的方法参数
               @SuppressWarnings("unused")
   			Object[] args = pjp.getArgs();
               // 拦截的放参数类型
               Signature sig = pjp.getSignature();
               MethodSignature msig = null;
               if (!(sig instanceof MethodSignature)) {
                   throw new IllegalArgumentException("该注解只能用于方法");
               }
               msig = (MethodSignature) sig;
               @SuppressWarnings("rawtypes")
   			Class[] parameterTypes = msig.getMethod().getParameterTypes();
   
   
               Method method = null;
               try {
                   method = target.getClass().getMethod(methodName, parameterTypes);
               } catch (NoSuchMethodException e1) {
                   // TODO Auto-generated catch block
                   e1.printStackTrace();
               } catch (SecurityException e1) {
                   // TODO Auto-generated catch block
                   e1.printStackTrace();
               }
   
               if (null != method) {
                   // 判断是否包含自定义的注解，说明一下这里的SystemLog就是我自己自定义的注解
                   if (method.isAnnotationPresent(SystemLog.class)) {
                       SystemLog systemlog = method.getAnnotation(SystemLog.class);
                       log.setModule(systemlog.module());
                       log.setMethod(systemlog.methods());
                       log.setLoginip(getIp(request));
                       log.setActionurl(request.getRequestURI());
   
                       try {
                           object = pjp.proceed();
                           log.setDescription("执行成功");
                           log.setState(1);
                       } catch (Throwable e) {
                           // TODO Auto-generated catch block
                           log.setDescription("执行失败");
                           log.setState(-1);
                       }
                   } else {//没有包含注解
                       object = pjp.proceed();
                       log.setDescription("此操作不包含注解");
                       log.setState(0);
                   }
               } else { //不需要拦截直接执行
                   object = pjp.proceed();
                   log.setDescription("不需要拦截直接执行");
                   log.setState(0);
               }
           }else {
           	 	object = pjp.proceed();
           }
           return object;
       }
   
       /**
        * 获取ip地址
        * @param request
        * @return
        */
       private String getIp(HttpServletRequest request){
           if (request.getHeader("x-forwarded-for") == null) {
               return request.getRemoteAddr();
           }
           return request.getHeader("x-forwarded-for");
       }
   }
   
   ```

   ```java
   	
   
   <!-- 基本事务定义,使用transactionManager作事务管理,默认find*方法的事务为readonly -->
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
   //配置文件中对自定义日志相关的配置	
   	<!-- apo切点 -->
   	<aop:config expose-proxy="true">
   		<aop:pointcut id="servicePointcut"
   			expression="(execution(* com.jyk.platform.service..*.*(..)) or execution(* com.jyk.service..*.*(..)))" />
   		<aop:advisor advice-ref="txAdvice" pointcut-ref="servicePointcut" />
   	</aop:config>
   	
   	<!--  配置使Spring采用CGLIB代理 -->
       <aop:aspectj-autoproxy proxy-target-class="true" expose-proxy="true"/>
       <bean id="logAopAction" class="com.jyk.platform.service.LogAopAction"></bean>
   ```

13. 

14. 对一些初始化资源的加载，可以采用监听器的方式，在容器初始化的时候就将这些资源加载进去。在监听器的配置中，可以对容器进行判断，限定只有在父容器初始化的时候进行加载，避免资源的多次加载引发一些问题（如果需要在监听器中刷新Token，这种操作如果频繁进行就会导致异常，因为很多平台的Token是限定次数的。）

```java
@Component
public class Configue implements ApplicationListener<ContextRefreshedEvent> {	
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent() == null){
		}
	}
}

```

13. 使用拦截器做登陆验证的时候，是分为两种请求类型处理的
    1. 同步请求，如果不是登陆状态，直接跳转到登陆页面
    2. 异步请求，如果不是登陆状态，给前端返回一个响应头（“sessionstatus”， “timeout”），前端可以通过获取sessionstatus得知状态。
       1. 判断是否为异步请求：通过request.getHeader("X-Request-With");获取请求头中的信息，如果返回值为XMLHttpRequest说明请求为ajax请求。
14. 网站首页
15. 在查询商品的详情时，由于商品有多个规格，所以使用商品id  pid查出来的可能会有多个产品，但是在页面显示时优先显示价格最低的那个。如果查询条件中pid为空，可以先使用pcode（产品编号）查找到对应的pid，然后再进行查询（最终还是得使用pid查询的原因同上）。
16. 对用户的校验有两个必要步骤
    1. 用户是否存在
    2. 用户是否冻结
17. 需要用到商品综合信息的时候，可以先从多个表中将商品的各方面详细信息查找出来放到一个vo中，然后从vo中挑选出需要的信息。
18. 业务中，一般使用商品的第一张图作为购物车、收藏列表等的展示图。
19. 在执行一些关键信息的更改或者录入时，验证阶段和更改阶段是不同的两个接口（虽然在用户看来只点击了一次按钮，但在请求的时候实际上是发送了两个请求），只有验证阶段返回true，才会继续下面的请求。
20. 消息推送时不会推送明文消息，都是有固定模板的，将模板和动态数据拼接起来才能获得完整的消息。


