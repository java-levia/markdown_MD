#Eclipse远程DeBug#

1.linux服务器上的tomcat
	
	bin/startup.sh开始处增加如下内容
	declare -x CATALINA_OPTS="-server -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8788"
	注意：以上内容必须在同一行  不可换行。