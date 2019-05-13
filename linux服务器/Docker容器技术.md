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