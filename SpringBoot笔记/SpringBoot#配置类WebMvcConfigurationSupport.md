#SpringBoot#
##配置类WebMvcConfigurationSupport&##

1. 配置类WebMvcConfigurationSupport
	1. 该类是springBoot2.0+之后用于取代WebMvcConfigurationAdapter的一个配置类，在其中可以配置静态资源连接、虚拟目录和物理路径的对应，跨域请求等
 <pre class="prettyprit lang-java">
	@Configuration
	public class MyMvcConfig extends WebMvcConfigurationSupport {
	@Value("${imgPath}")
	private String imgPath;
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        //指向外部目录
        registry.addResourceHandler("uploadimg//**").addResourceLocations("file:"+imgPath);
        registry.addResourceHandler("/**")
        .addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }
		//配置好后，可以在方法上使用@CrossOrigin注解允许方法跨域
	    @Override
	    public void addCorsMappings(CorsRegistry registry) {
	        registry.addMapping("/**")//设置允许跨域的路径
	                .allowedOrigins("http://www.yinghuaart.com","*")//设置允许跨域请求的域名
	                .allowCredentials(true)//是否允许证书 不再默认开启
	                .allowedMethods("GET", "POST")//设置允许的方法
	                .maxAge(3600);//跨域允许时间
	    }
	}
</pre>

