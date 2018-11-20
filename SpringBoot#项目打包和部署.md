#SpringBoot#
##SpringBoot项目打包和部署#

一、使用内置的tomcat，通过java -jar的方式启动

 

比如：java -jar bms.jar  

 

但是这种启动方式 一旦关闭控制台 或者crtl+c 退出 此时应用就关闭了

 

所以我们需要换种方式

 

springboot中的maven插件，提供了一种很方便的类似于shell的开启、关闭、重启服务的操作。而且这种启动方式是让项目在后台运行的，关闭shell也不会使项目停止运行

 

 

1、首先，项目的pom添加下面的插件，然后maven install

     <build>

          <plugins>

              <plugin>

                   <groupId>org.springframework.boot</groupId>

                   <artifactId>spring-boot-maven-plugin</artifactId>

                   <configuration>

                        <executable>true</executable>

                   </configuration>

              </plugin>

          </plugins>

     </build>

 

 

2、将打包好的springboot jar包放入到服务器上，然后执行下面的命令

sudo ln -s  /usr/local/server/bms.jar  /etc/init.d/bms

 

 

 

 

3、上述命令执行完全之后，通过下面的命令就可以启动、关闭、重启项目了。

/etc/init.d/bms start  

 

/etc/init.d/bms stop

 

/etc/init.d/bms restart  

    

 

 

当然，有些人启动的时候，可能会报下面的一个错误，这是权限不足

-bash: /etc/init.d/bms: Permission denied

 

执行下面命令就可解决问题

cd /etc/init.d/

chmod a+x bms  或 chmod 777 bms


二、使用war包方式，通过tomcat启动

 

1、修改打包方式

<packaging>war</packaging>

 

2、移除springboot内置tomcat，添加测试tomcat依赖。。修改pom.xml文件

       <dependency>

              <groupId>org.springframework.boot</groupId>

               <artifactId>spring-boot-starter-web</artifactId>

              <!-- 打包的时候以war包形式，这里要把springboot集成的tomcat去除 -->

              <exclusions>

                   <exclusion>

                        <groupId>org.springframework.boot</groupId>

                        <artifactId>spring-boot-starter-tomcat</artifactId>

                   </exclusion>

              </exclusions>

          </dependency>

          <!-- 项目测试需要 -->

          <dependency>

              <groupId>org.springframework.boot</groupId>

               <artifactId>spring-boot-starter-tomcat</artifactId>

              <scope>provided</scope>

          </dependency>

 

3、修改启动类，

 

在application的同目录下，添加一个新的类，继承SpringBootServletInitializer并重写其configure方法

代码如下

/**

 * 修改启动类，继承 SpringBootServletInitializer 并重写 configure 方法

 */

publicclassSpringBootStartApplication extends SpringBootServletInitializer {

   @Override

   protectedSpringApplicationBuilder configure(SpringApplicationBuilderbuilder) {

       // 注意这里要指向原先用main方法执行的Application启动类

       returnbuilder.sources(BootStrap.class);

    }

}

 

4、将war包放到服务器上的tomcat下，重启即可。。。

如果你发到服务器上启动报错: file binary not execute大概意思即使二进制文件不能执行之类的。说明你没有修改maven插件的

<configuration><executable>true</executable></configuration>。你打包生成的war不是可执行的war。


ssl证书服务
crt个数转成pem格式命令  openssl x509 -in www.x.com.crt -out www.x.com.pem


springboot项目在eclipse启动正常，但部署到linux无法启动，这样的情况下，首先关注启动时候的日志。
无法启动的其中一个原因可能是因为jpa定义的数据库表在数据库中已经存在，这样的情况下删除数据库中引发冲突的数据库表可以解决问题
