RocketMQ常用命令



**启动nameserver**

```bash
./home/software/rocketmq-4.5.2/bin/mqnamesrv -n 192.168.239.239:9876 &

nohup sh bin/mqnamesrv &
```

**启动broker**

```bash
nohup sh mqbroker -c /home/software/rocketmq-4.5.2/conf/broker.conf -n 192.168.239.239:9876 autoCreateTopicEnable=true &

nohup sh bin/mqbroker -n 192.0.0.1:9876 -c conf/broker.conf &

```

**关闭broker/namesrv**

```bash
sh bin/mqshutdown broker
```

```bash
sh bin/mqshutdown namesrv
```

**查看集群情况**

```bash
 ./mqadmin clusterList -n 127.0.0.1:9876
```

**查看 broker 状态** 

```bash
./mqadmin brokerStatus -n 127.0.0.1:9876 -b 172.20.1.138:10911 (注意换成你的 broker 地址)
```

**查看 topic 列表**

```bash
 ./mqadmin topicList -n 127.0.0.1:9876
```

**查看 topic 状态**

```bash
 ./mqadmin topicStatus -n 127.0.0.1:9876 -t MyTopic (换成你想查询的 topic)
```

**查看 topic 路由**

```bash
 ./mqadmin topicRoute -n 127.0.0.1:9876 -t MyTopic
```

```bash
在 Rocket_HOME/distribution/target/apache-rocketmq 下执行 " sh bin/mqbroker -m " 来查看 broker 的配置参数
```

