MongoDB

Linux CentOS6.9  安装MongoDB3.6

1. 创建.repo文件  直接安装MongoDB3.6企业版，一定要选择偶数版本，奇数版如3.5为开发版，不适合生产部署；

```
vim /etc/yum.repos.d/mongodb-enterprise.repo 

[mongodb-enterprise] 
name=MongoDB Enterprise Repository baseurl=https://repo.mongodb.com/yum/redhat/$releasever/mongodb-enterprise/3.6/$basearch/ gpgcheck=1 enabled=1 gpgkey=https://www.mongodb.org/static/pgp/server-3.6.asc

```

2. yum安装

```
# 安装当前3.6.*最新版 
sudo yum install -y mongodb-enterprise 

# 或者指定3.6.*的一个版本 
sudo yum install -y mongodb-enterprise-3.6.4 mongodb-enterprise-server-3.6.4 mongodb-enterprise-shell-3.6.4 mongodb-enterprise-mongos-3.6.4 mongodb-enterprise-tools-3.6.4 

# 固定MongoDB版本 防止yum意外升级 
exclude=mongodb-enterprise,mongodb-enterprise-server,mongodb-enterprise-shell,mongodb-enterprise-mongos,mongodb-enterprise-tools

```

3. 使用

   MongoDB服务启动停止 设置为开机自启 配置文件 等设置

```
sudo service mongod start # 启动服务 
sudo service mongod stop # 停止服务 
sudo service mongod restart # 重启服务 
sudo chkconfig mongod on # 开机自动启动服务 
cat /var/run/mongodb/mongod.pid # 默认pid位置 
cat /var/log/mongodb/mongod.log # 默认log位置 

# /var/lib/mongo                   # 数据位置 

cat /etc/mongod.conf # MongoDB配置文件 可以修改里面的log pid 位置 数据库位置 端口等配置信息

mongo --host 127.0.0.1:27017  #使用

# Control+C 退出

远程连接不上MongoDB 可以尝试将bindIp设置成 0.0.0.0 （不要注释掉这一行  直接改成0.0.0.0）
```

