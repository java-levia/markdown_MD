# 代码规范#SpringBoot

1. 配置嵌入式servlet容器错误处理（SpringBoot 2.0+）

   1. SpringBoot默认使用嵌入式的Tomcat，默认没有页面来处理404等常见错误。因此，为了用户体验，404等常见的错误需要我们用自定义页面来处理.

   2. 代码

      `@Configuration
      public class ErrorPageConfig {
      	
      //WebServerFactoryCustomizer在SpringBoot 2.0之后用于对嵌入式Servlet容器进行配置。	

      ```java
      @Bean
      public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer(){
      	return new WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>() {
      		@Override
      		public void customize(ConfigurableServletWebServerFactory factory) {
      			//
      			ErrorPage errorPage400 = new ErrorPage(HttpStatus.BAD_REQUEST, "/error-400.html");
      			ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-404.html");
      			ErrorPage errorPage500 = new ErrorPage(
      					HttpStatus.INTERNAL_SERVER_ERROR, "/error-500.html");
      			factory.addErrorPages(errorPage400, errorPage404, errorPage500);
      		}
      	};
      }
      ```
      }`

2. 国际化

   1. 简述：国际化简单地说就是在不修改代码的情况下，根据不同语言及地区显示相应的语言界面。

   2. Spring中对国际化文件支持的基础接口是MessageSource。

   3. 国际化的具体配置：

      1. 默认情况下，国际化资源文件的基础名为messages，且存放在classpath根路径下，即messages.properties(默认配置)、messages_zh_CN.properties(中文配置)、messages_en_US.properties等等。

         1. 如果使用的是以上默认配置，则无需在springBoot的配置文件中配置spring.messages.basename,否则需要配置  文件夹名.自定义的基础名。

      2. SpringBoot默认就支持国际化，而且不需要做过多的配置，只需要在resources/下定义好国际化配置文件即可。

         1. 做好相应的配置之后，可以直接在前端模板中直接调用，如下

            `<!DOCTYPE html>

            <html>

            <head>

            ​    <meta charset="UTF-8" />

            ​    <title>hello spring boot</title>

            </head>

            <body>

            ​    <p><label th:text="#{国际化配置文件中的key}"></label></p>

            </body>

            </html>`

         2. 国际化信息在代码中的调用

            1. 首先在需要的地方直接注入MessageSource

               `@Autowired

               private MessageSource messageSource;`

               * 需要注意的是，messageSource是org.springframework.context.MessageSource下的类。 

            2. 在正式使用之前，还得知道一个知识点，如何得到当前请求的Locale，共有两种方式

               1. Locale locale = LocaleContextHolder.getLocale()；(第一种方式更好用，不需要传参)
               2. Locale locale1 = RequestContextUtils.getLocale(request);

            3. 有了Locale就可以很简单地得到国际化的数据

               1. String msg = messageSource.getMessage("welcome",null,locale);