#消息中间件#
##activeMQ##

1. 基本概念
	1. Destination
		- 目的地，JMS Provider(消息中间件）负责维护，用于对Message进行管理的对象。MessageProducer需要指定Destination才能发送消息，MessageConsumer需要指定Destination才能接受消息。

	2. Producer
		- 消息生成者，负责發送Message到目的地。应用接口为MessageProducer。

	3. Consumer（Receiver）
		- 消息消费者，负责从目的地中消费（处理|监听|订阅）Message。应用接口为MessageConsumer。

	4. Message
		- 消息，消息封装一次通信的内容。常见类型有：StreamMessage、BytesMessage、TextMessage、ObjectMessage、MapMessage。

	5. ConnectionFactory
		- 链接工厂，用于创建链接的工厂类型。注意，不能和JDBC中的ConnectionFactory混淆。

	6. Connection
		- 链接。用于建立访问ActiveMQ连接的类型，由链接工厂创建。

	7. Session
		- 会话，一次持久有效有状态的访问。由链接创建。是具体操作消息的基础支撑。

	8. Queue & Topic
		- Queue是队列的目的地，Topic是主题  目的地。都是Destination的子接口。
		- Queue： 队列中的消息，默认只能由唯一一个消费者处理。一旦处理完，消息删除
			- 消息一旦被消费就不再存在于队列中queue是支持存在多个消费者的，但是队列中的消息只能被其中一个消费者所消费。
			- 当消费者不存在时，消息会一直保存，直到有消费者消费为止(或者消息超时）
			- 队列中的消息要配置多个consumer时会用到监听，且监听的消费者是轮流处理消息的（类似轮询）
		- Topic： 主题中的消息，会发送给多个消费者同时处理。只有在消息可以重复处理的业务场景中可以使用
			- 主题中的消息不会保存，所以一定要设置监听机制，如果在没有消费者监听目的地的情况下發送消息，消息会直接被丢弃

		- 只要有监听的设置，就需要使用in.read阻塞线程，防止结束而关闭监听线程
			

	9. PTP（点对点的消息处理模型）
		- 是基于Queue实现的消息处理模型

	10. PUB & SUB
		- Publish & Subscribe.消息的发布/订阅模型，是基于Topic实现的消息处理模型


2. ActiveMQ安装
	1. activeMQ安装很简单，直接在官网下载压缩包，上传到服务器解压缩就可以使用（前提是服务器安装好JDK并配置好环境变量）
	2. activeMQ的启动  在解压文件下的bin文件夹下有一个名为activemq的文件，执行 ./activemq start 就可以启动activemq
	3. activeMq中集成了一个运行容器jetty，在访问activeMQ控制台的时候输入的端口号其实是jetty的端口号8161，如果要更改jetty的端口号可以在conf目录下的jetty.xml中进行更改。
	4. 在进入activemq控制台后需要输入用户名和密码，其实是jetty容器的用户名和密码，在jetty-realm.properties配置文件中已经配置好了两个用户：user/admin,用户名和密码都是这两个，如果需要更改也可以在这里面更改。
	5. ActiveMQ控制台中导航栏选项的意义
		- Queue：消息队列中的相关信息
		- Topic：发布订阅模式中的相关信息
		- Subscribers 用于发布订阅的一个检查端口
		- Connections 查看当前的activeMQ有有哪些链接
		- Network 网络的相关信息
		- Scheduled 定时任务
		- Send 可以用于测试消息的发送
	6. activemq.xml		groups.properties		users.properties    jetty-realm.properties	jetty.xml是比较重要的五个配置文件

	7. 消息的确认机制
		1. 创建会话的时候，必须传递两个参数，分别代表是否支持事务和如何确认消息处理。（这两个参数分别针对的是消息的发送者和接收者，是否支持事务只对发送者有效，消息处理机制只对消息处理者有效）
			- 参数1 transacted-是否支持事务，数据的类型是boolean，true表示支持，false表示不支持
			- true 支持事务，第二个参数对producer来说默认无效，建议传递的数据是Session.SESSION_TRANSACTED
			- false 不支持事务，常用参数，第二个参数必须传递，且必须有效
				- AUTO_ACKNOWLEDGE -自动确认消息，消息的消费者处理后，自动确认，常用。商业开发不推荐
				- CLIENT_ACKNOWLEDGE -客户端手动确认。消息的消费者处理后，必须手工确认。（这个确认机制只是等客户端确认消息之后就删除消息，另外的客户端并不能拉取到消息，也就是说还是只能被其中一个消费者所消费，并不能被多次处理，DUPS_OK_ACKNOWLEDGE这种处理机制才能被多次消费） 
				- DUPS_OK_ACKNOWLEDGE -有副本的客户端手动确认。（这种方式一个消息可以多次处理，可以降低Session消耗，在可以容忍重复消息时使用。（不推荐））

	8. PTP和PUB/SUB的对比
	
		
		