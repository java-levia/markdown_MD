# 代码规范#SSM

1. 关于静态类

   1. 多数用于表示各种状态的属性被作为常量定义在该静态类中，一方面可以集中进行管理，另一方面在调用时可以很方便地得知该属性的业务意义。

2. 对一些固定了选择范围的情况下考虑使用枚举类

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

5. 页面中需要动态显示的文字（包括提示信息和页面导航等的为文字）都整理成properties文件（按实际情况决定需要配置的语言），调用时使用PropertiesUtil  中的方法调用（PropertiesUtil  中有相应的方法根据环境切换对应的配置文件获取提示信息）

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

9. 数据库中的大部分数据的删除其实都不是真删除，只是将基础状态进行更改（基础状态在StaticUtil中进行定义）

10. 对修改/添加这两个操作，如果能合并的进行合并（一般添加操作没有id，修改操作有id，以此区别两种操作）。

11. 程序中利用切面编程，使用自定义注解的方式记录平台的操作日志（spring配置中扫描service层）

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
   
   //定义一个切面类写切面逻辑
   	 
   ```

   

12. 对一些初始化资源的加载，可以采用监听器的方式，在容器初始化的时候就将这些资源加载进去。在监听器的配置中，可以对容器进行判断，限定只有在父容器初始化的时候进行加载，避免资源的多次加载引发一些问题（如果需要在监听器中刷新Token，这种操作如果频繁进行就会导致异常，因为很多平台的Token是限定次数的。）

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

13. 与前端或者app交互的安全控制
    1. 服务器向app端推送消息需要有手机设备的串号（这个应该是应用在初次运行的时候就可以获得的）
    2. app端向服务器发送消息