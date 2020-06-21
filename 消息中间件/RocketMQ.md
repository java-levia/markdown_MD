RocketMQ 集群



1. **各角色介绍**
   1. Producer  消息的发送者；
   2. Consumer 消息接收者；
   3. Broker  暂存和传输消息
   4. NameServer  管理Broker
   5. Topic: 区分消息的种类，一个发送者可以发送消息给一个或多个Topic;一个消息接收者可以订阅一个或者多个Topic消息
   6. Message Queue: 相当于Topic的分区，用于并行发送和接收消息。

2. **集群搭建方式**

   1. **集群的特点**

   * NameServer是一个几乎无状态节点，可集群部署，节点之间无任何信息同步. 
   * Broker中，Master与slave的对应关系通过指定相同的BrokerName,不通的BrokerId来定义，如果brokerId为0表示的是主节点，如果是非0的表示从节点。一个主节点可以包含多个从节点。Master也可以部署多个。**每个Broker与NameServer集群中的所有节点建立长连接，定时注册Topic信息到所有NameServer.**
   * Producer与NameServer集群中的其中一个节点（随机选择）建立长连接，**定期从NameServer取Topic路由信息，并向提供Topic服务的Master建立长连接，且定时向Master发送心跳。**Producer完全无状态，可集群部署。
   * Consumer与NameServer集群中的其中一个节点（随机选择）建立长连接，定期从NameServer取Topic路由信息，并向提供Topic服务的Maser、slave建立长连接，且定时向Master、slave发送心跳。

   2. **集群模式**

      1. 单Ｍaster模式

         这种方式风险较大，一旦Broker重启或者宕机时，会导致整个服务不可用。不建议线上环境使用，可以用于本地测试

      2.  多Master模式

         一个集群无slave,全是master,例如两个Master或者三个Master,这种模式的优缺点如下：

         * 优点：配置简单，单个Master宕机或重启维护对应用无影响，在磁盘为RAID10时，即使机器宕机不可恢复情况下，由于RAID10磁盘非常可靠，消息也不会丢（异步刷盘丢失少量消息，同步刷盘一条也不丢），性能最高；
         * 缺点：单台机器宕机期间，这台机器上未被消费的消息在机器恢复之前不可订阅，消息实时性会受到影响。

      3. 多Master多Slave模式（异步）

         每个Master配置一个Slave,有多对Master-Slave,HA采用异步复制方式，主备有短暂消息延迟（毫秒级），这种模式的优缺点如下：

         * 优点：即使磁盘损坏，消息丢失的非常少，且消息实时性不会受影响；Master宕机后，消费者依然可以从slave消费，而且此过程对应用透明，不需要人工干预，性能同多Master模式几乎一样。
         * 缺点：Master宕机，磁盘损坏情况下会丢失少量消息

      4. 多Master多Slave模式（同步）

         每个Master配置一个Slave,有多对Master-Slave,HA采用同步复制方式，即只有主备都写成功，才向应用返回成功，这种模式的优缺点如下：

         * 优点：数据与服务都无单点故障，Master宕机的情况下，消息无延迟，服务可用性与数据可用性都非常高；
         * 缺点: 性能比异步复制模式略低（低10%左右），发送单个消息的RT会略高，且目前版本在主节点宕机后，备机不能自动切换为主机。

      5. 集群工作流程

         1. 启动NameServer,NameServer启动后监听端口，等待Broker、Producer、Consumer连接上来；NameServer相当于微服务中的注册中心
         2. Broker启动，跟所有的NameServer保持长连接，定时发送心跳包。心跳包中包含当前Broker信息（IP+端口等）以及存储所有Topic信息。注册成功后，NameServer集群中就有Topic跟Broker的映射关系。
         3. 收发消息前，先创建Topic,创建Topic时需要指定该Topic要存储在哪些Broker上，也可以在发送消息时自动创建Topic。
         4. Producer发送消息，启动时先跟NameServer集群中的其中一台建立长连接，并从NameServer中获取当前发送的Topic存在哪些Broker上，轮询从队列列表中选择一个队列，然后与队列所在的Broker建立长连接从而向Broker发消息。
         5. Consumer跟Producer类似，跟其中一台NameServer建立长连接，获取当前订阅Topic存在哪些Broker上，然后直接跟Broker建立连接通道，开始消费。

   3. 集群搭建

   4. mqadmin管理工具

      1. 使用方式

         * 进入RocketMQ安装位置，在bin目录下执行./mqadmin {command} {args}

      2. 命令介绍

         * Topic相关

         | 名称 | 含义 | 命令选项 | 说明 |
         | ---- | ---- | -------- | ---- |
         |      |      |          |      |
         |      |      |          |      |
         |      |      |          |      |

         
      
   5. 消费者集群模式下，消费者组中的每个消费者只消费与它关联的queue中的消息，队列维护一个游标，消费者每消费完一条消息，游标就移动到下一条消息上。集群模式下的消费者相当于多个消费者分同一块蛋糕，每份蛋糕只能分给一个消费者。当消费者开启广播模式时，则会消费该Topic下的所有队列中的消息。
   
   
   
