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

					* 