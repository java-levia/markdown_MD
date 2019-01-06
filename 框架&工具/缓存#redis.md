#缓存方案#
##Redis&##

###redis###

1. Redis的安装和配置

	1. 下载redis
	2. 在/usr/local 文件夹下创建/redis文件夹  将下载下来的redis压缩包ftp到服务器中的/usr/local/redis文件夹下
	2. 解压压缩包后在解压得到的文件夹中执行make命令进行编译（redis执行的是源码安装）
	3. 切换到编译完成后的文件夹中，进入src目录，执行make install命令进行安装
	4. 安装完成后，就可以进入src目录下执行 ./redis-server进行前端启动（但这种方式不能用于生产环境）
	5. 将 src文件夹下的 redis-server redis-cli 文件复制到redis目录下
	6. 将redis的配置文件 redis.conf 复制一份到redis目录下，更改配置文件中的 daemonize 后面的no为yes
	7. 使用 ./redis-server redis.conf 后端启动redis
	8. 使用 ./redis-cli 启动redis客户端对数据库进行操作


2. redis的操作
	
	1. exists keys  //测试指定的key是否存在
	2. del key1 key2 key3 删除给定的key
	3. type key 返回给定的key的value类型（返回的时value的类型而不是key的类型）
	4. keys pattern 返回匹配指定模式的所有key  这里的pattern指的时匹配符  比如通配符* keys *表示所有key或者 keys *e 表示以e结尾的所有key
	5. rename oldkey newkey 改名
	6. dbsize 返回当前数据库的key数量
	7. expire key seconds 为key指定过期时间
	8. ttl key 返回key的剩余过期时间
	9. select db-index 选择数据库（redis中也允许创建多个数据库，最多允许创建16个数据库，标号为0-15）
	9. move key db-index 将key从当前数据库移动到制定的数据库
	10. flushdb 删除当前数据库中的所有key
	11. flushall 删除所有数据库中的所有key


3. Redis支持的数据类型
	
	1. string类型
		* redis的string类型可以存储任何类型的数据。包括jpg图片或者序列化的对象。 
		* 单个value值最大上限是1G字节。
		* 如果只用string类型，redis就可以被看做加上持久化特性的memcache。

		* string类型操作
			- set key value
			- mset key1 value1 key2 value2 ...keyN valueN  一次性设置多个key-value
			- mget key1 key2 key3 key4...keyN  一次性获取多个key的值
			- incr key 对key的value做加加操作，然后返回新的值
			- decr key 对key的value做减减操作，返回新值
			- incrby key integer   加指定值
			- decrby key integer   减指定值
			- append key value 给指定key的字符串值追加value
			- substr key start end 返回截取过的key的字符串值

	2. list类型   
		* 其实就是一个双向链表，通过push、pop操作从链表的头部或者尾部添加删除元素。这使得list既可以用作栈，也可以用作队列
		
		* 使用场景
			- 在数据库表中的数据比较多的情况下，如果需要使用数据库中的某个字段排序后的前几条数据，需要对该字段建立索引，在这种需求比较多的情况下，可能导致数据库建立过多索引，影响效率，这种情况下可以使用redis的list进行特定数据的获取（比如说获取最新插入的十条数据这种需求）。
		
		* list类型操作
			- lpush key value  在key对应的list的头部添加字符串元素（value）
			- rpop key  删除链表中key对应的元素
			- llen key 返回key   对应list的长度，key不存在返回0，如果key对应类型不是list返回错误
			- lrange key start end 返回指定区间内的元素，下标从0开始
			- rpush key value  同上，在尾部添加
			- lpop key  从list的头部删除元素，并返回删除的元素
			- ltrim key start end 截取list，保留指定区间内元素
		
		* list类型可用于模拟栈和队列的操作，使用posh和pop的操作可以达到先进先出或者先进后出的目的

	3. set类型
		* redis的set是string类型的无序集合（这里要区别有序和排序的概念，有序是指插入顺序和取出顺序相同，排序是指集合中的元素按照升序或者降序来排序）
		* set元素最大可以包含（2的32次方-1）个元素
		* set集合类型除了最基本的增删操作，其他的有用操作还包括集合的取并集（union），交集（intersection），差集（difference），通过这些操作可以很容易的实现社交软件中的好友推荐功能。

		* set类型操作
			- sadd key member		添加一个string元素到key对应的set集合中，成功返回1，如果元素已经在集合中则返回0，key对应的set不存在则返回错误。
			- srem key member 【member】 从key对应的set集合中移除给定的元素，成功返回1
			- smove p1 p2 member 从p1对应的set中一处member并添加到p2对应的set中。
			- scard key  返回set的元素个数
			- sismember key member 判断member元素是否在set集合中，如果在返回1
			- sinter key1 key2 ...keyN   返回所有给定key的交集
				 sunion key1 key2 ...keyN	 f返回所有给定key的并集
				 sdiff key1 key2 ...keyN	 返回所有给定key的差集
				 smembers key		返回key对应set的所有元素

	4. Sort Set 排序集合类型（注意是排序集合而不是有序集合）
		* redis的sort set是string类型元素的集合，不同的是每个元素都会关联一个权。通过权值可以有序地获取集合中地元素 
		* 该Sort set类型的使用场景：获取热门帖子的回复信息。假设一个场景，需要获取上百万个帖子中热度最高的五个（以回复量作为热度统计标准），这时候可以在sort set中存储一个键值对，键<hotmessage>值为帖子id和backnum回复量，其中id作为与数据库中关联用的字段，将backnum作为权值。sort set中会将权值作为排序的依据。

		* sort set类型操作
			- zadd key score member  添加元素到集合，元素在集合中存在则更新对应的score值
			- zrem key member  删除指定元素，1表示成功，如果元素不存在则返回0、
			- zincrby key incr member 按照incr幅度增加对应member的score值，返回score值
			- zrank key member 返回指定元素在几何中的排名（下标），集合中的元素是按照score从小到大排序的
			- zrevrank key member 同上，但是集合中元素是按score从大到小排的
			- zrange key start end 类似lrange操作，从集合中去指定区间的元素。返回的是有序结果
			- zrevrange key start end  同上，返回的结果是逆序的
			- zcard key 返回集合中元素个数
			- zscore key element 返回给定元素对应的score
			- zremrengebyrank key min max 删除集合中排名在给定区间的元素（权值从小到大排序）
	
	5. 数据类型Hash
		* hash数据类型存储的数据与mysql数据库中存储的一条记录极为相似。

		* hash类型操作
			- hset key field value	设置hash field为指定值，如果key不存在则先创建（field 类似于mysql数据库中的字段
				 hget key field	获取指定的hash field
				 hmget key field 1 ...fieldN 	获取全部指定的hash field
				 hmset key field1 value1  ...fieldN valueN		同时设置hash的多个field
				 hincrby key field integer		将指定的hash field加上给定值
				 hexists key field 	测试指定field是否存在
				 hdel key field 	删除指定的hash field
				 hlen key	返回指定hash的field数量
				 hkeys key		返回hash的所有field
				 hvals key		返回hash的所有value
				 hgetall key 		返回hash的所有field和value


