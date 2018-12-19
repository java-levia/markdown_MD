#SpringBoot#
##Redis&MongoDB##

1 Redis

	1 添加依赖 
	<dependency>    
		<groupId>org.springframework.boot</groupId>    
		<artifactId>spring-boot-starter-data-redis</artifactId> 
	</dependency> 
 
	2 配置文件： 
		#redis spring.redis.host=localhost 
		spring.redis.port=6379 
		#spring.redis.password=123456 
		#spring.redis.database=0 
		#spring.redis.pool.max-active=8  
		#spring.redis.pool.max-idle=8  
		#spring.redis.pool.max-wait=-1  
		#spring.redis.pool.min-idle=0  
		#spring.redis.timeout=0 
 
	3 测试 
 
		import org.springframework.beans.factory.annotation.Autowired; 
		import org.springframework.data.redis.core.StringRedisTemplate; 
		import org.springframework.data.redis.core.ValueOperations; 
		import org.springframework.stereotype.Component; 
		 
		/** 
		 * @author wujing 
		 */ 
		@Component 
		public class RoncooRedisComponent { 
		 
		 @Autowired 
		 private StringRedisTemplate stringRedisTemplate; 
		 
		 public void set(String key, String value) { 
		  ValueOperations<String, String> ops = this.stringRedisTemplate.opsForValue(); 
		  if (!this.stringRedisTemplate.hasKey(key)) { 
		   ops.set(key, value); 
		   System.out.println("set key success"); 
		} else { 
		   // 存在则打印之前的 value 值 
		   System.out.println("this key = " + ops.get(key)); 
		  } 
		 } 
		 
		 public String get(String key) { 
		  return this.stringRedisTemplate.opsForValue().get(key); 
		 } 
		 
		public void del(String key) { 
		  this.stringRedisTemplate.delete(key); 
		 } 
		} 
		 


		@Autowired  
		private RoncooRedisComponent roncooRedisComponent; 
		 
		@Test  
		public void set() {   
			roncooRedisComponent.set("roncoo", "hello world");  
			} 
		 
		@Test  
		public void get() {   
			System.out.println(roncooRedisComponent.get("roncoo"));  
			} 
		 
		@Test  
		public void del() {   
			roncooRedisComponent.del("roncoo"); 
		 } 
		 
		注：生产环境下，如果外网可以访问，一定要设置密码！ 

2 MongoDB
	
	命令行启动：mongod.exe --dbpath d:\roncoo_mongodb\

	1 本机的MongoDB安装目录在C:\javaEnviroment   启动方式为git切换到MongoDB所在的目录 使用命令npm start 就可以启动  然后浏览器访问http://127.0.0.1:1234 就可以进入可视化页面

	2 

	