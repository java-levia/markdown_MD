### SpringCloud返回值为XML问题

我也是引入了jackson-dataformat-xml这个依赖，它是提供了jackson将实体类转化为xml相关的作用。而本身jackson是可以将实体类转化为json的，所以这样Jackson是可以将实体类转化为两种类型的数据，而具体要转化为哪一种数据，是要看http请求里面的accept头信息的，我的浏览器chrome的accept是  Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8 ，然后服务器会根据accept来决定是返回xml还是json，由于浏览器accept只有最后的*/*是匹配 application/json的，而application/xml在*/*前面，优先级比json高，所以用浏览器直接调用是会优先返回xml格式的。 

 解决方案有两种： 

	1. 自己调用接口的时候修改accept信息，改为application/json  (postman之类的工具)  

2.添加依赖        

```xml
<dependency>
    <groupId>com.fasterxml.jackson.jaxrs</groupId>
    <artifactId>jackson-jaxrs-xml-provider</artifactId>         
</dependency> 
```

 然后就可以使用后缀来调用相关的接口获取对应格式的数据了 比如我有个url  localhost/get/user 返回一个用户数据 添加了上面的依赖后，如果想获取xml格式的，就使用localhost/get/user.xml来调用接口 如果想获取json格式的，就使用localhost/get/user.json来调用接口 它的原理是服务器根据后缀主动修改了accept信息 

3. 设置@requestMapping()的属性

   ```java
   @RestController
   public class OrderController {
       @Autowired
       private MemberApi memberApi;
   	//设置属性produces为 {"application/json;charset=UTF-8"}) 返回值为json
       //设置属性
       @RequestMapping(value="/getMember/{name}/{age}",produces = {"application/json;charset=UTF-8"})
       public Member getMember(@PathVariable(name = "name") String name,
                               @PathVariable(name="age") Integer age){
           return memberApi.getMember(name, age);
       }
   }
   ```

4. 在请求的Mapping上加上produces = { “application/json;charset=UTF-8” }，例如：

   

   ```java
   @GetMapping(value = "/user-instance", produces = { "application/json;charset=UTF-8" })
   以下是json和xml 
   @GetMapping(value = "/user-instance", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
   @GetMapping(value = "/user-instance", produces = MediaType.APPLICATION_XML_VALUE)
   
   ```

   支持JSON和XML两种格式的返回

   有时项目需求两种返回格式，这时候我们只要加上jackson xml的依赖就可以了

   

   ```xml
   <dependency>
     <groupId>com.fasterxml.jackson.jaxrs</groupId>
     <artifactId>jackson-jaxrs-xml-provider</artifactId>
   </dependency>
   
   ```

   Controller
   在Mapping上不用标明格式

   ```java
   @GetMapping(value = "/user/{id}")
   // @GetMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
     public User findById(@PathVariable Long id){
         return this.restTemplate.getForObject(userServiceUrl+id,User.class);
       }
   
   ```
   

   在访问时通过后缀来规定返回格式：

   http://localhost:8010/user/1.json
   --------------------- 
   