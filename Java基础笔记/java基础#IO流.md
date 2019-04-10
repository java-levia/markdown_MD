#java基础#
##IO流&异常处理&File&递归##

1.异常 Throwable类

	1. 异常Throwable类是java语言中所有错误或者异常的超类，它有两个子类，Error和Exception。
	
	2. 严重问题：Error
		- Error用于指示合理的应用程序不应该试图捕获的严重问题。Error在程序中无法处理
		
	3. 普通问题：Exception
		- 编译期 问题：不是RuntimeException的异常，这类异常需要我们在代码中进行处理，否则无法通过编译。
		- 运行期问题：RuntimeException，出现这种问题说明我们的代码不合理，需要修改代码。
	
	4. 如果程序出现问题，我们没有进行处理，虚拟机会做出默认的处理。
		- 虚拟机的处理方式是将异常的名称、原因及出现的问题等信息输出在控制台。同时程序停止在出错的地方，无法继续向下运行
	
	5. 异常处理
		1. try...catch...finally
			- try里面的代码越少越好，因为try里的代码是需要走异常处理机制的，虚拟机是需要分配一些资源来管理try里的代码。
			- catch里面必须有内容，只有这样才能对catch所处理的问题暴露出来，否则就相当于隐藏了问题。
			- 在try...catch语句中，一旦try中出现问题，就会和catch里面的问题进行匹配，一旦匹配成功，就执行catch里面的处理，然后就结束了try...catch语句（这时候会出现在try中位于问题语句下方的语句无法执行到的问题） 
			- 在异常处理中，能明确的尽量明确，不要直接用Exception来处理（用Exception处理的时候，虚拟机会拿着出现的异常到Exception的子类中进行匹配，这样有点降低程序的效率）
			- 平级关系的异常谁前谁后无所谓，如果出现了子父关系，父类的异常必须在后，否则会报编译器异常
			- JDK7出现了一个新的异常处理方案  -> try{}catch(异常1 | 异常2 | ... e){}   ,但这种处理方式也有一个问题，就是括号里的多个异常必须是平级关系。
			- 在try里面发现问题后，jvm会帮我们生成一个异常对象，然后把这个对象抛出，和catch里面的类进行匹配。如果该对象是某个类型的，就会执行该catch里面的处理信息。
			- catch中使用e.printStackTrace()获取异常类名和异常信息，以及异常出现在程序中的位置。返回值void。把信息输出在控制台。
			- catch中使用printStackTrace(PrintStream s) 通常用该方法将异常信息保存在日志文件中，以便查阅定位异常。 
	
		2. Throws
			- 定义功能方法时，需要把出现的问题暴露出来让调用者去处理。这种情况可以通过throws在方法上标识。
			- 在程序中，如果一个方法上抛出了编译时异常，方法的调用者必须对异常进行处理，如果方法抛出的是运行期异常，方法的调用者可以不对异常进行处理。
	
		3. throw
			- 在功能方法内部出现某种情况，程序不能继续运行，需要进行跳转时，就用throw把异常抛出。
			- throw用于在方法内部抛出异常对象
			- throws往往表达的是一种抛出异常的可能性，而执行到throw则表示一定抛出了异常
	
		4. 三种异常处理方式的选择
			- 如果该功能内部可以将问题处理，用try，如果处理不了则用throws
		
		5. finally
			- 被finally控制的语句体一定会执行，但是如果在执行到finally之前jvm退出了，则finally不能执行了。
			- 用于释放资源，在io流操作或者数据库操作中经常会见到。
			- 如果catch里面有return语句，finally语句的代码会在return之前执行（严格来说并不是再return之前，而是在return执行到一半的时候）。而且，如果finally中的语句对return的结果有更改，最终return的还会是运行finally之前的那个结果，因为在执行finally之前return后面的对象已经被替换为了具体的值，而finally对值所做出的更改已经无法改变已经替换的返回值。（也就是说finally对值做出的更改是有效的，只是无法再通过return返回）（有点绕）


​		
		6. 自定义异常
			- 自定义异常需要继承Exception或者RuntimeException
			- 自定义异常如果需要异常信息提示，需要在自定义异常中定义一个有参构造并使用super关键字调用父类的有参构造。（定义了有参构造的同时也必须定义一个无参构造）


		7. 异常的注意事项
			1. 子类重写父类方法时，子类的方法必须抛出相同的异常或父类异常的子类。
			2. 如果父类抛出了多个异常，子类重写父类的方法时，只能抛出相同的异常或者异常的子集，子类不能抛出父类没有的异常。
			3. 如果被重写的父类方法没有异常抛出，那么子类绝对不可以抛出异常，如果子类方法内有异常发生，那么子类只能try，不能throws。


