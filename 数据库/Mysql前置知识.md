Mysql前置知识

1. 版本

   * 5.x版本时期：

     5.0-5.1：早期产品的延续，只是在第四版的基础上做升级维护

     5.2-5.3：这两个版本用的特别少，平时生产环境不要用这两个版本

     5.4-5.x：mysql整合了三方公司的新存储引擎，InnoDB就是在5.4版本整合进来的，企业中最常用的版本也是5.5版本

2. 核心目录

   * /var/lib/mysql   mysql安装目录
   * /usr/share/mysql    配置文件所在目录
   * /usr/bin    命令目录（mysqladmin\mysqldump等）
   * /etc/init.d/mysql     启停脚本

3. MYSQL配置文件

   * my-huge.cnf     高端服务器配置    1-2g内存就算高端服务器

   * my-large.cnf     中等规模服务器

   * my-medium.cnf    一般规模服务器

   * my-small.cnf     较小规模服务器

     但是以上配置文件mysql默认是不识别的，默认只能识别/etc/my.cnf,所以如果/etc目录下不存在这个配置文件，可以复制my-huge.cnf改名为my.cnf粘贴到/etc目录下进行相关配置

     注意：mysql5.5默认配置文件是/etc/my.cnf;5.6的默认配置文件/etc/mysql-default.cnf

4. mysql原理

   * Mysql逻辑分层：

     连接层：主要是连接外部客户端，获取sql语句，这一层本身不处理sql，会将他传给下一层

     服务层：这一层处理sql语句，同时还能优化sql，但是有可能会将我们自己写的优化过的sql语句再进行处理，有可能会降低执行效率

     ```
     查询执行计划：explain+SQL语句
     
     执行计划中各字段的含义：
     id:编号
     select_type:查询类型
     table：表
     type:类型
     possible_keys: 预测用到的索引
     key：实际使用的索引
     key_len: 实际使用索引的长度（这个概念比较模糊）
     ref：表之间的引用
     rows: 实际通过索引查到的数据个数  
     Extra：额外的信息，有以下几种
     1. using filesort: 如果这个字段里面出现using filesort则表示性能消耗比较大，表示当前sql语句需要一次额外的一次查询，一般出现在order by语句中
     （对于单索引：如果排序和查找是同一个字段则不会出现using filesort，如果排序和查找不是同一个字段，则会出现using filesort ，表示需要一次额外的查找（或排序）
       对于复合索引：不能跨列（最佳左前缀） 结论是：where 和order by按照复合索引的顺序使用，不要跨列或无序使用
     ）
     2. using temporary:性能损耗大，用到了临时表。一般出现在group by语句中
     
     3. using index: 性能提升了；索引覆盖。原因，不需要读取源文件，只从索引文件中获取数据，只要使用到的列全部在索引中，就是索引覆盖
      
     
     
     执行计划中，id值相同，从上往下顺序执行
     表的执行顺序会因为表中数据条数的改变而改变，其原因是笛卡尔积，数据量越大的表越往后执行
     
     id值不相同的时候，id值越大越优先查询（id值是根据什么得 来的？）
     本质是在嵌套子查询时，先查询最内层，再查外层
     
     select_type：
     PRIMARY : 包含子查询SQL中的子查询（一般是最外层的sql）
     SUBQUERY: 嵌套在SQL语句内部的子查询语句
     SIMPLE: 简单查询（不包含子查询/union的查询）
     DERIVED: 衍生查询（在查询的时候用到了临时表）
     	a. 在from子查询（from后面跟着子查询语句查询出的临时表）中只有子查询出来的唯一一张表，那该表就是衍生表
     	b. 在from子查询中，如果有table1 union table2，则table1就是derived，table2就是union表
     	
     type: 越靠左的查询效率越高（对type进行优化的前提是有索引）
     	system>const>eq_ref>ref>range>index>all
     	最左边的几个级别只是理想情况，实际能达到的比较好的级别是ref和range
     	
     ```

    因此，只记录ref以及以下级别的type
      ref： 非唯一性索引，对应每个索引键的查询，返回匹配的所有行（针对索引中的每个值，可以返回0个、1个或多个），比如说，给某个表中的age这个字段加上了索引，使用age这个字段作为筛选条件时，查出了0条 1条或者多条 针对这个查询就是ref级别的查询（=）
    range: 检索指定范围的行，具体就是指的where后面是一个范围查询（between, in , >, <, >= 等，这几个关键词中，in有时候会导致索引失效，导致级别为all）
      index：查询全部索引中的数据，具体是指将某个索引中的数据全查一遍，对这个索引以外的其他字段没有进行查询

      all： 查询全部表中的数据，指全表扫描，对表中的每个字段的数据都进行了查询，性能最差
     ```
     
     引擎层：这一层是mysql的存储引擎，常见的由MyIsam和InnoDB，早期的mysql默认引擎是MyIsam,5.5.5版本以后默认引擎变为InnoDB
     
     存储层：这一层是真正存储数据的一层 
     ```

5. sql优化

   sql执行存在的问题：性能低、执行时间长、等待时间长、索引失效、服务器参数设置不合理

   * SQL

     编写过程：select ..from..join ..on..where..group by..having..order by..limit..

     解析过程：from..on..join..where..group by.. having...select..order by ..limit

   * SQL优化，主要就是在优化索引

     索引是一个数据结构，为了帮助Mysql高效获取数据的数据结构（索引的数据结构是树，常用的是B树、hash树。mysql里用的是B树）

     索引的弊端：

      	1. 索引本身很大，需要存放在内存/硬盘里（通常存放在硬盘中 ）
      	2. 索引并不是所有情况都适用：
           	1. 少量数据不适用  
          	2. 频繁变化的列不适合做索引 
          	3. 很少使用的列不适合做索引
     	3. 索引会降低增删改的效率

     索引的优势：

     	1. 提高查询的效率（降低IO的使用率）

      	2. 降低CPU使用率

    * 索引分类

      主键索引：主键索引和唯一索引很相似，唯一的区别是，主键索引不能是null 唯一索引可以是null

      单值索引：单列的索引，一个表可以有多个单值索引。

      唯一索引：不能重复。id就属于唯一索引

      复合索引：多个列构成的索引

   * 创建索引

     方式一：create

     create 索引类型 索引名 on 表（字段 ）

     单值索引： create index name_index on  student(name);

     唯一索引：create unique index id_index on student(id);

     复合索引：create index id_name_index on student(id, name);

     方式二：alter table

     alter table 表名 索引类型  索引名（字段）

     单值索引：alter table student add index name_index(name);

     唯一索引：alter table student add unique index id_index(id);

     复合索引：alter table student add index in_name_index(id, name);

   * SQL性能问题

     分析sql的执行计划

     * explain关键字可以模拟sql优化器执行sql语句，让开发人员知道自己编写的sql的状况

   

   Mysql查询优化器会干扰我们的优化

   

 6. B树与索引

    * 三层Btree可以存放上百万条数据
    * Btree一般都指的B+树，数据全部存放在叶节点中
    * B+树中查询任意数据的次数：n次（n指的是B+树的高度），因为所有数据都存在叶节点中
    * 

