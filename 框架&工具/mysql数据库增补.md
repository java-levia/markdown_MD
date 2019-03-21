#mysql数据库###

1. sql性能下降排查


		* 索引失效的情况
			1. 如果查询条件中有or，即使其中有条件带索引页不会使用（索引尽量少用or）（要想使用or同时索引也生效，只有将or条件中的每个列都加上索引。）
			2. 对于复合索引，必须使用到改索引中的第一个字段作为条件时才能保证系统使用该索引。
			3. like的模糊查询使用%开头，索引失效
			4. 如果列类型时字符串，查询条件中一定得将数据使用引号引起来，否则不会使用索引。
			5. 如果mysql预计使用全表扫描比索引快，则不使用索引
			6.     show status like ‘Handler_read%’;
				    大家可以注意：
				    handler_read_key:这个值越高越好，越高表示使用索引查询到的次数
				    handler_read_rnd_next:这个值越高，说明查询低效

2. 函数
   1. TO_DAYS(日期)会返回一个从年份0开始的天数（可以在数据库中操作两个日期之间的天数）
   2. INTERVAL 1 DAY / INTERVAL 1 HOUR 等 ，就是取间隔的含义比如  NOW() - INTERVAL  1 DAY的含义就是一天之前的这个时间点。
   3. DATE_FORMAT(日期，格式)；

3. mybatis批量更新数据sql拼接

```sql
UPDATE yoiurtable
    SET dingdan = CASE id 
        WHEN 1 THEN 3 
        WHEN 2 THEN 4 
        WHEN 3 THEN 5 
    END
WHERE id IN (1,2,3)
//这种批量更新方式在mapper中可以使用<trim>标签和<foreach>标签进行循环，在不需要版本控制的情况下批量更新比较有效率，但是如果有version版本控制则无法使用
<update id="updateBatch" parameterType="java.util.List">
        update mydata_table
        <trim prefix="set" suffixOverrides=",">
            <trim prefix="status =case" suffix="end,">
                 <foreach collection="list" item="item" index="index">
                     <if test="item.status !=null and item.status != -1">
                         when id=#{item.id} then #{item.status}
                     </if>
                     <if test="item.status == null or item.status == -1">
                         when id=#{item.id} then mydata_table.status//原数据
                     </if>
                 </foreach>
            </trim>
        </trim>
        where id in
        <foreach collection="list" index="index" item="item" separator="," open="(" close=")">
            #{item.id,jdbcType=BIGINT}
        </foreach>
    </update>

```

