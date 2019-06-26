### Spring-Mybatis 原理#Spring管理Mybatis的Mapper接口与实例化相关原理

1. 毋庸置疑的是，Spring在将Mybatis的Mapper接口实例化的过程中使用了Jdk动态代理。

   1. 先实现Jdk动态代理的InvocationHandle类，并重写其中的抽象方法invoke，在invoke中实现代理逻辑。

      ```java
      public xxx implements InvocationHandler(){
          @override
          public Object invoke(Object proxy, Method method, Object[] args){
              
          }
      }
      ```

      

   2. 需要对代理的方法进行调用时

      ```java
      //这个方法返回的是织入了代理逻辑之后的代理对象
      Proxy.newProxyInstance(ClassLoader, interface, 真实对象（需要被代理的对象）)
      //在Mybatis中，如果仅仅是产生一个将    
      ```

   3. 经过以上步骤就可以产生一个可用于操作数据库数据的Mapper对象，但在这这种情况下还是面临一个问题，仅仅是利用这产生的mapper对象只存在于内存中(代理的情况下，我们只有接口而获取不到对象的类)，而我们在进行业务操作的过程中还需要在spring框架中多次调用mapper对象对我们的业务数据进行增删改查，这样的话就需要将动态代理长生的对象注入到Spring容器中进行管理，但常规的方式无法达到这一目的（因为代理产生的对象的特殊性），这时就需要用到BeanDefinition这个类。（产生代理是比较简单的，但是产生的代理类如果交给Spring进行管理？）

   4. 向Spring容器中注入Bean的流程中（具体见图SpringBean的实例化过程），Spring提供了一些拓展点，其中就有一个名为BeanFactoryPostProcessor(bean工厂的后置处理器)的接口,实现这个接口并重写其中的postProcessorBeanFactory方法,方法的参数ConfigurableListableBeanFactory中包含了BeanDefinitionMap中的信息，因此，通过ConfigurableListableBeanFactory可以获得目标类的BeanDefinition

      ```java
      //这个类会在Spring构建工厂的时候执行
      @Component
      public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor{
          @Overrider
          public void postProcessorBeanFactory(ConfigurableListableBeanFactory beanFactory){
              //通过getBeanDefinition方法获取到目标类的BeanDefinition
              GenericBeanDefinition definition = (GenericBeanDefinition)beanFactory.getBeanDefinition("全类名")；
                  //然后使用BeanDefinition调用SetBeanClass(需要关联到目标类的对象.class)
               definition.SetBeanClass(需要关联到目标类的对象.class)   
          }
      }
      
      //经过这样的处理后，如果我们需要在Spring容器中获取目标类的Bean是无法获取到的，但是可以获取到SetBeanClass()方法中设置的对象。因为在经过BeanFactoryPostProcessor的处理后，注入到Spring容器中的bean变成了SetBeanClass()方法中设置的对象
      ```

   5. 从以上内容可以看出来，Spring产生Bean并不一定需要对Bean进行扫描然后加入到容器中，而是可以通过其他方式，比如直接将BeanDefinition载入到BeanDefinitionMap中之后，Spring就可以通过preInstantiateSingletons方法产生Bean注入到Spring容器中。由此 引申出来一种将代理对象注入到Spring中的方式（先获得代理类的BeanDefinition,然后再把代理类的BeanDefinition注入到BeanDefinitionMap中，之后preInstantiateSingletons方法就会将代理类的Bean对象添加到Spring容器中，但是这种方式对代理类来说不适用），还有一种方式是，ConfigurableListableBeanFactory这个类中有一个方法registerSingleton("beanName",  bean对象)，这个方法可以将实例化出来的对象注册到Spring容器中，这种方式可以将代理对象注入到Spring容器中

      ```java
      @Component
      public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor{
          @Overrider
          public void postProcessorBeanFactory(ConfigurableListableBeanFactory beanFactory){
              //获取到一个代理对象 
              Dao dao = Proxy.newProxyInstance(ClassLoader, interface, 真实对象（需要被代理的对象）)
              //这样就将dao注入到了Spring容器中
              beanFactory.registerSingleton("dao", dao);
          }
      }
      
      //以上方式可以将代理对象注入到Spring容器中，其他的与Mybatis相关的资源也都是被Spring管理的，例如数据源、Mapper对象、sqlSession对象。但是InvocationHandler接口的实现类并没有被Spring管理，那么在这个InvocationHandler接口的实现类中是无法获取到Mybatis的这些资源的，但实际上我创建这个代理类的原因就是我们需要通过动态代理的方式执行我们的sql查询。而同时也无法直接将InvocationHandler接口的实现类交给Spring管理，因为一个应用会有很多个Mapper对象，如果针对每一个Mapper对象都创建一个InvocationHandler接口的实现类交给Spring管理是不现实的，所以只能寻找其他办法
      ```

      

   6. 针对以上问题，Spring中的FactoryBean能够解决这个问题

      1. FactoryBean和BeanFactory的根本区别：

         * Spring中有很多个IOC容器，其中像preInstantiateSingletons产生的Bean（也就是BeanFactory产生的Bean）注入的是普通的IOC容器，但是FactoryBean所产生的Bean和preInstantiateSingletons所产生的Bean并不在同一类IOC容器中，这才是FactoryBean和BeanFactory的根本区别

         * FactoryBean的特性：

           ```java
           //Mybatis源码中就有一个MapperFactoryBean类实现了FactoryBean这个接口
           //实现一个FactoryBean
           @Component
           public MyMapperFactoryBean implements FactoryBean{
               
               @Override
               public Object getObject() throws Exception{
                   return null;
               }
               
               @Override
               public Class<?> getObjectType() {
                   return null;
               }
           }
           ```

           

           1. 它是一个普通的Bean：具体体现在可以通过@Component将FactoryBean的实现类注入到Spring容器中，并且通过Spring的getBean()方法获取到这个Bean

              ```java
              //在这里，如果类名前带上&则获取的是MyMapperFactoryBean这个Bean
              //
              annotationConfigApplicationContext.getBean("&myMapperFactoryBean").getClass().getSimpleName()
              ```

              

           2. 它是一个特殊 的Bean：具体体现在

           ```java
           //1 首先它需要依赖FactoryBean接口
           //2 在调用 类名前不带上&符号则返回的Bean是它的getObject方法里面的返回值
           annotationConfigApplicationContext.getBean("myMapperFactoryBean").getClass().getSimpleName()
           ```

           

      2. Spring提供了一个接口ImportBeanDefinitionRegistrar,实现这个接口并重写其中registerBeanDefinitions(AnnotationMetadata importingClassMetadata,  BeanDefinitionRegistry registry) 方法，通过这个方法就可以往BeanDefinitionMap中注入BeanDefinition。要获取一个类的BeanDefinition可以通过BeanDefinitionBuilder这个类获取

         ```java
         public void registerBeanDefinition(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry){
             //通过BeanDefinitionBuilder 传入一个FactoryBean的实现类可以构建一个BeanDefinition
             BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(FactoryBean的实现类.class);
             AbstractBeanDefinition definition = builder.getBeanDefinition();
             //通过BeanDefinition还可以向对应的Bean中传入所需的对象，以下这行代码就是向Bean中传入了所需的对象
             definition.getConstructorArgumentValues().addGenericArgumentValue(Bean的名称.class)
             //将Bean的名称和对应的BeanDefinition对象传入registerBeanDefinition方法中可以注入到BeanDefinition中
             registry.registerBeanDefinition("BeanName", definition);
         }
         ```

         