4. 持久化功能

  1. redis为了内部数据的安全考虑，会把本身的数据以文件的形式保存到硬盘中一份，在服务器重启之后会自动把硬盘中的数据恢复到内存（redis）中

  2. redis本身支持多种持久化功能
      - snap shotting快照持久化
         - 该持久化默认开启，一次性把redis中的全部数据保存一份存储到硬盘中，如果数据非常多（10-20G）就不适合频繁进行该持久化操作
           - 该持久化的持久化文件名为dump.rdb,位于配置文件同目录下
           - 可以在redis.conf配置文件下设置快照持久化的相关参数
           - 手动发起快照持久化命令行

             - > ./redis-cli -h 127.0.0.1 -p 6379 bgsave  

         - append only file（AOF持久化）（精细持久化）
         	- 本质：把用户执行的每个’写‘指令（添加，修改，删除）都备份到文件中，还原数据的时候就是执行具体的写指令而已。
         	- AOF持久化默认是不开启的
         	- 开启AOF持久化（开启AOF持久化的时候会清空redis的内部数据，所以最好是在redis第一次启动的时候就开启AOF而不要在运行中途开启）
         		- 将redis.conf配置文件中的appendonly配置项的no修改为yes
         		- 添加配置项 appendfilename appendonly.aof  用来配置aof持久化文件名称
         		- 持久化文件的生成位置依旧是与redis.conf同级目录
         		- aof备份的备份频率  appendfsync  默认为everysec 

         - redis的持久化命令
         	- bgsave	一部保存数据到磁盘（快照保存）
         		 lastsave	返回上次成功保存到磁盘的unix时间戳
         		 shutdown	同步保存到服务器并关闭redis服务器
         		 bgrewriteaof	当日志文件过长时优化AOF日志文件的存储

5. Redis的主从模式

	1. 为了降低每个redis服务器的负载，可以多设置几个，并做主从模式，一个服务器负载写数据，其他服务器负载读数据，主服务器数据会自动同步给从服务器。

	2. 配置主从服务器
		1. 获取主服务器的host和port
		2. 在从服务器的redis.conf配置文件中查找 slaveof 配置项，配置 slaveof MASTER_HOST MASTER_PORT,重启服务器后就可以连接到主服务器。
		3. 从服务器时没有写入能力的（写入也没有意义），如果确实需要写入能力，可以将从服务器中的配置项 slave-read-only 设置为no
			​	

