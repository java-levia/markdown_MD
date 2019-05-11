消息中间件RabbitMQ

1. 消息队列的应用场景
   * 异步处理
   * 应用解耦
   * 流量削峰
   * 日志处理等

2. 配置用户（可以用命令行配置也可以在网页的控制台进行配置）
   * 添加/删除用户
     * 添加用户 rabbitmqctl  add_user  Username  Password  新添加的用户是无法登陆控制台的，需要进行授权
     * 删除用户 rabbitmqctl  delete_user  Username
     * 修改用户密码 rabbitmqctl  change_password  Username  Newpassword
     * 查看当前用户列表 rabbitmqctl  list_users
   * 用户授权
     * rabbitmqctl  set_user_tags  User  Tag   user是用户名  Tag是角色  （角色由  administrator，monitoring，policymaker，management等）
   
3. 简单的测试队列

   * 简单的测试队列基本上就是一个消息的发送者发送消息到队列中，一个接收者监听队列，有消息的时候就会取出来消费。SpringBoot中简单队列创建如下

   ```java
   
   
   /**
    * 先创建一个配置类，用于注册队列
   */
   
   @Configuration
   public class RabbitConfig {
   
       private final static String queueName="first_queue";
       @Bean
       public Queue queue(){
           //创建一个队列
           return new Queue(queueName);
       }
   }
   
   //然后使用amqpTemplate进行消息的发送
   @Component
   public class Send {
       @Autowired
       private AmqpTemplate amqpTemplate;
   
       public  void send() {
           String msg = "hello world!!!";
           amqpTemplate.convertAndSend("first_queue", msg);
       }
   
   }
   
   //监听消息队列  关键注解@RabbitListener(queues = "first_queue")
   @Component
   public class Receiver {
       
       @RabbitListener(queues = "first_queue")
       public void process(String str){
           System.out.println("接收到的消息"+str);
       }
   }
   
   
   //简单队列的劣势：耦合性高，一个生产者对应一个消费者  ，如果需要多个消费者消费队列中的消息，这时候就不行了。如果生产者的队列名变更了，同时也需要变更消费者的队列名，耦合性高
   ```

4. WorkQueues  工作队列

   模型是：一个生产者对应多个消费者

   解决的问题：生产者和消费者一一对应可能会导致消息的积压，工作队列就是为了解决这个问题出现的