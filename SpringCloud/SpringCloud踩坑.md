SpringCloud踩坑

1. 服务远程调用时，采用重写Feign类的方式定义fallback时，如果被重写的接口有注解，一定要给重写方法加上相同的注解，否则会导致远程调用失败。

