RabbitMQ安装与配置

1. 由于RabbitMQ的编程语言是erlang，在安装之前需要先安装好erlang的环境

   ```
   # wget http://dl.fedoraproject.org/pub/epel/6/x86_64/epel-release-6-8.noarch.rpm
   # rpm -ivh epel-release-6-8.noarch.rpm
   # yum install erlang
   ```

   

2. 安装RabbitMQ

   ```
   # wget https://dl.bintray.com/rabbitmq/all/rabbitmq-server/3.7.7/rabbitmq-server-3.7.7-1.el6.noarch.rpm
   # rpm -ivh rabbitmq-server-3.7.7-1.el6.noarch.rpm
   ```

3. RabbitMQ的启动与停止

   ```
   # 启动 
   service rabbitmq-server start 
   
   # 停止 
   service rabbitmq-server stop 
   
   # 重启 
   service rabbitmq-server restart 
   
   # 开机自启 
   chkconfig rabbitmq-server on 
   
   # 启用监控插件 
   rabbitmq-plugins enable rabbitmq_management 
   
   # 修改防火墙 
   vim /etc/sysconfig/iptables
   
   # 开放端口15672 和 5672 
   -A INPUT -m state --state NEW -m tcp -p tcp --dport 15672 -j ACCEPT 
   -A INPUT -m state --state NEW -m tcp -p tcp --dport 5672 -j ACCEPT 
   
   # 重启防火墙 
   service iptables restart
   
   ```

4. 添加用户

   ```
   # 添加用户admin,密码为123456 
   rabbitmqctl add_user admin 123456 
   
   # 给用户添加管理员角色 
   rabbitmqctl set_user_tags admin administrator 
   
   # 查看所有用户 
   rabbitmqctl list_users 
   
   # 查看admin所有权限 
   rabbitmqctl list_user_permissions admin 
   
   # 清除admin权限[指定权限] 
   rabbitmqctl clear_permissions [-p VHostPath] admin 
   
   # 查看所有权限[-p test_vhosts] 或指定权限 
   rabbitmqctl list_permissions [-p VHostPath] 
   
   # 设置admin在VHostPath下的权限 
   rabbitmqctl set_permissions -p VHostPath admin ConfP WriteP ReadP 
   
   # 删除admin用户 
   rabbitmqctl delete_user admin 
   
   # 修改admin的密码为123456 
   rabbitmqctl change_password admin 123456 
   
   # 启用监控插件 
   rabbitmq-plugins enable rabbitmq_management 
   
   # 关闭监控插件 
   rabbitmq-plugins disable rabbitmq_management
   
   #访问管理后台地址
   http://192.168.0.28:15672
   ```

   