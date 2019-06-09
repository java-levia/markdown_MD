SpringSocial  实现微信登陆需要注意的地方

1. 在Social的标准实现中，StringHttpMessageConverter字符集为ISO8859-1,与微信返回信息的UTF-8不同，  所以需要覆盖原来的方法将字符集修改为UTF-8

```JAVA
    /**
     * 这里需要覆盖一个方法，因为标准实现中默认注册的转换器字符集为ISO8859-1,而微信返回的用户信息字符集是UTF-8,所以这里需要覆盖原来的方法
     */
    @Override
    protected List<HttpMessageConverter<?>> getMessageConverters(){
        List<HttpMessageConverter<?>> messageConverters = super.getMessageConverters();
        messageConverters.remove(0);
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return messageConverters;
    }
```

2. 由于微信在实现OAuth协议的时候对封装的参数没有按照OAuth2.0的标准进行，所以在使用Social进行微信第三方登陆的开发时，需要对OAuth2Template中默认实现的一些方法进行重写，从而替换其中的参数名。

```java

```

