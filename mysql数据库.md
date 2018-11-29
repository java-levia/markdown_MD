#数据库#
 
1 存储过程

		* 我们常用的SQL语句在执行的时候都要先完成编译，而存储过程是一组为了完成特定功能的SQL语句集，编译后存储在数据库中，用户通过指定存储过程的名字并给信参数来调用执行它。mysql在5.0之后开始支持存储过程。
		
		* 存储过程的优点
			- 增强了SQL语言的功能和灵活性
			- 存储过程允许标准组件式编程
			- 存储过程能够实现较快的执行速度
		
		* 存储过程的创建及调用
			* 创建无参数的存储过程
				- create procedure PROCEDURE_NAME()
				- begin
				- STATEMENT
				- end
			* 调用存储过程
				- call PROCEDURE_NAME()
		 
			* 在存储过程中定义变量
				- declare 变量名 变量类型 default 默认值；
				- 例： declare a varchar(20) default 'abc';

				- 在存储过程中定义变量但不赋予初始值，可以在接下来的语句中使用 > set 函数进行赋值
				- 例：> declare a varchar(20);
					- > set a = 'abc';

			* 存储过程中参数的用法
				- 存储过程的参数有三种  
					* in: 输入参数
					- 例：create procedure t1_pro(in in_pro int)
					- > begin
					- > set in_pro = in_pro+1;
					- > select in_pro;
					- > end

					- 为参数设置初始值 > set @in_pro=3;
					- 调用存储过程 > call t1_pro(@in_pro);  
					- 输出的结果为4；
					- 此时调用@in_pro   > select @in_pro ,发现输出的结果为3；
					- 结论：in 参数必须在调用存储过程之前进行初始化，且在存储过程中对in参数作出的改变不能返回（在存储过程之外调用参数，会发现在存储过程中对参数做出的改变是无效的）


					* out：输出参数
					-  > create procedure t2_pro(out out_pro int)
					-  > begin 
					-  > select out_pro;
					-  > set out_pro=18;
					-  > select out_pro;
					-  > end
					-  为参数设置初始值 > set @out_pro = 4;
					-  调用存储过程 发现显示结果分别为 null 和 18
					-  此时调用 > select @out_pro ,显示的结果为18；
					-  结论：out 参数不认可在存储过程以外赋的值，但对存储过程以内进行的赋值会得到保存并可以进行返回（在外部调用参数，会发现存储过程中对参数做出的改变是有效的。）

					* inout：输入输出参数
					- 输入输出参数就像是in参数和out参数的综合体，既可以保存存储过程以外的赋值改变，也可以对存储过程中做出的改变进行返回。 

			* 查看数据库中创建了那些存储过程（和查看函数类似）
				* select name from mysql.proc where db='DATABASE_NAME' and type='PROCEDURE';


2 流程控制

		* 流程控制语句，用于将多个SQL语句，划分或组成符合业务逻辑的代码块。
		* 流程控制语句包括： if语句 case语句 loop语句 while语句 leave语句 iterate语句 
		* 每个流程中，可能只包含一个单独的语句，也可以使用BEGIN...END构造多个语句，语句可以嵌套。

		* if语句
			- > create procedure t_pro(in age int)
			- > begin
			- > if  age >= 18&& age<60 then
			- >   select "成年人";
			- > elseif age < 18 then
			- >   select "未成年人"；
			- > else
			- >   select "老年人";
			- > end if;
			- > end

		* case语句（分支语句） 
			- case语句的简单应用可以在SQL语句中 
				- select <case gender when 'M' then '男' else '女' end> as '性别' from employees; 可以将数据库表中的M 和 F 转换为更容易识别的男和女。 
				- ifnull函数  ifnull(exp1,exp2) 如果exp1为空值，那么返回的结果就是exp2的值 ，如果exp1不为空，则返回exp1的值。
			- case语句在存储过程中的应用
				- > create procedure PROCEDURE_NAME(in v_empno int)
				- > begin
				- > declare addS int;  //定义一个参数
				- > case v_empno    //case语句的判断对象为 v_empno这个参数
				- > when 1001 then      //语义:判断v_empno是否等于1001  如果是 执行下面的语句
				- >     set addS=1500;
				- > when 1002 then
				- >     set addS=2500;
				- > else
				- >     set addS=1000;
				- > end case;
				- > update salaries set salary = addS where emp_no=v_empno;  //将表中的数据更新为case语句中设定的数据
				- > end;
				
		* while语句（循环语句）
			-> create procedure add_pro()
			-> begin
			-> declare a int default 1;
			-> declare b int default 0;
			-> while a<=100 do  //while后跟着循环条件 为真执行
			-> set b = b+ a;
			-> set a =a +1;
			-> end while;  //循环结束语句
			-> select b;
			-> end
			-> @@

			- select max(emp_no) into maxempno from employees;  //这条语句获取employees表中emp_no字段的最大值并赋值给maxempno;

		* repeat语句（另一种循环语句）
			- 语句格式为
				-> repeat
				-> STATEMENT
				-> until 条件   //退出循环的条件
				-> end repeat;

		* loop语句

