sql技巧

1. ```sql
   --sql  分割一个字段进行统计
   SELECT t.UNIT_NAME, count(*) FROM (select pkid,
   regexp_substr(UNIT_NAME, '[^,]+', 1, level) UNIT_NAME
   from FS_UNIT_INFO
   connect by level <= regexp_count(UNIT_NAME, ',') + 1
   and pkid = prior pkid
   and prior dbms_random.value is not NULL) t GROUP BY t.UNIT_NAME
   ```

   

2. ```sql
   --根据某个字段分组然后取最大的一条
   select  listid,recid   
   from ( select listid,recid,row_number() over(partition by listid order by    recid desc) rn  
   from mo_partprg)   t1 where rn=1;  
   --row_number() OVER(partition by tm.bj order by tm.cou desc)通过tm.bj进行结果内分组，分组后按照tm.cou进行排序,组内排序后再加上一个组内行号（这样可以按照组内的行号取组内的123...行）
   
   --RANK() OVER(partition by tm.bj order by tm.cou desc) as rank   通过tm.bj进行结果内分组，分组后按照tm.cou进行排序，组内排序后再做Rank（tm.cou相同的为同一个rank，通过rank可以取到同为最大值的多行）
   ```

   