SpringBoot小技巧

1. 项目启动时初始化资源

   * 在springBoot中，项目启动初始化资源可以实现CommandLineRunner接口，CommandLineRunner会在所有的Spring Beans都初始化之后，SpringApplication.run()之前执行，非常适合在应用程序启动之初进行一些数据初始化的工作。

   ```java
   @Component
   //实现CommandLineRunner接口并重写run方法
   @Order(数字越小启动越早)
   public class Runner implements CommandLineRunner {
       @Override
       public void run(String... args) throws Exception {
           System.out.println("The Runner start to initialize ...");
       }
   }
   ```

   * 如果在启动容器的时候需要初始化很多资源，并且初始化资源之间有序，可以通过@Order设置CommandLineRunner的执行顺序。@Order(数字越小启动越早)

2. 

