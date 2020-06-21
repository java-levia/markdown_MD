Session占用处理，表锁定处理，清除表级联和添加表级联

1. session锁定查询：

   ```sql
   -- 查询当前session占用情况
   select l.session_id,o.owner,o.object_name from v$locked_object l,dba_objects o where l.object_id=o.object_id;
   -- 通过session查询sid个serial
   SELECT sid, serial#, username, oSUSEr, terminal,program ,action, prev_exec_start FROM v$session where sid = 8;
   --清除占用的session
   alter system kill session '8,18509';
   ```

   

2. 删除/添加表级联

   ```sql
   --删除表级联
   ALTER TABLE NB_T_ACCOUNT_RECHARGE_ORDER
   DROP CONSTRAINT PK_ACCOUNT_RECHARGE_ORDER_ID 
   
   
   --添加表级联
   
   
   
   
   
   
   1、ORACLE数据库中的外键约束名都在表user_constraints中可以查到。其中constraint_type='R'表示是外键约束。
   2、启用外键约束的命令为：alter table table_name enable constraint constraint_name 
   3、禁用外键约束的命令为：alter table table_name disable constraint constraint_name
   4、然后再用SQL查出数据库中所以外键的约束名：
   select 'alter table '||table_name||' enable constraint '||constraint_name||';' from user_constraints where constraint_type='R'
   select 'alter table '||table_name||' disable constraint '||constraint_name||';' from user_constraints where constraint_type='R'
   ```

3. oracle数据备份

   