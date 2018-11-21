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

		