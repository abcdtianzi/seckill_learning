package org.seckill.dao;


import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ting on 2017/8/6.
 */
public interface SeckillDao {
    /**
     * 减库存
     * @param id
     * @param killTime
     * @return
     */
    int reduceNumber(@Param("id") long id,@Param("killTime") Date killTime);

    /**
     * 根据ID找秒杀对象
     * @param id
     * @return
     */
    Seckill queryById(long id);


    /**
     * 查询所有对象
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset,@Param("limit") int limit);


    /**
     * 使用存储过程执行秒杀
     * @param paramMap
     */
    void killByProcedure(Map<String, Object> paramMap);

}
