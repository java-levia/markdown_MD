# 线程池ThreadPoolExecutor

## 1. 构造函数

1. ```java
   //ThreadPoolExecutor的构造函数 
   public ThreadPoolExecutor(int corePoolSize,
                                 int maximumPoolSize,
                                 long keepAliveTime,
                                 TimeUnit unit,
                                 BlockingQueue<Runnable> workQueue,
                                 ThreadFactory threadFactory,
                                 RejectedExecutionHandler handler) {
           if (corePoolSize < 0 ||
               maximumPoolSize <= 0 ||
               maximumPoolSize < corePoolSize ||
               keepAliveTime < 0)
               throw new IllegalArgumentException();
           if (workQueue == null || threadFactory == null || handler == null)
               throw new NullPointerException();
           this.acc = System.getSecurityManager() == null ?
                   null :
                   AccessController.getContext();
           this.corePoolSize = corePoolSize;
           this.maximumPoolSize = maximumPoolSize;
           this.workQueue = workQueue;
           this.keepAliveTime = unit.toNanos(keepAliveTime);
           this.threadFactory = threadFactory;
           this.handler = handler;
       }
   
   /**
   构造函数的参数含义如下:
   
   	corePoolSize:指定了线程池中的线程数量，它的数量决定了添加的任务是开辟新的线程去执行，还是放到workQueue任务队列中去；
   
   	maximumPoolSize:指定了线程池中的最大线程数量，这个参数会根据你使用的workQueue任务队列的类型，决定线程池会开辟的最大线程数量；
   
   	keepAliveTime:当线程池中空闲线程数量超过corePoolSize时，多余的线程会在多长时间内被销毁；
   
   	unit:keepAliveTime的单位
   
   	workQueue:任务队列，被添加到线程池中，但尚未被执行的任务；它一般分为直接提交队列、有界任务队列、无界任务队列、优先任务队列几种；
   
   	threadFactory:线程工厂，用于创建线程，一般用默认即可；
   
   	handler:拒绝策略；当任务太多来不及处理时，如何拒绝任务；
   	
   **/
   
   //handler:拒绝策略；当任务太多来不及处理时，如何拒绝任务；
   public interface RejectedExecutionHandler {
       void rejectedExecution(Runnable r, ThreadPoolExecutor executor);
   }
   /**里面只有一个方法。当要创建的线程数量大于线程池的最大线程数的时候，新的任务就会被拒绝，就会调用这个接口里的这个方法。
   
   可以自己实现这个接口，实现对这些超出数量的任务的处理。
   
   ThreadPoolExecutor自己已经提供了四个拒绝策略，分别是CallerRunsPolicy,AbortPolicy,DiscardPolicy,DiscardOldestPolicy
   **/
   
   ```

   ## ThreadPoolExecutor的四种拒绝策略

   ### 	AbortPolicy

   ​	ThreadPoolExecutor中默认的拒绝策略就是AbortPolicy,直接抛出异常。

   ```java
   private static final RejectedExecutionHandler defaultHandler =
       new AbortPolicy();
   
   
   //以下是这个策略的实现
   public static class AbortPolicy implements RejectedExecutionHandler {
       public AbortPolicy() { }
       public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
           throw new RejectedExecutionException("Task " + r.toString() +
                                                " rejected from " +
                                                e.toString());
       }
   }
   
   //直接抛出个RejectedExecutionException异常，也不执行这个任务了。
   ```

   ### 		CallerRunsPolicy

   ​		CallerRunsPolicy在任务被拒绝添加后，会调用当前线程池的所在的线程去执行被拒绝的任务(调用线程池所在的线程执行任务,这操作骚啊, 这个策略的缺点就是可能会阻塞主线程)。

   ### 		DiscardPolicy	

   ​		这个东西什么都没干,采用这个拒绝策略，会让被线程池拒绝的任务直接抛弃，不会抛异常也不会执行。

   ### 		DiscardOldestPolicy

   ​		DiscardOldestPolicy策略的作用是，当任务呗拒绝添加时，会抛弃任务队列中最旧的任务也就是最先加入队列的，再把这个新任务添加进去。

   

## 2.**workQueue任务队列**

​		任务队列一般分为直接提交队列/有界任务队列/无界任务队列/优先任务队列;

