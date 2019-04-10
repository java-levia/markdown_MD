Quartz定时任务框架

1. 简介

   * Quartz是基于java的一个开源调度框架。
   * 比较重要的几个概念
     1. Scheduler  调度器。不管开发者要实现怎样的定时任务，最终都需要使用Scheduler对象来调度，他的角色类似公司中的高管，是一个统筹的作用。在Spring中是通过SchedulerFactoryBean封装起来的
     2. Trigger 触发器。用于定义调度任务的时间规则，与我们以前用于定时任务的Cron表达式发生直接关系的就是这个对象。有SimpleTrigger,CronTrigger,DateIntervalTrigger和NthIncludedDayTrigger等多种实现，其中CronTrigger使用比较多。
     3. Calendar  他是一些日历特定时间点的集合。一个trigger可以包含多个Calendar，以便排除或包含某些时间点。
     4. JobDetail: 用来描述Job实现类及其他相关静态信息。在Spring种有JobDetailFactoryBean和MetbodInvokingJobDetailFactoryBean两种实现，如果任务调度只需要执行某个类的某个方法，就可以通过MethodInvokingJobDetailFactoryBean来调用。
     5. Job：是一个接口，只有一个方法void execute(JobExecutionContext context),开发者实现该接口定义运行任务，JobExecutionContext类提供了调度上下文的各种信息。Job运行时的信息保存在JobDataMap实例中。实现Job接口的任务，默认是无状态的，若要将Job设置成有状态的，在quartz中是给实现的Job添加@DisallowConcurrentExecution注解（以前是实现StatefulJob接口，现在已被Deprecated）,在与spring结合中可以在spring配置文件的job detail中配置concurrent参数。
   * 核心元素
     1. Quertz任务调度的核心元素是scheduler，trigger，job，其中trigger和job是任务调度的元素据（trigger对象定义调度时间规则，job对象定义任务需要执行的业务流程），scheduler是实际执行调度的控制器。
     2. 在 Quartz 中，trigger 是用于定义调度时间的元素，即按照什么时间规则去执行任务。Quartz 中主要提供了四种类型的 trigger：SimpleTrigger，CronTirgger，DateIntervalTrigger，和 NthIncludedDayTrigger。这四种 trigger 可以满足企业应用中的绝大部分需求。
     3. 在 Quartz 中，job 用于表示被调度的任务。主要有两种类型的 job：无状态的（stateless）和有状态的（stateful）。对于同一个 trigger 来说，有状态的 job 不能被并行执行，只有上一次触发的任务被执行完之后，才能触发下一次执行。Job 主要有两种属性：volatility 和 durability，其中 volatility 表示任务是否被持久化到数据库存储，而 durability 表示在没有 trigger 关联的时候任务是否被保留。两者都是在值为 true 的时候任务被持久化或保留。一个 job 可以被多个 trigger 关联，但是一个 trigger 只能关联一个 job。
     4. 在 Quartz 中， scheduler 由 scheduler 工厂创建：DirectSchedulerFactory 或者 StdSchedulerFactory。 第二种工厂 StdSchedulerFactory 使用较多，因为 DirectSchedulerFactory 使用起来不够方便，需要作许多详细的手工编码设置。 Scheduler 主要有三种：RemoteMBeanScheduler， RemoteScheduler 和 StdScheduler

2. 创建一个简单的QuertZ定时任务

   ```java
   package com.test.quartz;
   
   import static org.quartz.DateBuilder.newDate;
   import static org.quartz.JobBuilder.newJob;
   import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
   import static org.quartz.TriggerBuilder.newTrigger;
   
   import java.util.GregorianCalendar;
   
   import org.quartz.JobDetail;
   import org.quartz.Scheduler;
   import org.quartz.Trigger;
   import org.quartz.impl.StdSchedulerFactory;
   import org.quartz.impl.calendar.AnnualCalendar;
   
   public class QuartzTest {
   
       public static void main(String[] args) {
           try {
               //创建scheduler
               Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
   
               //定义一个Trigger
               //定义name/group
               Trigger trigger = newTrigger().withIdentity("trigger1", "group1") 
                   .startNow()//一旦加入scheduler，立即生效
                   .withSchedule(simpleSchedule() //使用SimpleTrigger
                       .withIntervalInSeconds(1) //每隔一秒执行一次
                       .repeatForever()) //一直执行，奔腾到老不停歇
                   .build();
   
               //定义一个JobDetail
               //定义Job类为HelloQuartz类，这是真正的执行逻辑所在。
               //也就是说，真正的业务逻辑是写在HelloQuartz.class这个类中，这个类实现了Job接口并重写了execute方法，execute方法中的逻辑就是这个调度任务的业务逻辑
               JobDetail job = newJob(HelloQuartz.class) 
                   .withIdentity("job1", "group1") //定义name/group
                   .usingJobData("name", "quartz") //定义属性
                   .build();
   
               //加入这个调度
               scheduler.scheduleJob(job, trigger);
   
               //启动之
               scheduler.start();
   
               //运行一段时间后关闭
               //在这里sleep10秒就是让定时任务执行10秒，因为调度线程和执行线程并不是同一个
               Thread.sleep(10000);
               scheduler.shutdown(true);
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
   }
   /**
   小结：
   	创建一个完整调度任务的基本流程:
   	1. 创建一个Scheduler任务调度对象
   	2. 创建一个调度触发器Trigger
   	3. 创建一个业务逻辑封装对象Job并传入业务逻辑对象（一个实现了Job接口的类）
   	4. 将trigger和Job对象传入Scheduler对象中，至此一个简单的任务调度对象构建完毕
   	5. 使用Scheduler的start()方法执行调度
   	6. 还可以使用shutdown方法关闭。
   */
   ```

