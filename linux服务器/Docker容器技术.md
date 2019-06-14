Docker容器技术

1. Docker的基本组成
   * Docker Client客户端
   * Docker Deamon守护进程
   * Doker Image 镜像
     * 镜像是容器的基石，容器基于镜像启动和运行。镜像相当于容器的源代码，保存了用于容器启动的各种条件
     * Docker是一个层叠的**只读**文件系统
   * Docker Container 容器
   * Docker Register 仓库

2. Docker容器相关技术简介

   * Docker依赖的Linux内核特性

     * NameSpaces 命名空间

       

     * Control groups (cgroups) 控制组

3. Docker守护式容器
   * 守护式容器的特点：
     * 能够长期运行
     * 没有交互式会话
     * 适合运行应用程序和服务
   * 以守护形式运行容器：
     1. 以交互式容器的方式运行守护式容器
     
        * 启动  docker run -i -t IMAGE /bin/bash   
        * 然后使用ctrl+p和 ctrl+q来退出交互式容器的bash，这样容器就会在后台运行。 如果需要再进入到运行中的容器，使用命令 docker attach  [CONTAINER ID/NAMES]进入
     
     2. 使用run命令启动守护式容器：docker run -d 镜像名 command arg, -d是告诉容器以后台的方式启动容器
     
     3. 使用容器的日志命令查看容器内部的运行情况
     
        * docker logs -f -t --tail 容器名
     
          -f --follows=true|false 默认为false
     
          -t  --timestamps=true|false 默认为false
     
          --tail ='all'
     
     4. 查看容器内进程：
       
        * 使用 docker top 容器名 
        
     5. 在运行中的容器内启动新进程
       
        * docker exec -d -i -t 容器名 command arg
        
     6. 停止守护式容器
        * docker stop 容器名：发送一个stop命令等待容器正常停止
        * docker kill 容器名：强制关闭容器
        
     7. 设置容器的端口映射
        *  run -P -p
        * -P --publish-all =true|false 默认为false（docker run -P -i -t centos /bin/bash）,使用-P将为容器暴露的所有端口进行映射
        * -p，--publish=【】  containerPort     (docker run -p 80 -i -t centos /bin/bash),使用小写的p可以指定映射哪些端口
        * 容器映射端口的方式有四种格式
          * containerPort        (docker run -p 80 -i -t centos /bin/bash)
          * hostPort:containerPort    (docker run -p 8082:80 -i -t centos /bin/bash)
          * ip:containerPort    (docker run -p 0.0.0.0:80 -i -t centos /bin/bash)
          * ip:hostPort:containerPort    (docker run -p 0.0.0.0:8082:80 -i -t centos /bin/bash)
        
     8. 遇上宿主机正常联网而容器无法联网的情况的解决办法：
     
        * vi /usr/lib/sysctl.d/00-system.conf
        * 添加 net.ipv4.ip_forward=1
     
     9. 查看docker容器的ip地址：docker inspect 容器名
     
        * 容器内的程序可以通过宿主机的ip地址进行访问，也可以通过容器本身的ip地址进行访问
        * 使用docker ps命令查看进程信息时，PORTS的信息0.0.0.0:32770->80/tcp  表示的是将容器的80端口映射到宿主机的32770端口上
     
     10. 当我们重启容器后，需要另外再重启容器内的服务，通过 docker exec 进程名  服务名（docker  exec  webServer  nginx）



搭建自己的腾讯云服务器环境：

1. pull一个mysql镜像     docker pull mysql
2. 