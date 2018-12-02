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
			- > git log --oneline  相对更加简洁
			- > git reflog  其中的 HEAD@{移动到当前版本需要多少步}
			
		* 基于索引值进行版本穿梭操作 > git reset --hard <索引值>
			- > git reset --hard HEAD^^ HEAD后面跟几个^符号表示后退几个版本  只能后退
			- > git reset --hard HEAD~3 ~符号后面跟上数字几就表示后退几个版本 只能后退J
		* 多屏显示  空格向下翻页  B向上分页 Q退出
		* 使用 > rm <file> 可以删除文件，然后相继使用 > git add  > git commit 可以将文件删除的操作提交到本地库

5 删除文件找回
		* 删除的文件能找回的前提是，文件存在时的状态提交到了本地库。
		* 添加到暂存区(尚未提交到本地库)的删除文件找回
			- 使用 > git reset --hard HEAD （因为删除操作尚未提交到本地库，而 --hard 关键字表示的是 在本地库移动指针，会对工作区和暂存区都进行刷新）
		* 找回已提交到本地库的删除文件
			- 操作 git reset --hard <指针位置>
		
6 查看文件的更改
		* 使用 > git diff <file> 表示将工作区的文件和暂存区的文件进行比较
		* 使用 > git diff <本地库历史版本> <file> 表示和本地库的某个文件文件进行比较


7 分支
	什么是分支 
		* 在版本控制过程中，使用多条线同时推进多个任务。
		
	分支的好处
		* 同时并行推进多个功能的开发，提高开发效率
		* 各个分支在开发过程中，如果一个分支开发失败，不会对其他分支有任何影响。失败的分支删除重新开始即可。

	分支的操作
		* 查看分支 > git branch -v
		* 创建分支 > git branch <分支名 >   分支的命名也有一定的规范
		* 切换分支 > git checkout <分支名>   
		* 合并分支 
			- 首先切换到需要增加新内容的分支上（比如需要在主分支上合并其他分支，就先切换到主分支上）
			- 使用 > git merge <有新内容的分支名>

	分支冲突
		在合并分支的时候，如果需要增加新内容的分支和有新内容的分支都在同一个位置做了更改，这样在合并分支的时候回产生分支冲突导致分支合并失败。
			- 在产生分支冲突时，本地库中的冲突文件中会有如下格式的代码行
			-   <<<<<<<<<<HEAD
			-   hhhhhhhhhh edit by <分支名>
			-   ===================
			-   hhhhhhhhhh  edit by <另一个分支名>
			-   >>>>>>>>>>  <另一个分支名>
			

8 Eclipse集成Git
	
	忽略的文件
		- Eclipse中有部分文件是编辑器自动生成的，是用来管理我们写的代码，不需要提交到本地库,包括：
			- .classpath
			- .settings
			- .project
			- target文件夹下的所有

	设置忽略文件
			- https://github.com/github/gitignore
			- 在用户文件夹下创建java.gitignore文件，复制github提供的java.gitignore文件并添加eclipse中需要忽略的文件到java.gitignore中。
			- 在用户文件夹下的.gitconfig文件中[core]下添加excludesfile = c:/Users/mi/java.gitignore 
	