3. **最佳实践**

   1. 一个应用尽可能只使用一个 Topic，消息子类型用 tags 来标识，tags 可以由应用自由设置。只有发送消息设置了tags，消费方在订阅消息时，才可以利用 tags 在 broker 做消息过滤。

   2. 每个消息在业务层面的唯一标识码，要设置到 keys 字段，方便将来定位消息丢失问题。服务器会为每个消
       息创建索引（哈希索引），应用可以通过 Topic，key 来查询返条消息内容，以及消息被谁消费。由于是哈希索引，请务必保证 key 尽可能唯一，这样可以避免潜在的哈希冲突。

   3. 消息发送成功或者失败，要打印消息日志，务必要打印 sendresult 和 key 字段。

   4. 对于消息不可丢失应用，务必要有消息重发机制。例如：消息发送失败，存储到数据库，能有定时程序尝试重发或者人工触发重发。

   5. 某些应用如果不关注消息是否发送成功，请直接使用`sendOneWay`方法发送消息。

   6. Consumer 数量要小于等于queue的总数量，由于Topic下的queue会被相对均匀的分配给Consumer，如果 Consumer 超过queue的数量，那多余的 Consumer 将没有queue可以消费消息。

      消费过程要做到幂等（即消费端去重），RocketMQ为了保证性能并不支持严格的消息去重。

      尽量使用批量方式消费，RocketMQ消费端采用pull方式拉取消息，通过`consumeMessageBatchMaxSize`参数可以增加单次拉取的消息数量，可以很大程度上提高消费吞吐量。另外，提高消费并行度也可以通过增加Consumer处理线程的方式，对应参数`consumeThreadMin`和`consumeThreadMax`。

      消息发送成功或者失败，要打印消息日志。

   7. 补充

      7.1. **线上建议关闭autoCreateTopicEnable配置**  该配置用于在Topic不存在时自动创建，会造成的问题是自动新建的Topic只会存在于一台broker上，后续所有对该Topic的请求都会局限在单台broker上，造成单点压力。

   ​		7.2. **broker master宕机情况是否会丢消息**  broker master宕机，虽然理论上来说不能向该broker写入但slave仍然能支持消费，但受限于rocketmq的网络连接机制，默认情况下最多需要30秒，消费者才会发现该情况，这个时间可通过修改参数`pollNameServerInteval`来缩短。这个时间段内，发往该broker的请求都是失败的，而且该broker的消息无法消费，因为此时消费者不知道该broker已经挂掉。 直到消费者得到master宕机通知后，才会转向slave进行消费，但是slave不能保证master的消息100%都同步过来了，因此会有少量的消息丢失。但是消息最终不会丢，一旦master恢复，未同步过去的消息仍然会被消费掉。

4. ##### RocketMQ中tag的坑<https://blog.csdn.net/Dome_/article/details/94584498>