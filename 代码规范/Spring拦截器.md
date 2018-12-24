# Spring拦截器

1. 拦截器的概念：

2. 拦截器通过继承HandlerInterceptorAdapter实现，重写其中的三个方法实现程序的拦截需求；拦截器的调用逻辑如下

    拦截器在业务处理器处理请求之前被调用  
        * 如果返回false  
                  *  从**当前**的拦截器**往回执行**所有拦截器的afterCompletion(),再退出拦截器链 
        * 如果返回true  
             ​    执行下一个拦截器,直到所有的拦截器都执行完毕 ,再执行被拦截的Controller  
        * 然后进入拦截器链,  
                  *    从最后一个拦截器往回执行所有的postHandle()  
                  *    接着再从最后一个拦截器往回执行所有的afterCompletion()

3. 在这个拦截器的逻辑中，只有拦截器链中的所有preHandle都返回true时，才会执行postHandle，而afterCompletion不管preHandle是否返回true都会执行。
4. postHandle和afterCompletion的执行时机
   1. postHandle在业务处理器处理请求执行完成后,生成视图之前执行的动作。可以用于在返回的视图中添加信息，比如在modelAndView中加入数据，比如当前时间
   2. 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()。