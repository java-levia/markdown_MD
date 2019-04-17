SpringBoot集成Memcached

1. 简单介绍

   Memcached是一个高性能的分布式内存对象缓存系统，用于动态web应用以减轻数据库负载。它通过在内存中缓存数据和对象减少读取数据库的读取次数，从而提高访问速度。

   Memcached基于一个存储键值对的hashmap，其守护进程是用c写的，但是客户端可以用任何语言来编写，并通过memcached协议与守护进程通信。

   因为SpringBoot没有针对Memcached提供对应的组件包，因此需要我们自己来集成。官方推出的java客户端SpyMemCached是一个比较好的选择之一。

2. 集成

   * 添加依赖

   ```xml
   <dependency>
     <groupId>net.spy</groupId>
     <artifactId>spymemcached</artifactId>
     <version>2.12.2</version>
   </dependency>
   ```

   * 添加配置

   ```yaml
   memcache.ip=192.168.0.161
   memcache.port=11211
   ```

   * 设置配置对象

   ```java
   //添加配置对象
   //这里的@ConfigurationProperties(prefix = "memcache")表示的是将配置文件（application.yml）中以memcache开头的配置载入到这个对象中
   @Component
   @ConfigurationProperties(prefix = "memcache")
   public class MemcacheSource {
   
       private String ip;
   
       private int port;
   
       public String getIp() {
           return ip;
       }
   
       public void setIp(String ip) {
           this.ip = ip;
       }
   
       public int getPort() {
           return port;
       }
   
       public void setPort(int port) {
           this.port = port;
       }
   }
   ```

   * 在程序启动的时候初始化memcache

   ```java
   @Component
   public class MemcachedRunner implements CommandLineRunner {
   
       @Resource
       private  MemcacheSource memcacheSource;
   
       private MemcachedClient client = null;
   
       @Override
       public void run(String... args) throws Exception {
           try {
               client = new MemcachedClient(new InetSocketAddress(memcacheSource.getIp(),memcacheSource.getPort()));
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   
       public MemcachedClient getClient() {
           return client;
       }
   
   }
   ```

   * 简单测试

   ```java
   @RunWith(SpringRunner.class)
   @SpringBootTest(classes = RedisDemoApp.class)
   public class RepositoryTests {
   
     @Resource
       private MemcachedRunner memcachedRunner;
   
     @Test
     public void testSetGet()  {
       MemcachedClient memcachedClient = memcachedRunner.getClient();
       memcachedClient.set("testkey",1000,"666666");
       System.out.println("***********  "+memcachedClient.get("testkey").toString());
     }
   
   }
   ```

   