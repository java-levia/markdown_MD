## Lambda表达式

1. Lambda表达式可以将函数作为方法中的一个参数进行传递
2.  Java8 添加了一个新的特性Function，顾名思义这一定是一个函数式的操作。我们知道Java8的最大特性就是函数式接口。所有标注了`@FunctionalInterface`注解的接口都是函数式接口，具体来说，所有标注了该注解的接口都将能用在lambda表达式上。

3. Lambda表达式的一些应用

   * Predicate接口： 该接口可以将条件作为参数传入到A方法中，然后在方法中使用predicate<被操作对象的泛型>.test(传入需要被操作的对象)，然后在调用A方法时使用Lambda表达式构造号条件当作predicate参数传入到方法中。

     1. 应用场景： 在一些涉及到逻辑判断的方法中，将Predicate作为方法的参数传入，然后将Predicate作为判断条件。在调用方法时使用Lambda表达式将判断逻辑构造出来作为参数（Lambda表达式可以看作Predicate或者Function接口的一种实现）

        ```java
        //Predicate接口是用的方法内部的参数，如果要使用从外部传入的参数可以使用Function接口的功能
        //Predicate接口和Function接口的共同点是，都可以使用Lambda表达式将函数作为参数传入到方法中
        //不同点是，Predicate接口的Lambda表达式通过test()走Lambda表达式的返回值是Boolean类型，多用于构造if等判断条件；Function接口的Lambda表达式自由度更高，可以自定义入参和返回值类型
        
        testPredicate(Predicate<> predicate){
        ArrayList list = new ArrayList();
            if(predicate.test(list)){
                
            }
        }
        ```

        

     

   * Function接口：该接口可以理解为带参数的Predicate接口，可以传递方法外部的参数，具体表现为

     ```java
     //从testFunction方法外部传入的参数obj，在方法内部使用Function接口的apply()方法进行调用并传入参数
     testFunction(Function<入参泛型， 返回值泛型> function, Object obj){
         function.apply(obj)
     }
     ```

     

4. Lambda表达式语法

   1. 函数式接口
      * Lambda表达式的前置条件：必须是函数式接口才可以使用Lambda表达式
      * 函数式接口的基本特征：
        1. 接口中标注了FunctionInterface注解
        2. 接口中只有一个抽象方法（除此之外可以有其他的默认实现），会被编译器自动识别成函数式接口
        3. 接口中有一个抽象方法，同时包含了Object类的其他抽象方法也会被识别成抽象接口

   2. Lambda表达式的三种编写方式

      * expression: 单条语句表达式

        * 单条语句表达式只能是一个完整的语句，不能有多个语句
        * 单条语句的表达式如果可以通过上下文推断返回值则可以省略return，

        ```java
        a->System.out.println(a);
        ```

        

      * statement： 语句块

        * 多条语句要使用语句块的方式，如果是需要有返回值则必须return

        ```java
        //构建函数表达式
        
        (String a, String b)->{
            System.out.println(a);
            System.out.println(b);
            //如果需要有返回值则必须return
            return a+b;
        }
        ```

        

      * reference：方法引用

        1. 如果某个方法在结构上与Lambda表达式中对应的方法是匹配的那么就可以直接引用给Lambda表达式。其总共包含四种引用类型

      ```java
      // 1. 引用实例方法
      
      public class FunctionDemo {
      	//该方法相当于实现了 某个特殊的函数表达式 a
          public String sayHello(String name){
              System.out.println(name);
              return name+"say hi";
          }
      	
          public static  String functionTest(){
              //new一个对象
              FunctionDemo fd = new FunctionDemo();
              //使用对象以Lambda表达式的方式（：：）构建一个满足函数表达式 a的对象f
              Function<String, String> f = fd::sayHello;
              //使用对象f调用函数表达式a 中的抽象方法并传参获得返回值
              return f.apply("levia");
          }
      
          public static void main(String[] args) {
              System.out.println(functionTest());
          }
      }
      
      /**
       1. 基于Lambda表达式引用实例方法，首先要构建一个Lambda函数表达式 Lam，表达式中有一个抽象方法 apply()（这个名字可以命名为满足命名规范的任意名字）；
       2. 然后创建一个类 M，使用类似于实现这个函数表达式中抽象方法的方式构建一个方法 met()
       （说是实现，其实并不是真的实现，首先这个类不需要继承或者实现函数表达式接口，其次实现方法的方法名称也没必要与函数表达式中抽象方法的名称一样，但是方法的入参和出参需要与函数表达式中抽象方法保持一致）；
       3. 在进行方法引用时，先new一个M类的对象 m，然后使用这个m::met()构建满足Lambda表达式的函数表达式对象 Lam lamObj=m::met()
       4. 然后使用lamObj.apply(入参)的方式进行真正的方法调用。
      */
      
      
      //基于Lambda表达式引用构造方法
      //构造方法引用的条件是  需要获得构造器对象con 命名 = nameOfObject::new这个表达式中，nameOfObject类中必须有与构造器相同的的返回值类型（好TM绕）
      //构造方法引用是根据结果进行推导，结果是什么类型的，给返回的就是什么类型的构造方法
      Supplier s =  ArrayList::new;
      
      ```

      

