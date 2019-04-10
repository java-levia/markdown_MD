### Spring-Mybatis 原理#Spring管理Mybatis的Mapper接口与实例化相关原理

1. 毋庸置疑的是，Spring在将Mybatis的Mapper接口实例化的过程中使用了Jdk动态代理。

   1. 先实现Jdk动态代理的InvocationHandle方法，并重写其中的抽象方法invoke，在invoke中实现代理逻辑。

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

      