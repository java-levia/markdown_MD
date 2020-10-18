# SpringBoot源码分析



```java
/**Configuration注解*/
@Configuration注解在SpringBoot中用于做一些基础的配置，比如说Bean的注入等
  
//通过AnnotationConfigApplicationContext可以获取到所有通过@Confuguration注解注入到容器中的Bean
  
//通过@Bean注解注入到容器中的类最终在SpringBoot中被解析为BeanDefinition(Bean定义对象)
 
```

### BeanFactoryPostProcessor

```java
//通过BeanFactoryPostProcessor接口，可以将BeanDefinition的定义进行更改，在实例化时将另外一个Bean注入到容器中

@Component
public class LeviaBeanFactoryPostProccessor implements BeanFactoryPostProcessor {


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition)beanFactory.getBeanDefinition("beanA");
        genericBeanDefinition.setBeanClass(BeanD.class);
    }
}
```

### AutowireMode(注入模型)

```java
## AutowireMode 用于控制Bean给属性进行注入的方式
	//在日常开发中，通过扫描@Controller @Service这种注解修饰的类进行注入，这种就属于AUTOWIRE_NO这种注入类型，AUTOWIRE_NO是默认的注入类型
	int AUTOWIRE_NO = 0;
	
  int AUTOWIRE_BY_NAME = 1;
  int AUTOWIRE_BY_TYPE = 2;
  int AUTOWIRE_CONSTRUCTOR = 3;


//通过自定义BeanDefinition（实现BeanFactoryPostProcessor）可以更改Bean的注入方式
@Component
public class LeviaBeanFactoryPostProccessor implements BeanFactoryPostProcessor {


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition)beanFactory.getBeanDefinition("beanA");
        /**自定义BeanDefinition,更改这个BeanDefinition最终注入到容器中的实体**/
        //genericBeanDefinition.setBeanClass(BeanD.class);
        /**设置BeanDefinition的注入模型**/
        /** AUTOWIRE_BY_NAME、AUTOWIRE_BY_TYPE 这种注入类型需要依赖类中的setXXX方法（这种方式即 构造器注入 setter注入 接口注入中的  setter注入）**/
        /** AUTOWIRE_BY_NAME 表示注入的时候是根据 setXXX(XXX xxx) 方法中的 xxx(属性名)进行注入，如果属性名不匹配则无法进行注入**/
        /** AUTOWIRE_BY_TYPE 表示注入的时候是根据 setXXX(XXX xxx) 方法中的 XXX的类型进行注入，如果类型不匹配则无法进行注入**/
        genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
    }
}
```

### ConstructorArgumentValues()

```java
@Component
public class LeviaBeanFactoryPostProccessor implements BeanFactoryPostProcessor {


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition)beanFactory.getBeanDefinition("beanA");
        /**自定义BeanDefinition,更改这个BeanDefinition最终注入到容器中的实体**/
        //genericBeanDefinition.setBeanClass(BeanD.class);
        /**设置BeanDefinition的注入模型**/
        /** AUTOWIRE_BY_NAME、AUTOWIRE_BY_TYPE 这种注入类型需要依赖类中的setXXX方法（这种方式即 构造器注入 setter注入 接口注入中的  setter注入）**/
        /** AUTOWIRE_BY_NAME 表示注入的时候是根据 setXXX(XXX xxx) 方法中的 xxx(属性名)进行注入，如果属性名不匹配则无法进行注入**/
        /** AUTOWIRE_BY_TYPE 表示注入的时候是根据 setXXX(XXX xxx) 方法中的 XXX的类型进行注入，如果类型不匹配则无法进行注入**/
        //genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        /** 通过自定义该方法可以设置Bean初始化时默认调用的构造函数**/
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addIndexedArgumentValue(0, "");
        genericBeanDefinition.setConstructorArgumentValues(constructorArgumentValues);
    }
}
```

