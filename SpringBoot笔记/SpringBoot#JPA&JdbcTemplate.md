#SpringBoot#
##JPA/jdbcTemplate##

1 JPA

	JPA全称Java Persistence API.JPA通过JDK 5.0注解或XML描述对象－关系表的映射关系，并将运行期的实体对象持久化到数据库中。

2 Spring Data

	Spring Data是一个用于简化数据库访问，并支持云服务的开源框架。其主要目标是使得数据库的访问变得方便快捷，并支持map-reduce框架和云计算数据服务。此外，它还支持基于关系型数据库的数据服务，如Oracle RAC等。对于拥有海量数据的项目，可以用Spring Data来简化项目的开发，就如Spring Framework对JDBC、ORM的支持一样，Spring Data会让数据的访问变得更加方便。

3 Spring Data JPA

	1 Spring Data JPA能干什么？
	可以极大的简化JPA的写法，可以在几乎不用写实现的情况下，实现对数据的访问和操作。除了CRUD外，还包括如分页、排序等一些常用的功能。
	
	2 首先我们需要清楚的是Spring Data是一个开源框架，在这个框架中Spring Data JPA只是这个框架中的一个模块，所以名称才叫Spring Data JPA。如果单独使用JPA开发，你会发现这个代码量和使用JDBC开发一样有点烦人，所以Spring Data JPA的出现就是为了简化JPA的写法，让你只需要编写一个接口继承一个类就能实现CRUD操作了。

	3 JPA/Hibernate关系
	  JPA是一种规范，而Hibernate是它的一种实现。除了Hibernate，还有EclipseLink(曾经的toplink)，OpenJPA等可供选择，所以使用Jpa的一个好处是，可以更换实现而不必改动太多代码。

4 Spring Boot JPA-Hibernate

	1 在pom.xml添加mysql,spring-data-jpa依赖；

		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
		</dependency>


		<dependency>
		    <groupId>org.springframework.boot</groupId>
		    <artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>

	2 在application.properties文件中配置mysql连接配置文件
		
		########################################################
		###datasource
		########################################################
		spring.datasource.url = jdbc:mysql://localhost:3306/test
		spring.datasource.username = root
		spring.datasource.password = root
		spring.datasource.driverClassName = com.mysql.jdbc.Driver
		spring.datasource.max-active=20
		spring.datasource.max-idle=8
		spring.datasource.min-idle=8
		spring.datasource.initial-size=10

	3 在application.properties文件中配置JPA配置信息

		########################################################
		### Java Persistence Api
		########################################################
		# Specify the DBMS
		spring.jpa.database = MYSQL
		# Show or not log for each sql query
		spring.jpa.show-sql = true
		# Hibernate ddl auto (create, create-drop, update)
		spring.jpa.hibernate.ddl-auto = update
		# Naming strategy
		#[org.hibernate.cfg.ImprovedNamingStrategy  #org.hibernate.cfg.DefaultNamingStrategy]
		spring.jpa.hibernate.naming-strategy = org.hibernate.cfg.ImprovedNamingStrategy
		# stripped before adding them to the entity manager)
		spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL5Dialect

5 spring Data接口介绍
	
	1 Repository 接口是 Spring Data 的一个核心接口，它不提供任何方法，开发者需要在自己定义的接口中声明需要的方法 ：
 		* public interface Repository<T, ID extends Serializable> { } 

	  有这么几点需要强调下：
		1. Repository是一个空接口，即是一个标记接口；
		2. 若我们定义的接口继承了Repository，则该接口会被IOC容器识别为一个Repository Bean纳入到IOC容器中，进而可以在该接口中定义满足一定规范的方法。
		3. 实际上也可以通过@RepositoryDefinition,注解来替代继承Repository接口。
		4. 查询方法以find | read | get开头；
		5. 涉及查询条件时，条件的属性用条件关键字连接，要注意的是条件属性以首字母大写。
		6. 使用@Query注解可以自定义JPQL语句实现更灵活的查询。
	
	2 CrudRepository接口

		CrudRepository 接口提供了最基本的对实体类的添删改查操作
		  --T save(T entity);//保存单个实体   
		  --Iterable<T> save(Iterable<? extends T> entities);//保存集合         
		  --T findOne(ID id);//根据id查找实体          
		  --boolean exists(ID id);//根据id判断实体是否存在          
		  --Iterable<T> findAll();//查询所有实体,不用或慎用!          
		  --long count();//查询实体数量          
		  --void delete(ID id);//根据Id删除实体          
		  --void delete(T entity);//删除一个实体   
		  --void delete(Iterable<? extends T> entities);//删除一个实体的集合          
		  --void deleteAll();//删除所有实体,不用或慎用!   

	3 PagingAndSortingRepository接口
		
		该接口提供了分页与排序功能   
 		--Iterable<T> findAll(Sort sort); //排序    
		--Page<T> findAll(Pageable pageable); //分页查询（含排序功能） 

	4 其他接口
		
		JpaRepository：查找所有实体，排序、查找所有实体，执行缓存与数据库同步

		JpaSpecificationExecutor：不属于Repository体系，实现一组 JPA Criteria 查询相关的方法，封装  JPA Criteria 查询条件。通常使用匿名内部类的方式来创建该接口的对象。
		
		自定义 Repository：可以自己定义一个MyRepository接口。

6 Spring Boot JdbcTemplate
	
	1 在pom.xml加入jdbcTemplate的依赖	
		<dependency>
		    <groupId>org.springframework.boot</groupId>
		    <artifactId>spring-boot-starter-jdbc</artifactId>
		</dependency>
		
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
	
	2 代码使用：
		在需要使用的类中加入
		@Resuorce
		private JdbcTemplate jdbcTemplate;

		将类声明为：@Repository，引入JdbcTemplate
		public Demo getById(long id){
			String sql = "select *from Demo where id=?";
			RowMapper<Demo> rowMapper = new BeanPropertyRowMapper<Demo>(Demo.class);
			return jdbcTemplate.queryForObject(sql, rowMapper,id);
		}

	3 编写DemoService类，引入DemoDao进行使用
		@Resource
		private DemoDao demoDao;
		
		public void save(Demo demo){
			demoRepository.save(demo);
		}
