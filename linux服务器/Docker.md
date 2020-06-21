# Docker

1. ## Docker安装

   - ### Docker中的相关概念

     **镜像（image）:**

     ​	Docker镜像就好比是一个模板，可以通过这个模板去创建一个容器服务， 例如：tomcat镜像===>run===>tomcat01容器（由这个run起来的容器提供服务），通过这个镜像可以创建多个容器（最终服务运行或者项目运行就是在容器中的）。 

     **容器（container）:**

     ​	Docker利用容器技术，独立运行一个或者一组应用，容器是通过镜像创建的。 

     ​	容器的最基本命令有：启动、停止、删除等

     ​	目前可以把这个容器理解为一个建议的linux系统

     **仓库（repository）:**

     ​	仓库就是存放镜像的地方！

     ​	仓库分为共有仓库和私有仓库！ 

     ​	Docker环境中默认连接的是国外的仓库，国内阿里云和华为云都有容器服务器，由于国外服务器下载镜像速度很慢，一般都需要连接国内的镜像从而达到加速的目的。

     ### 安装Docker

     ​	

     ```shell
     #系统内核是3.10以上的
     [root@workstation ~]# uname -r
     3.10.0-1062.9.1.el7.x86_64
     
     #系统版本
     [root@workstation ~]# cat /etc/os-release
     NAME="CentOS Linux"
     VERSION="7 (Core)"
     ID="centos"
     ID_LIKE="rhel fedora"
     VERSION_ID="7"
     PRETTY_NAME="CentOS Linux 7 (Core)"
     ANSI_COLOR="0;31"
     CPE_NAME="cpe:/o:centos:centos:7"
     HOME_URL="https://www.centos.org/"
     BUG_REPORT_URL="https://bugs.centos.org/"
     
     CENTOS_MANTISBT_PROJECT="CentOS-7"
     CENTOS_MANTISBT_PROJECT_VERSION="7"
     REDHAT_SUPPORT_PRODUCT="centos"
     REDHAT_SUPPORT_PRODUCT_VERSION="7"
     
     ```

     #### 安装

     ```shell
     #卸载旧的Docker版本
     $ sudo yum remove docker \
                       docker-client \
                       docker-client-latest \
                       docker-common \
                       docker-latest \
                       docker-latest-logrotate \
                       docker-logrotate \
                       docker-engine
                       
     #安装需要的环境
     $ yum install -y yum-utils
     
     #设置镜像的仓库
     $ sudo yum-config-manager \
         --add-repo \
         http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo #默认是从国外的镜像仓库下载镜像（阿里云自用仓库地址  registry.cn-hangzhou.aliyuncs.com/levia_repository/levia_custom）
         
     #安装最新版docker引擎 docker-ce社区版。ee企业版
     $ sudo yum install docker-ce docker-ce-cli containerd.io
     
     #启动Docker
     $ systemctl start docker
     #使用docker version查看是否安装成功
     $ docker version
     
     #使用docker run hellow-word 测试（所有的命令都是使用docker run 启动的）
     $ docker run hellow-world
     
     
     #修改默认的镜像仓库地址
     #可以通过修改daemon配置文件/etc/docker/daemon.json来使用加速器
     sudo mkdir -p /etc/docker
     sudo tee /etc/docker/daemon.json <<-'EOF'
     {
       "registry-mirrors": ["https://92qim0wo.mirror.aliyuncs.com"]
     }
     EOF
     sudo systemctl daemon-reload
     sudo systemctl restart docker
     
     #查看已经下载的镜像
     $ docker images
     
     
     #卸载docker
     #卸载docker的依赖
     $ sudo yum remove docker-ce docker-ce-cli containerd.io
     #删除资源
     $ sudo rm -rf /var/lib/docker
     ```

     ![1591544428666](C:\Users\Admin\AppData\Roaming\Typora\typora-user-images\1591544428666.png)

     #### Docker是怎么工作的
     
     ​	Docker是一个Client-Server结构的系统，Docker的守护进程运行在主机上。通过Socket从客户端访问!
     
     ​	DockerServer接收到DockerClient的指令，就会执行这个命令！
     
     ![1591544390513](C:\Users\Admin\AppData\Roaming\Typora\typora-user-images\1591544390513.png)
     
     #### Docker为什么比VM快
     
     1.Docker有着比虚拟机更少的抽象层
     
     2.docker利用的是宿主机的内核，VM需要使用Guest OS
     
     所以说新建一个容器的时候，docker不需要像虚拟机一样重新加载一个操作系统内核，避免了引导。虚拟机是加载Guest OS,分钟级别的，而DOCKER是利用宿主机的操作系统，省略了这个复杂的过程。