2.IO流

	1. File类
	
		1. 想要实现IO的操作，就必须直到硬盘上的文件表示形式。Java所提供的file类就是这样一个类。
		
		2. File：文件和目录路径名的抽象表示形式。
	
		3. File类构造方法：
			- File（String pathname）:根据一个路径得到File对象
			- File（String parent, String child）：根据一个目录和一个子文件/子目录得到一个File对象。
			- File（File parent, String child)：根据一个父File对象和一个子文件/子目录得到File对象。
			- 以上方法运行既不能创建文件，也无法表示文件是否存在。
	
		4. File类的创建功能
			- public boolean createNewFile()：如果文件不存在，则创建文件返回true，如果文件存在则返回false。如果父文件夹不存在，运行起来会报IO异常
			- public boolean mkdir()：如果文件夹不存在则创建文件夹，如果文件夹存在返回false。如果用这个方法创建文件夹，父文件夹不存在的时候运行起来虽然不会报错，但会返回false，也就是无法创建。
			- public boolean mkdirs()：创建文件夹，如果父文件夹不存在，会一起创建出来，但这个方法只能创建文件夹。
			- 如果创建文件或者文件夹忘了写盘符路径，则默认创建在项目路径下。
	
		5. File类的删除功能
			- public boolean delete()：使用file对象调用delete()方法就可以删除文件夹或者文件。
			- 注意：java的文件删除不走回收站。如果要删除一个多层文件夹，需要从最里面的文件夹依次往外删除，否则无法成功。
	  
		6. 重命名（也可用作剪切）
			- public boolean renameTo(File dest)
				- 如果路径名相同，这个方法用于重命名
				- 如果路径名不同，这个方法用作剪切并重命名。
	
		7. 判断功能
			- public boolean isDirectory(): 判断是否是目录
			- public boolean isFile(): 判断是否为文件
			- public boolean exists(): 判断是否存在
			- public boolean canRead(): 判断是否可读
			- public boolean canWrite(): 判断是否可写
			- public boolean isHidden(): 判断是否隐藏
	
		8. 获取功能
			1. 初级获取功能
			- public String getAbsolutePath()：获取绝对路径
			- public String getPath()：获取相对路径
			- public String getName(): 获取名称
			- public long length(): 获取文件中内容的长度。单位 字节
			- public long lastModified()：获取最后一次的修改时间，毫秒值。 
			2. 高级获取功能
			- public String[] list(): 获取指定目录下的所有文件或者文件夹的名称数组。
			- public File[] listFiles()：获取指定目录下的所有文件或者文件夹的File数组。
			- 文件过滤器
				- public String[] list(FilenameFilter filter);
				- public File[] listFiles(FilenameFilter filter);
				- 以上两个方法，可以使用匿名内部类的方式，重写其中的accept方法，accept返回true时表示对文件放行。
	
	2. 递归
	
		1. 方法定义中调用方法本身的现象叫递归
			1. 递归一定要有出口
			2. 递归的次数不能太多，否则就内存溢出
			3. 构造方法不能递归使用
		
		2. 递归解决问题的思想
			1. 先分解后合并
			2. 做递归需要些一个方法
				1. 明确参数类型和返回值类型
				2. 明确出口条件（结束条件）
				3. 找出其中的特殊情况
	
	3. IO流
	
		1. IO流用来处理设备之间的数据传输（上传和下载文件）
		
		2. Java对数据的操作都是通过流的方式 
	
		3. IO流的分类：
			1. 根据流向分可以分为：输入流和输出流
			2. 根据数据类型分可分为：
				1. 字节流：
					- 字节输入流		InputStream
					- 字节输出流		OutputStream
	
				2. 字符流
					- 字符输入流		Reader
					- 字符输出流		Writer
	
		4. 在用完字节输出流对象之后要进行close，关闭流对象主要有两个用处
			1. 让流对象变成垃圾，这样就可以被垃圾回收器回收了
			2. 通知系统去释放跟该文件相关的资源
	
		5. 计算机是如何识别什么时候该把两个字节转换为一个中文呢
			1. 在计算机中中文的存储分为两个字节，第一个字节肯定是负数，第二个字节常见是负数，可能有正数，但是不影响。
	
		6. java  IO 的缓冲区类
			1. 字节流缓冲区类：
				- 缓冲区类有一个默认的缓冲区大小，同时也可以通过构造方法指定缓冲区的大小，但平时默认的缓冲区大小就够用了。简单来说，buffered...Stream 只是将File...Stream进行了封装，其后的操作模式，和File...Stream一样
				- 写数据：BufferedOutputStream（OutputStream out）
					
				- 读数据：BufferedInputStream(InputStream in)