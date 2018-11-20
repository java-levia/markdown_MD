#Linux#
##Ubuntu14.04安装及配置mysql5.7.19##
通过uname -a或lsb_release -a 查看版本信息 ，确定linux的发行版是debian还是ubuntu。
root@suepc:/usr/local# uname -a
Linux suepc 3.13.0-129-generic #178-Ubuntu SMP Fri Aug 11 12:48:20 UTC 2017 x86_64 x86_64 x86_64 GNU/Linux
根据对应的版本下载mysql
mysql的下载地址：https://dev.mysql.com/downloads/file/?id=471390
本文以 mysql-server_5.7.19-1ubuntu14.04_amd64.deb-bundle.tar 为例。

1. ）更新源

sudo apt-get update

sudo apt-get upgrade

2.）将mysql包放在usr的local目录下

cd  /usr/local/

sudo  cp  ~/Download/mysql-server_5.7.19-1ubuntu14.04_amd64.deb-bundle.tar .

3.）切换到root用户下,并输入密码

su root

******

4.）在当前目录新建一个将mysql包并将mysql-server_5.7.19-1ubuntu14.04_amd64.deb-bundle.tar解压

mkdir mysql

tar -xvf  mysql-server_5.7.19-1ubuntu14.04_amd64.deb-bundle.tar

5.）解压好后一共有11个压缩包如下：

 

libmysqlclient20_5.7.19-1ubuntu14.04_amd64.deb    mysql-common_5.7.19-1ubuntu14.04_amd64.deb            mysql-community-test_5.7.19-1ubuntu14.04_amd64.deb

libmysqlclient-dev_5.7.19-1ubuntu14.04_amd64.deb  mysql-community-client_5.7.19-1ubuntu14.04_amd64.deb  mysql-server_5.7.19-1ubuntu14.04_amd64.deb

libmysqld-dev_5.7.19-1ubuntu14.04_amd64.deb       mysql-community-server_5.7.19-1ubuntu14.04_amd64.deb  mysql-testsuite_5.7.19-1ubuntu14.04_amd64.deb

mysql-client_5.7.19-1ubuntu14.04_amd64.deb        mysql-community-source_5.7.19-1ubuntu14.04_amd64.deb

6.）因为包与包中间存在依赖关系，这里安装有个先后顺序。这里用sudo dpkg -i [包名]命令逐个安装。

我的安装的顺序是：

1.mysql-common_5.7.19-1ubuntu14.04_amd64.deb 
2.libmysqlclient20_5.7.19-1ubuntu14.04_amd64.deb 
3.libmysqlclient-dev_5.7.19-1ubuntu14.04_amd64.deb 
4.libmysqld-dev_5.7.19-1ubuntu14.04_amd64.deb 

7.）需要再安装一个依赖包叫libaio1，命令为 
sudo apt-get install libaio1 
8.）继续： 
5.mysql-community-client_5.7.19-1ubuntu14.04_amd64.deb 
6.mysql-client_5.7.19-1ubuntu14.04_amd64.deb 
7.mysql-community-source_5.7.19-1ubuntu14.04_amd64.deb 
9.）这里需要再安装一个依赖包叫libmecab2,安装好后，继续安装最后一个： 
8.mysql-community-server_5.7.19-1ubuntu14.04_amd64.deb 

以上操作在root用户下进行，安装完成后MYSQL是默认启动的 安装过程中需要设置数据库密码。

到这里，所有的已经安装完毕。输入mysql  -u root -p可以登陆数据库了。

10.停止mysql

service mysql stop

11.启动mysql

service mysql start

12.进入mysql登陆数据库

mysql  -u root -p

QA：远程工具报10061错误

默认情况下MySQL不允许这些工具远程连接的，你可以找到/etc/mysql/my.cnf这个文件，将bind-address=127.0.0.1改为 bind-address=0.0.0.0 或将其注释掉

执行GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '1qaz2wsx' WITH GRANT OPTION;

重启mysql即可。

 

---------------------------------------------------------------------------------------------------------

设置数据库默认字符集：
vi /etc/mysql/my.cnf
增加：
[client]
default-character-set=utf8

[mysqld]
character-set-server=utf8