3 定义条件和处理
	
		条件的定义和处理可以用来定义在处理过程中遇到问题时相应的处理步骤。
			- 语法：declare continue handler for sqlstate '需要处理的错误代码值' set 变量=变量值；

4 存储过程的管理

		查看数据库下的存储过程
			- show procedure status where db='数据库名';（这个命令查看的存储过程比较混乱）
			- 先切换到需要查看存储过程的数据库下 使用 > select specific_name from mysql.proc; (这个命令查看的存储过程比较清晰）
			- 查看存储过程的创建细节  show create procedure <PROCEDURE_NAME);
		删除存储过程
			-  drop procedure if exists <PROCEDURE_NAME>;
	

5 函数
		* 常见函数
			* 单行函数 
				- concat 用于拼接，传入的参数可以是数据库字段，也可以是字符串，或者两者都有
				- length 获取字符串的长度
				- ifnull 对值为null的字段进行处理，用一个固定值替换null
				- trim 用来去掉前后空格，中间的去不掉，如果使用'a' from 'aaaaaaa不用去aaaaa' 这种句式，也可以去掉前后的a
				- lpad 用指定的字符实现左填充指定长度
				- rpad 用指定的字符实现右填充指定长度
				- replace 替换

				- 数学函数
					- round（）  四舍五入
					- ceil（） 向上取整  返回大于等于该参数的最小整数 
					- floor（） 向下取整
				- 日期函数
					- curdate（） 返回当前系统日期，不包含时间
					- curtime（） 返回当前系统时间，不包含日期
					- now（） 返回当前日期+时间
					- year(now()) 返回年
					- month（now()) 返回月  其他的 日 时 分 秒 都可以用同样的方式获取
					- str_to_date 将日期格式的字符串转成指定格式的日期
					- date_format 将日期格式转换成字符
					- 
			* 分组函数（聚合函数） （输出是一组值，用于做统计）
				- sum 求和
				- avg 平均值
				- max 最大值
				- min 最小值
				- count 计算个数 计算非空值的个数
					-  
				- 聚合函数在进行计算的时候会自动忽略null值
				- 聚合函数都可以和distinct搭配使用  使用方式如 count(distinct salary)可以统计工资的种数；
				- 和聚合函数一起查询的字段要求时group by后的字段

			* 分组查询 group by
				- 分组查询中的筛选条件分为两类，一种时分组前的删选，筛选的数据源是原始表。一种是分组后的筛选，筛选的数据源是前面查出来的表。聚合函数做条件进行筛选的情况，肯定是放在having子句中
				- 能用分组前筛选的尽量使用分组前筛选，这样性能会比较好
				- 按多个字段进行分组是可以的，两个分组条件用逗号隔开就行
				
		函数是可以嵌套的

		* 自定义函数
		创建函数
			- > create function <FUNCTION_NAME>(变量1 变量类型，变量2 变量类型...)
			- > returns 返回值数据类型
			- > begin
			- > STATEMENT
			- > return 返回数据;
			- > end;
		
		调用函数
			- 直接 FUNCTION_NAME(变量1，变量2...）就可以调用	

		删除函数
			- drop function if exists FUNCTION_NAME；
			

6 锁

	MYSQL的锁机制
		- 相对于其他数据库而言，MySQL的锁机制比较简单，其显著的特点是不同的存储引擎支持不同的锁机制。
		- 比如 常用的MYISAM引擎采用的是表级锁，InnoDB默认使用行级锁但也支持表级锁。BDB引擎采用的市页面锁。

	MYSQL锁的开销、加锁速度、死锁、粒度、并发性能
		- 表级锁：开销小、枷锁快；不会出现死锁；锁定粒度大，发生锁冲突的概率最高，并发度最低；
		- 行级锁：开销大、加锁慢；回出现死锁；锁定粒度最小，发生锁冲突的概率最低，并发度也最高；
		- 页面锁：开销和加锁时间介于表级锁和行级锁之间，回出现死锁；锁定粒度介于表锁和行锁之间；

		仅从锁的角度来说，表级锁更适合以查询为主，只有少量按索引条件更新数据的应用，如WEB应用。而行级锁更适合于有大量按索引条件并发更新少量不同的数据，同时又有并发查询的应用，如一些在线事务处理系统。
		综上所述，很难说哪种锁更好，只能看情况判断哪种锁更适合。 

	表级锁（MYISAM)

		- 共享读锁： 
			- > lock table TABLE_NAME read;
			- 对数据库表添加共享读锁之后，持锁线程之外的线程依旧可以读取被锁表格的内容，但无论是持锁线程还是其他线程都无法对表格进行增删改操作，持锁线程进行增删改操作会报错，非持锁线程进行增删改操作会出现线程等待，只有解锁之后才会继续执行。

		- 独占写锁
			- > lock table TABLE_NAME write;
			- 对数据库表添加独占写锁后，持锁线程可以对表进行增删该查，除持锁线程以外的其他线程对被锁表格执行增删改查都会造成线程等待，只有等持锁线程解锁后非持锁线程的操作才会继续执行。

		- 持锁线程只能访问或更新被锁的数据库表，如果试图访问或更新其他表会报错。如果需要在持锁阶段访问某个表，可以对那个表加锁后再进行访问。