1. **直接提交队列(SynchronousQueue)**: 	设置为SynchronousQueue队列，SynchronousQueue是一个特殊的BlockingQueue，它没有容量，每执行一个插入操作就会阻塞，需要再执行一个删除操作才会被唤醒，反之每一个删除操作也都要等待对应的插入操作。

		2. **有界的任务队列(ArrayBlockingQueue)**:  使用ArrayBlockingQueue有界任务队列，若有新的任务需要执行时，线程池会创建新的线程，直到创建的线程数量达到corePoolSize时，则会将新的任务加入到等待队列中。若等待队列已满，即超过ArrayBlockingQueue初始化的容量，则继续创建线程，直到线程数量达到maximumPoolSize设置的最大线程数量，若大于maximumPoolSize，则执行拒绝策略。在这种情况下，线程数量的上限与有界任务队列的状态有直接关系，如果有界队列初始容量较大或者没有达到超负荷的状态，线程数将一直维持在corePoolSize以下，反之当任务队列已满时，则会以maximumPoolSize为最大线程数上限。
  		3. **无界任务队列(LinkedBlockingQueue)**: 使用无界任务队列，线程池的任务队列可以无限制的添加新的任务，而线程池创建的最大线程数量就是你corePoolSize设置的数量，也就是说在这种情况下maximumPoolSize这个参数是无效的，哪怕你的任务队列中缓存了很多未执行的任务，当线程池的线程数达到corePoolSize后，就不会再增加了；若后续有新的任务加入，则直接进入队列等待，当使用这种任务队列模式时，一定要注意你任务提交与处理之间的协调与控制，不然会出现队列中的任务由于无法及时处理导致一直增长，直到最后资源耗尽的问题。
  		4. **优先任务队列(PriorityBlockingQueue)**: 通过运行的代码我们可以看出PriorityBlockingQueue它其实是一个特殊的无界队列，它其中无论添加了多少个任务，线程池创建的线程数也不会超过corePoolSize的数量，只不过其他队列一般是按照先进先出的规则处理任务，而PriorityBlockingQueue队列可以自定义规则根据任务的优先级顺序先后执行。

## 3. ThreadFactory自定义线程创建

​	线程池中线程就是通过ThreadPoolExecutor中的ThreadFactory，线程工厂创建的。那么通过自定义ThreadFactory，可以按需要对线程池中创建的线程进行一些特殊的设置，如命名、优先级等，下面代码我们通过ThreadFactory对线程池中创建的线程进行记录与命名

```java
public class ThreadPool {
    private static ExecutorService pool;
    public static void main( String[] args )
    {
        //自定义线程工厂
        pool = new ThreadPoolExecutor(2, 4, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(5),
                new ThreadFactory() {
            public Thread newThread(Runnable r) {
                System.out.println("线程"+r.hashCode()+"创建");
                //线程命名
                Thread th = new Thread(r,"threadPool"+r.hashCode());
                return th;
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
          
        for(int i=0;i<10;i++) {
            pool.execute(new ThreadTask());
        }    
    }
}

public class ThreadTask implements Runnable{    
    public void run() {
        //输出执行线程的名称
        System.out.println("ThreadName:"+Thread.currentThread().getName());
    }
}
```



## 4. ThreadPoolExecutor拓展

​	ThreadPoolExecutor扩展主要是围绕beforeExecute()、afterExecute()和terminated()三个接口实现的

**1、beforeExecute：线程池中任务运行前执行**

**2、afterExecute：线程池中任务运行完毕后执行**

**3、terminated：线程池退出后执行**

通过这三个接口我们可以监控每个任务的开始和结束时间，或者其他一些功能。

```java
public class ThreadPool {
    private static ExecutorService pool;
    public static void main( String[] args ) throws InterruptedException
    {
        //实现自定义接口
        pool = new ThreadPoolExecutor(2, 4, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(5),
                new ThreadFactory() {
            public Thread newThread(Runnable r) {
                System.out.println("线程"+r.hashCode()+"创建");
                //线程命名
                Thread th = new Thread(r,"threadPool"+r.hashCode());
                return th;
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy()) {
    
            protected void beforeExecute(Thread t,Runnable r) {
                System.out.println("准备执行："+ ((ThreadTask)r).getTaskName());
            }
            
            protected void afterExecute(Runnable r,Throwable t) {
                System.out.println("执行完毕："+((ThreadTask)r).getTaskName());
            }
            
            protected void terminated() {
                System.out.println("线程池退出");
            }
        };
          
        for(int i=0;i<10;i++) {
            pool.execute(new ThreadTask("Task"+i));
        }    
        pool.shutdown();
    }
}

public class ThreadTask implements Runnable{    
    private String taskName;
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public ThreadTask(String name) {
        this.setTaskName(name);
    }
    public void run() {
        //输出执行线程的名称
        System.out.println("TaskName"+this.getTaskName()+"---ThreadName:"+Thread.currentThread().getName());
    }
}
```

## 5. 线程池线程数量

​	线程吃线程数量的设置没有一个明确的指标，根据实际情况，只要不是设置的偏大和偏小都问题不大，结合下面这个公式即可

```
            /**
             * Nthreads=CPU数量
             * Ucpu=目标CPU的使用率，0<=Ucpu<=1
             * W/C=任务等待时间与任务计算时间的比率
             */
            Nthreads = Ncpu*Ucpu*(1+W/C)
```

以上就是对ThreadPoolExecutor类从构造函数、拒绝策略、自定义线程创建等方面介绍了其详细的使用方法，从而我们可以根据自己的需要，灵活配置和使用线程池创建线程，其中如有不足与不正确的地方还望指出与海涵。