SpringBoot默认的错误处理机制

1. SpringBoot中针对浏览器端和app端的默认错误处理机制时不一样的，区别这两个端的方式就就是请求头中是否携带“text/html”这个值，如果携带，说明是浏览器发过来的请求，否则认为是app端的请求（可以通过查看ErrorMvcAutoConfiguration这个类获得一些信息）
   1. 针对浏览器的错误处理机制

      针对浏览器的错误处理，默认会返回一段html代码

      针对浏览器的自定义错误处理页面：

       	1. 不使用模板引擎： 在Resources文件夹下创建public文件夹，在文件夹下创建一个404.html用于404错误页面，500同理
       	2. 使用模板引擎： 在templates/error/下定义

   2. 针对app的错误处理机制

      针对app的错误处理，默认会返回一个Json字符串

      

      1. 如果需要在app中返回自定义的异常信息，则需要创建一个运行时异常继承RuntimeException，同时将要传达的信息作为这个异常类的属性。

      2. 同时需要再创建一个添加了注解@ControllerAdvice的类，代码如下：

         ```java
         //添加这个注解表示这个类是用来增强其他Controller
         @ControllerAdvice
         public class ControllerExceptionHandler{
             
             //这个注解表示这个方法用来处理UserNotFoundException这个异常的信息
             @ExceptionHandler(UserNotFoundException.class)
             @ResponseBody
             //这个注解可以返回状态码
             @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
             public Map<Integer, Object> handlerUserNotException(UserNotFoundException ex){
                Map<Integer, Object>  result = new HashMap<>();
                 result.put("id", ex.getId());
                 result.put("msg", ex.getMessage())
                return result;     
             }
         }
         ```

         

      