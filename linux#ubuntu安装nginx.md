#Linux#
##Ubuntu安装nginx##
1.先检查升级

sudo apt-get update
sudo apt-get upgrade

    # sudo apt-get update
    # sudo apt-get upgrade

2.安装nginx需要的依赖包

    # sudo apt-get install libpcre3 libpcre3-dev libpcrecpp0 libssl-dev zlib1g-dev

3.下载nginx

	# cd /home

	# wget http://nginx.org/download/nginx-1.10.2.tar.gz

4.解压nginx

    # tar -zxvf nginx-1.10.2.tar.gz

5.编译，安装nginx

	# cd nginx-1.10.2.tar.gz

	# ./configure --prefix=/usr/local/nginx --with-http_stub_status_module --with-http_ssl_module

	# make

	# make install


6.查看nginx版本

	# /usr/local/nginx/sbin/nginx -v


7.启动nginx

	# /usr/local/nginx/sbin/nginx


注意：此步，有可能nginx默认的80端口被apache2服务占用，导致无法启动nginx。可以修改nginx的服务端口或者apache2的服务端口。

8.修改apache2端口

	# vi /etc/apache2/ports.conf


apache ports
修改第5行 为Listen 88
重启apache2服务

	# service apache2 restart


再次启动nginx

	# /usr/local/nginx/sbin/nginx


9 springBoot项目使用nginx服务器配置ssl证书

server {
        listen 443;
        server_name yinghuaart.com;
        ssl on;     
        ssl_certificate   ssl/cert-1540869145533_yinghuaart.com.pem;
        ssl_certificate_key  ssl/cert-1540869145533_yinghuaart.com.key;
        ssl_session_timeout 5m;
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
        ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
        ssl_prefer_server_ciphers on;
        location / {
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; 
		proxy_set_header X-Forwarded-Proto $scheme; 
		proxy_set_header X-Forwarded-Port $server_port;
    
            proxy_pass http://www.yinghuaart.com:9999;
            proxy_redirect off;
        }
}

	如果出现从一个页面跳转到另一个页面，请求路径由https变成http的情况，可能是：
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; 
		proxy_set_header X-Forwarded-Proto $scheme; 
		proxy_set_header X-Forwarded-Port $server_port;
	没有配置好的原因

	出现配置好ssl证书后，进行页面跳转时，从nginx的代理端口跳转到了springboot项目原来的端口，这被称为页面跳转时的端口丢失，遇到这种情况，可以在nginx.conf配置文件中配置ssl证书的server中添加proxy_set_header Host $host:$server_port;  可以保证端口在页面跳转时不发生丢失。

	nginx反向代理时出现https页面跳转时变成http的情况（400 Bad Request: The plain HTTP request was sent to HTTPS port），可通过在
		nginx代理中配置proxy_redirect（视情况决定配置在server中还是location中）
		proxy_redirect http:// $scheme://;
		以上指令会将后端响应header location内容中的http://替换成用户端协议https://。