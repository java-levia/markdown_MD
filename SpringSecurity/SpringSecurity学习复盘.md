SpringSecurity学习复盘

1. 善用@ConfigurationProperties注解，将部分可以自定义的内容放到配置文件中进行配置；
2. 使用@ConditionOnMissingBean注解，可以定义一个默认的实现，当系统没有其他实现时默认使用这个实现，如果系统有其他实现，则使用另外的实现。
3. 使用@Bean注解可以将Bean交给Spring容器管理
4. 

