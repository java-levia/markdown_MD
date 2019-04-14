Spring中的一些好用的注解

1. 视图注解@JsonView  可以用来控制Json输出的内容

   1. 在实体类中使用接口声明多个视图

      ```java
      public class User{
          //申明简单视图
          public interface UserSimpleView{};
          
          public interface UserDetailView extends UserSimpleView{};
          
          
      }
      ```

      

   2. 在实体类中的get方法上指定视图

      ```java
      {
          @JsonView(UserSimpleView.class)
          public String getUsername(){
              return username;
          }
          
          //由于UserDetailView视图继承了UserSimpleView视图，所以也会展示属于UserSimpleView的内容
          @JsonView(UserDetailView.class)
          public String getPassword(){
              return password;
          }
      }
      ```

   3. 还需要在Controller方法上指定视图

      ```java
      //通过指定视图，可以控制只返回指定视图的属性
      @JsonView(User.UserSimpleView.class)
      public User query("这是一个模拟查找列表的请求"){
          
      }
      ```

2. 日期类型参数的处理

   * 遇到日期类型的参数，将数据处理成时间戳的形式返回给前端

     

3. @valid注解和BindingResult验证请求参数的合法性并处理校验结果

   * 在实体类中使用@NotBlank可以做非空校验，但是要这个注解生效，还需要在形参上注解@valid才会生效
   * 在仅仅使用@valid注解的情况下，如果参数不满足需求，请求无法进入到方法内部就返回错误信息了，但是有时候我们需要对错误信息做一些记录，就需要进到方法中，这时候就可以使用BindingResult对象，在我们的请求参数不满足条件的时候，请求的错误信息会被保存在BindingResult对象中，这时候可以使用BindingResult的一些方法对返回的错误信息进行记录

   ```java
   //在参数中引入BindingResult对象
   public User create(@Valid @RequestBody User user， BindingResult errors){
       if(errors.hasErrors()){
           errors.getAllErrors().stream().foreach(error->System.out.println(error.getDefaultMessage()))
       }
   }
   ```

   * @Valid注解是属于Hibernate Validator类，其中除了@NotBlank这个注解以外还有很多其他的注解，可以用来校验格式、大小、日期、html和银行卡号等等