7 数据库事务



8 数据库基础
	
		* 数据库中的+只能当作运算符使用，不能当连接符使用。两个字符串在sql中使用+连接得到的解果是0
		* 数据库中判断控制不能使用“=null”，要判断是否为null  只能使用 is null。
		* 安全等于<=>  这个符号可以用于判断null值也可以用于判断普通类型的值，比如说  salary <=> null  也是可以的
		* 排序  按多种方式排序，中间可以逗号隔开
		* order by 一般放在查询语句的最后面，但是如果用到limit子句，也可以将limit子句放在order by后面
		* 所有的数据库中，索引都是从1开始的，这点和java不一样。
		

9 连接查询
	
		连接查询在mysql数据库中有两套标准，其一是sql92标准，另一个是sql97标准 
		
	sql92标准
		* 等值连接
			* 指的是通过一个两个表中意义相同的字段值相等的方式消除两个表连接产生的笛卡尔积

		* 自连接
		* 非等值链接

	sql99标准

		语法 
			- > select 查询列表
			- > from 表1 别名 连接类型
			- > join 表2 别名
			- > on 连接条件
			- > where 筛选条件
			
		* 内连接
			* 关键字 inner  inner可以省略
		* 外连接
			* 外连接的查询结果为主表中的所有记录，如果从表中有和他匹配的，则显示匹配的值，如果从表中没有和他匹配的，则显示为null。
			* left join左边是主表，right join 右边是主表。
			- 左外连接
				* 关键字 left outer   其中outer可以省略
				* 
			- 右外连接
				* 关键字 right outer  outer可以省略
				* 
			- 全外连接
				* 关键字 full  outer outer可以省略
		* 交叉连接
		   交叉连接的关键字是cross  交叉连接的结果就是笛卡尔乘积
	
10 子查询（内查询）

		出现在其他语句中的select语句，都被称为子查询或内查询（增删改查语句中都可以有子查询）
		
		分类：
		 * 按子查询出现的位置
			 * select 后面
			 * from后面
			 * where 或者 having后面
			 * exists后面
	
		* 按结果集的行列数不同
			* 

	
	 