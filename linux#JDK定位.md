#linux#
##JDK定位##
	使用“echo  $JAVA_HOME”命令，能定位JDK安装路径的前提是配置了环境变量JAVA_HOME，否则根本定位不到JDK的安装路径。

        使用“which  java”命令，也是定位不到安装路径的，which  java定位到的是java程序的执行路径（使用“whereis  java”命令也是如此，它本身不能定位到安装路径）。

        那如何定位到JAVA的安装路径呢？如下所示：

    [root@localhost ~]# java  -version
        java version "1.7.0_65"
        OpenJDK Runtime Environment (rhel-2.5.1.2.el6_5-x86_64 u65-b17)
        OpenJDK 64-Bit Server VM (build 24.65-b04, mixed mode)
     
    [root@localhost ~]# which  java
        /usr/bin/java
     
    [root@localhost ~]# ls  -lrt  /usr/bin/java
        lrwxrwxrwx. 1 root root 22 Aug 17 15:12 /usr/bin/java -> /etc/alternatives/java
     
    [root@localhost ~]# ls  -lrt  /etc/alternatives/java
        lrwxrwxrwx. 1 root root 46 Aug 17 15:12 /etc/alternatives/java -> /usr/lib/jvm/jre-1.7.0-openjdk.x86_64/bin/java



        若JDK是非源码安装的话，也可以使用“rpm  –ql  packagename”命令查看JDK的安装路径。如下：

    [root@localhost ~]# rpm  -qa | grep java
        tzdata-java-2014g-1.el6.noarch
        java-1.6.0-openjdk-1.6.0.0-11.1.13.4.el6.x86_64
        java-1.7.0-openjdk-1.7.0.65-2.5.1.2.el6_5.x86_64
     
    [root@localhost ~]# rpm  -ql  java-1.7.0-openjdk-1.7.0.65-2.5.1.2.el6_5.x86_64 | more
        /etc/.java
        /etc/.java/.systemPrefs
        /usr/lib/jvm-exports/java-1.7.0-openjdk-1.7.0.65.x86_64
        /usr/lib/jvm-exports/java-1.7.0-openjdk-1.7.0.65.x86_64/jaas-1.7.0.65.jar
        /usr/lib/jvm-exports/java-1.7.0-openjdk-1.7.0.65.x86_64/jaas-1.7.0.jar
        /usr/lib/jvm-exports/java-1.7.0-openjdk-1.7.0.65.x86_64/jaas.jar
        /usr/lib/jvm-exports/jre-1.7.0-openjdk.x86_64
        /usr/lib/jvm-private/java-1.7.0-openjdk.x86_64/jce
        /usr/lib/jvm-private/java-1.7.0-openjdk.x86_64/jce/vanilla
        /usr/lib/jvm/java-1.7.0-openjdk-1.7.0.65.x86_64
        /usr/lib/jvm/java-1.7.0-openjdk-1.7.0.65.x86_64/ASSEMBLY_EXCEPTION
        /usr/lib/jvm/java-1.7.0-openjdk-1.7.0.65.x86_64/LICENSE
        --More--