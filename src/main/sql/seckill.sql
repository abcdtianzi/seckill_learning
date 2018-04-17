--秒杀执行存储过程
DELIMITER $$ --console  ;转换为$$

--定义存储过程,in 表示输入参数，out代表输出参数
--row_count()返回上一条修改类型sql（delete，update，insert）的影响行数
--row_count():0:未修改数据，>0表示修改的行数 ；<0表示未执行或者报错

CREATE PROCEDURE  `seckill`.`execute_seckill`(
  in v_seckill_id bigint,in v_phone bigint,
  in v_kill_time TIMESTAMP ,out r_result int)
  BEGIN
  DECLARE insert_count INT DEFAULT 0;
  START TRANSACTION;
  INSERT ignore INTO success_killed
  (id,user_phone,create_time)
  VALUES (v_seckill_id,v_phone,v_kill_time);
  SELECT ROW_COUNT() INTO insert_count;
  IF (insert_count = 0) THEN
  ROLLBACK ;
  SET  r_result = -1;
  ELSEIF(insert_count < 0)THEN
  ROLLBACK ;
  SET R_RESULT = -2;
  ELSE
    UPDATE  seckill
    SET number = number-1
    WHERE id = v_seckill_id
      AND end_time > v_kill_time
      AND start_time < v_kill_time
      AND number > 0;
    SELECT ROW_COUNT () INTO insert_count;
    IF (insert_count  = 0) THEN
      ROLLBACK ;
      SET r_result = 0;
    ELSEIF (insert_count < 0 ) THEN
      ROLLBACK ;
      SET r_result = -2;
    ELSE
      COMMIT ;
      SET r_result = 1;
    END  IF;
   END IF;
  END
$$  --换行


--存储过程定义结束


DELIMITER ;
SET @r_result = -3;
--执行存储过程
call execute_seckill(1003,13133322323,now(),@r_result);
--获取结果
SELECT @r_result;

--存储过程优化：事务行级锁持有的时间，本地数据库执行效率高
--简单的逻辑可以应用存储过程
--QPS：一个秒杀单6000／qps