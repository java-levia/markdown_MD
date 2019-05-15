Docker容器哦技术

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
     1. 运行交互式容器（docker run -i -t IMAGE /bin/bash）然后使用ctrl+p和 ctrl+q来退出交互式容器的bash，这样容器就会在后台运行。 如果需要再进入到运行中的容器，使用命令 docker attach  [CONTAINER ID/NAMES]进入
     2. 使用run命令启动守护式容器：docker run -d 镜像名 [command][arg]

