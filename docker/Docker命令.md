# Docker命令

## 镜像命令

```bash
$ docker rmi -f 容器ID #删除指定镜像
$ docker rmi -f 容器ID 容器ID 容器ID #删除多个镜像
$ docker rmi -f ${docker images -aq} #删除全部镜像
```



## 容器命令

### 运行容器

```bash
#运行容器
$ docker run [可选参数] image
	#参数说明
	/***********容器属性************/
	--name="NAME" 	#容器名字
	
	/***********运行方式***********/
	-d            	#后台方式运行
	-it 			#使用交互方式运行，进入容器查看内容
	
	/***********容器端口映射***********/
    -p  			#指定容器的端口 -p 8080:8080(主机端口：容器端口)
    	-p 主机端口:容器端口 #指定将容器端口映射到主机的某个端口
    	-p 容器端口  #指定容器的端口(在主机上无映射)
    -P				#随机指定端口(大写P)
    
    #启动并进入容器
    $ docker run -it --name='test-centos' centos /bin/bash
    
    #退出容器(停止容器)
    $ exit
    
    #退出容器(不停止容器)
	$ Ctrl + P + Q
```



### 启动和停止容器

```bash
#启动和停止容器
    #启动容器
    $docker start [容器ID]
    #重启容器
    $docker restart [容器ID]
    #停止容器
    $docker stop [容器ID]
    #强制停止容器
    $docker kill [容器ID]
```

### 查看容器

```bash
#查看容器
$ docker ps [可选参数]
	-a 		#查看运行过的容器 + 历史运行的容器
	-n=? 	#查看最近创建的容器(?数字占位符，表示查看多少个)
	-q		#只显示容器的编号
	
	
    #查看正在运行的容器
    $ docker ps
    
    #查看运行过的容器 + 历史运行的容器
    $ docker ps -a
    
    #查看最近创建的容器(?数字占位符，表示查看多少个)
    $ docker ps -n=?
    
```

### 删除容器

```bash
#删除容器
$docker rm [容器ID]
	-f #强制删除(可删除正在运行的容器)
	
	#删除全部容器
	$docker rm $(docker ps -aq)
	#删除全部容器(管道符| “管道“|”可将命令的结果输出给另一个命令作为输入之用”)
	$docker ps -a -q|xargs docker rm
```



## 常用的其他命令

```bash
#命令 docker run -d [镜像名]
$docker run -d centos
#运行以上命令之后，发现 $docker ps 命令看不到启动的容器，通过 $docker ps -a 查看，发现刚启动的这个容器被停止了

#常见的坑：docker 容器使用后台运行就必须要有一个前台进程，docker发现没有应用，立即就会停止容器

  --------------------------------------------------------------------------- 
  
#查看日志
$docker logs [可选参数]
	-tf 			#显示日志带上时间戳
	--tail number 	#要显示的日志行数

#查看日志
$docker logs -tf --tail centos [容器ID]

  --------------------------------------------------------------------------- 
#查看容器中的进程信息
$docker top [容器ID]

  --------------------------------------------------------------------------- 
#查看容器的元数据
$docker inspect [容器ID]

```

#### 进入当前正在运行的容器

```bash
#容器通常都是以后台方式运行，修改容器的配置需要进入容器才能进行
# 方式一[打开新的终端，进入容器内部]
$ docker exec -it [容器ID] /bin/bash
#方式二[进入容器中正在运行的命令行]
$ docker attach [容器ID]
```

## 从容器内拷贝文件到主机

```bash
$ docker cp 容器id:容器内路径 主机目的地路径

#拷贝是一个手动的过程，对于有同步需求的数据，我们通过 -v 卷技术实现自动同步
```