3. 关于Quartz的一些设计思想

   1. 关于name和group

      * name是任务在这个Scheduler里面的唯一标识。如果我们要更新一个JobDetail定义，只需要设置一个name相同的JobDetail实例即可。
      * group是一个组织单元，scheduler会提供一些对整组操作的API，比如scheduler.resumeJobs().

   2. Trigger

      在详细介绍每一种trigger之前，先介绍一些trigger的共性

      ​	StartTime&EndTime：这两个时间是Trigger会被触发的时间区间。在这个区间之外Trigger是不会被触发的。所有的Trigger都会包含这两个属性。

      ​	优先级Priority：当scheduler比较繁忙的时候，可能在同一个时刻，有多个Trigger被触发了，但资源不足（比如线程池不足），那么这个时候比剪刀石头布更好的方式，就是设置优先级。优先级高的先执行。需要注意的是，优先级只有在同一时刻执行的Trigger之间才会起作用，如果一个Trigger是9:00，另一个Trigger是9:30。那么无论后一个优先级多高，前一个都是先执行。

      优先级的值默认是5，当为负数时使用默认值。最大值似乎没有指定，但建议遵循Java的标准，使用1-10，不然鬼才知道看到【优先级为10】是时，上头还有没有更大的值。

      ​	错失触发策略MisFire：类似的Scheduler资源不足的时候，或者击确崩溃重启等，有可能某一些Trigger在应该触发的时间点没有被触发，也就是MisFire了。这个时候Trigger需要一个策略来处理这种情况，每一个不同种类的Trigger可选的策略各不相同。

      ​	会造成misFire的几种情况：

      ​		1）系统因为某些原因被重启。在系统关闭到重新启动之间的一段时间里，可能有些任务会 被 misfire；

      ​		2）Trigger 被暂停（suspend）的一段时间里，有些任务可能会被 misfire；

      ​		3）线程池中所有线程都被占用，导致任务无法被触发执行，造成 misfire；

      ​		4）有状态任务在下次触发时间到达时，上次执行还没有结束；

      ​	这里有两个点需要重点注意——MisFire的触发是有一个阈值的，这个阈值是配置在JobStore的。比如RAMJobStore是org.quartz.jobStore.misfireThreshold.只有不超过这个阈值才会重新调用。（这个阈值的概念是：scheduler可以忍受的未被调度的时间，如果超过这个阈值就不会被重新调用，默认一分钟，单位毫秒）

      ​	所有的MisFire策略实际上是解答两个问题：

        1. 已经MisFire的任务还要重新触发吗？

        2. 如果已经发生MisFire，要调整现有的调度时间吗？

           Calendar： 这里的Calendar不是jdk的java.util.Calendar,不是为了计算日期。它的作用是在于补充Trigger的时间。可以排除或加入某一些特定的时间点。

           ```
           AnnualCalendar cal = new AnnualCalendar(); //定义一个每年执行Calendar，精度为天，即不能定义到2.25号下午2:00
           java.util.Calendar excludeDay = new GregorianCalendar();
           excludeDay.setTime(newDate().inMonthOnDay(2, 25).build());
           cal.setDayExcluded(excludeDay, true);  //设置排除2.25这个日期
           scheduler.addCalendar("FebCal", cal, false, false); //scheduler加入这个Calendar
           
           //定义一个Trigger
           Trigger trigger = newTrigger().withIdentity("trigger1", "group1") 
               .startNow()//一旦加入scheduler，立即生效
               .modifiedByCalendar("FebCal") //使用Calendar !!
               .withSchedule(simpleSchedule()
                   .withIntervalInSeconds(1) 
                   .repeatForever()) 
               .build();
           ```

           