2. ## Docker命令

   - ### 帮助命令

     ```shell
     docker version #显示docker的版本信息
     docker info #显示docker的系统信息
     docker 命令 --help #万能命令
     
     --官方文档地址： https://docs.docker.com/reference/
     ```

     

   - ### 镜像命令

     ```shell
     #docker images  查看所有本地主机上的镜像
     [root@workstation ~]# docker images
     REPOSITORY          TAG                 IMAGE ID            CREATED           SIZE
     hello-world         latest              bf756fb1ae65        5 months ago    13.3KB 
     
     #解释
     REPOSITORY 镜像的仓库源
     TAG 镜像的标签
     IMAGE ID 镜像的ID
     CREATED 镜像的创建时间
     SIZE  镜像的大小
     
     #可选项
     Options:
       -a, --all             显示所有镜像
       -q, --quiet           只显示镜像的ID
     
     
     
     #docker search 搜索镜像
     AME                              DESCRIPTION                                     STARS               OFFICIAL            AUTOMATED
     mysql                             MySQL is a widely used, open-source relation…   9626                [OK]                
     #可选项，可通过收藏来过滤
     --filter=STARS
     
     
     #docker pull 镜像名[:tag] 下载镜像
     [root@workstation ~]# docker pull mysql
     Using default tag: latest #pull的时候不写TAG ,则默认就是下载的最新版latest
     latest: Pulling from library/mysql
     8559a31e96f4: Pull complete #分层下载  docker image的核心，联合文件系统
     d51ce1c2e575: Pull complete 
     c2344adc4858: Pull complete 
     fcf3ceff18fc: Pull complete 
     16da0c38dc5b: Pull complete 
     b905d1797e97: Pull complete 
     4b50d1c6b05c: Pull complete 
     c75914a65ca2: Pull complete 
     1ae8042bdd09: Pull complete 
     453ac13c00a3: Pull complete 
     9e680cd72f08: Pull complete 
     a6b5dc864b6c: Pull complete 
     Digest: sha256:8b7b328a7ff6de46ef96bcf83af048cb00a1c86282bfca0cb119c84568b4caf6 #签名
     Status: Downloaded newer image for mysql:latest
     docker.io/library/mysql:latest
     
     #docker pull mysql 等价于 docker pull docker.io/library/mysql:latest
     
     #指定版本下载
     #docker pull mysql:5.7
     
     #删除镜像  docker rmi -f [镜像ID]
     #删除多个镜像 docker rmi -f [镜像ID] [镜像ID] [镜像ID] [镜像ID]
     #删除所有镜像 docker rmi -f $(docker image -aq)
     ```

     

   - ### 容器命令

     ```shell
     #说明  我们有了镜像才能创建容器
     ```

      ```shell
     docker pull centos
      ```

     #### 新建容器并启动

     ```shell
     docker run [可选参数] image
     #参数说明
     --name="Name"  容器名字 tomcat01 tomcat02 用于区分不同的容器
     -d             后台方式运行容器
     -it            使用交互方式运行， 进入容器查看内容
     -p             指定容器的端口 -p 8080:8080
     		-p ip:主机端口：容器端口
     		-p 主机端口：容器端口（常用）
     		-p 容器端口
     		容器端口
     -P            随机指定端口		
     
     #测试，启动并进入容器
     [root@workstation ~]# docker images
     REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
     centos              latest              831691599b88        4 days ago          215MB
     logstash            7.6.1               d6d66afe6805        3 months ago        813MB
     hello-world         latest              bf756fb1ae65        5 months ago        13.3kB
     [root@workstation ~]# docker run --name="centos-test" -it centos /bin/sh
     sh-4.4# 
     sh-4.4# 
     sh-4.4# 
     sh-4.4# ls
     bin  etc   lib	  lost+found  mnt  proc  run   srv  tmp  var
     dev  home  lib64  media       opt  root  sbin  sys  usr
     sh-4.4#exit
     exit
     [root@workstation ~]# 
     
     #查看正在运行的容器
     docker ps
     
     [root@workstation ~]# docker ps
     CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                                            NAMES
     33acee280636        logstash:7.6.1      "/usr/local/bin/dock…"   2 days ago          Up 45 hours         0.0.0.0:5044->5044/tcp, 0.0.0.0:9600->9600/tcp   sleepy_hertz
     
     #查看运行过的容器
     docker ps -a
     
     [root@workstation ~]# docker ps -a
     CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                      PORTS                                            NAMES
     aab34215ced2        centos              "/bin/sh"                16 minutes ago      Exited (0) 13 minutes ago                                                    centos-test
     33acee280636        logstash:7.6.1      "/usr/local/bin/dock…"   2 days ago          Up 45 hours                 0.0.0.0:5044->5044/tcp, 0.0.0.0:9600->9600/tcp   sleepy_hertz
     8746407b1b68        hello-world         "/hello"                 13 days ago         Exited (0) 13 days ago      
     
     ```

     #### 列出所有运行的容器

     ```shell
     #docker ps命令
     docker ps #列出正在运行的容器
     docker ps -a #列出所有容器（包括当前正在运行的和曾经运行过的）
     docker ps -n=? #显示最近创建的容器
     docekr ps -q #只显示容器的编号
     ```

     #### 退出容器

     ```shell
     
     ```

     

     #### 删除容器

     ```shell
     
     ```

     

   - ### 操作命令

   - ### 

3. ## Docker镜像

4. ## 容器数据卷

5. ## DockerFile 

6. ## Doker网络原理

7. ## IDEA整合Docker 

8. ## Docker Compose 

9. ## Docker Swarm 

10. ## CI/CD Jenkins