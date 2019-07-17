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
   
3. 发送的消息对象的序列化

   * 在RabbitTemplate中有一个消息转换器对象，其默认值是一个SimpleMessageConverter,他的序列化方式就是按照JDK的序列化规则进行的，而在我们日常的开发中一般使用JSON格式的字符串做数据传输，所以需要配置自定义的序列化方式

   ```java
   //MessageConverter有多个实现，其中Jackson2JsonMessageConverter可以将对象序列化成json字符串
   
   @Bean
   public MessageConverter messageConverter(){
   		return new Jackson2JsonMessageConverter();
   }
   ```

   

4. 简单的测试队列

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

5. AmqpAdmin: ActiveMQ系统管理功能组件

   * AmqpAdmin可用于创建和删除Queue Exchange Binding

6. RabbitMQ为什么需要使用信道？为什么不知Tcp直接通信
   * TCP的创建和销毁开销特别大。创建需要三次握手，销毁需要四次分手。
   * 如果不用信道，那应用程序就会以TCP链接Rabbit，高峰时每秒成千上万条链接会造成资源的巨大浪费，而且操作系统每秒处理TCP链接数也是有限制的，必定造成性能瓶颈。
   * 信道的原理是一条线程一条通道，多条线程多条通道同用一条TCP链接。一条TCP链接可以容纳无限的信道，即使每秒成千上万的请求也不会成为性能的瓶颈。

7. AutoDelete

   ```java
   @Queue(autoDelete=true) 当所有消费客户端连接断开后，是否自动删除队列，true表示删除；false表示不删除
   @Exchange(autoDelete = true) 当所有绑定队列不再使用时，是否自动删除交换器 true表示删除，false表示不删除
   
   //以上两个属性影响会影响消息的保留情况。当消费者autoDelete为true时，如果消费者都掉线，删除Queue或者Exchange后，生产者所发送的消息在掉线期间无法保留，会出现消息丢失的情况。
   ```

   

8. RabbitMQ中消息确认的ACK机制

   ```java
   //什么是消息确认ACK
   如果在处理消息的过程中，消费者的处理器在处理消息时出现异常，那可能这条正在处理的消息就没有完成消息消费，数据就会丢失。为了确保数据不丢失，RabbitMQ支持消息确认-ACK
   ACK机制时消费者从RabbitMQ收到消息并处理完成后，反馈给RabbitMQ，RabbitMQ收到反馈后才将此消息从队列中删除。
   1. 如果一个消费者在处理消息时出现网络不稳定 服务器异常等状况，那么就不会有ACK反馈，RabbitMQ会认为这个消息没有正常消费，会将消息重新放入队列中。
   2. 如果在集群情况下：RabbitMQ会立即将这个消息推送给这个在线的其他消费者。这种机制保证了在消费者服务端故障的时候，不丢失任何消息和任务。
   3. 消息永远不会从RabbitMQ中删除：只有当消费者正常发送ACK反馈，RabbitMQ确认收到后，消息才会从RabbitMQ服务器的数据中删除。
   
   //ACK机制开发注意事项
   如果忘记了ACK，当Consumer退出时，Message会一直重新分发，然后RabbitMQ会占用越来越多的内存，由于RabbitMQ会长时间运行，因此这个内存泄漏时致命的。
   
   //模拟Ack故障，当消费者在消费消息的过程中抛出异常时，因为没有消费完成而没有确认消息的消费，ACK机制会一直发送这条未被消费的消息，导致队列中的其他消息阻塞。
   //解决ACK故障
   1. 通过try{}catch(){}的方式
   2. 通过在配置文件中配置重试次数
   #开启重试
   spring.rabbitmq.listener.retry.enabled = true
   #设置重试次数/默认三次
   spring.rabbitmq.listener.retry.max-attempts=5
   ```

   