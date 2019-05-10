消息中间件RabbitMQ

1. 消息队列的应用场景
   * 异步处理
   * 应用解耦
   * 流量削峰
   * 日志处理等

2. 配置用户（可以用命令行配置也可以在网页的控制台进行配置）
   * 添加/删除用户
     * 添加用户 rabbitmqctl  add_user  Username  Password  新添加的用户是无法登陆控制台的，需要进行授权
     * 删除用户 rabbitmqctl  delete_user  Username
     * 修改用户密码 rabbitmqctl  change_password  Username  Newpassword
     * 查看当前用户列表 rabbitmqctl  list_users
   * 用户授权
     * rabbitmqctl  set_user_tags  User  Tag   user是用户名  Tag是角色  （角色由  administrator，monitoring，policymaker，management等）

