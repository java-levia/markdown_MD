#git版本控制#

1 git的文件管理区域
	
		* git本地的文件管理被划分为三个区域：工作区、暂存区、本地库。
		* 工作区就是平时我们进行代码书写的区域。暂存区的文件是我们从工作区通过 > git add 命令提交上来的，暂存区的文件可以使用 > git commit 命令提交到本地库，也可以进行撤回操作。本地库就是我们本地文件的一个历史版本库，对这个目录中的所有文件进行的更改操作都会被git记录下来。

2 git与代码托管中心
	
		* 局域网环境下我们可以搭建Gitlab进行代码托管。
		* 外网环境下，可以使用GitHub或者码云进行代码托管。
		* 命令：

3 设置签名
	
		* 形式 用户名：tom
			  Email地址：可以写真实存在的email也可写一个不存在的email
		* 作用 用于区分不同开发人员的身份
		* 命令
			- 项目级别/仓库级别：仅在当前本地库范围内有效
				* git config user.name USER_NAME
				* git config user.email EMAIL
			- 系统用户级别：登陆当前操作系统的用户范围
				* git config --global user.name USER_NAME
				* git config --global user.email EMAIL
				
			- 级别优先级
				- 就近原则：项目级别优先于系统用户级别，二者都有时采用项目级别的签名
				- 如果只有系统用户级别的签名，就以系统用户级别的签名为准
				- 二者都没有是不允许的


4 常用命令
	
		* 使用 > git status 查看工作区  暂存区的状态
		* 使用 > git add <file> 将文件由工作区提交到暂存区
		* 使用 > git rm --cached <file> 可以撤销将文件提交到暂存区的操作
		* 使用 > git commit <file> 可以将暂存区的文件提交到本地库，提交成功后会出现版本介绍的编辑页面
			- 使用 > git commit -m "版本介绍" <file> 可以直接在命令行进行版本介绍的编辑
		* 使用 > git log 查看版本历史纪录
			- 在每条版本历史纪录中 commit后面的一行四十位的字符串是这个版本的哈希值（可看作版本号）
			- 有一条纪录上会有（HEAD->master）指示，这是表示git的版本指针，指示当前的版本。
			- > git log --pretty=oneline  可以保证每条记录在同一行显示
			- > git --oneline  相对更加简洁
			- > git reflog  其中的 HEAD@{移动到当前版本需要多少步}
			- 基于索引值进行版本穿梭操作 > git reset --hard <索引值>
		* 多屏显示  空格向下翻页  B向上分页 Q退出
 

