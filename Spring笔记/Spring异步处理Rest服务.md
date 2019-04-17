Spring异步处理Rest服务

1. 使用异步的方式处理Rest服务的好处在于，不会占用太多的tomcat线程，tomcat的吞吐量就能有一个比较大的提升。处理方式是，在一个主线程内使用Callable（如果有返回值）或者实现Runnable接口（没有返回值）创建一个副线程，真正的业务逻辑放到副线程中进行处理，

   ```java
   public Callable<String> order(){
     logger.info("主线程开始") 
       Callable<String> result = new Callable<String>(){
           
           public String call(){
               logger.info("副线程开始")
               //>>>>>>>>业务逻辑
               logger.info("副线程返回")    
               return "success";
           }
       }
       logger.info("主线程返回") 
       return result;
   }
   /**
   打印的日志为：
    主线程开始
    主线程返回
    副线程开始
    副线程返回
   */
   //这种异步处理方式的优势在于，tomcat的线程几乎在请求的同时就得到了释放，而所有的业务逻辑都在副线程中进行处理，副线程处理完后整个方法返回。所以在前端看来，处理的时间和同步处理所用的时间是一样的，但是这种方式迅速地释放了tomcat的线程，能承受更高的并发
   
   //这种方式的劣势在于  副线程的逻辑要写在主线程里面，在一些复杂的业务场景下无法满足需求，这时候就需要引入DeferredResult对象
   ```

   

   * 假设有这样一种业务场景，两个应用1和2，应用1接收到http请求后会通过线程1把这个请求发送到消息队列，应用2中有线程再监听消息队列，监听获取到请求后进行处理，处理完成后返回处理结果给消息队列，此时应用1有一个线程2在监听消息队列，从队列中获取到返回结果并使用线程2将结果返回给前端，在这样的场景下，线程1和线程2是完全隔离的，两者完全没有交互，并不知道对方的存在，在这样的场景下就需要用到 DeferredResult对象。

```java
//采用DeferredResult对象和消息中间件进行异步处理主要有三部分
/*
	1.线程1接收前端请求，将请求发送到消息中心
*/
//以下伪代码大概梳理了主线程的流程
public DefferedResult<String> order(){
    logger.info("主线程开始");
    //伪造一个字符串当作订单信息
    String orderNumber = RandomStringUtils.randomNumeric(8);
    //伪造一个消息队列，将订单信息发送到消息队列中
    queue.setPlaceOrder(orderNumber);
    
    //新建一个DeferredResult对象，这个对象是两个隔离线程进行异步通信的关键对象
    DeferredResult<String> result = new DeferredResult();
    //deferredResultHolder这个对象是spring的一个组件，用于在两个线程之间传递信息
    //在这个对象中是一个map，使用订单号做为key，每一个订单号代表一个订单，每个订单对应一个DeferredResult处理结果
    deferredResultHolder.getMap().put(orderNumber, result);
    
    logger.info("主线程结束");
    
    return result;
}

//除了以上主线程以外，还有一个线程在监听消息队列，一旦消息队列有返回值，就把返回值设置到deferredResultHolder这个对象中


//以上就是异步+消息中间件处理订单的伪代码
```

