package org.seckill.dao;


import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * Created by ting on 2017/8/6.
 */
public interface SuccessKilledDao {

    /**
     * 插入购买明细，可过滤重复
     * @param seckillId
     * @param userPhone
     * @return 返回影响行数
     */
    int insertSuccessKilled(@Param("id") long seckillId,@Param("userPhone") long userPhone);

    /**
     * 根据id查询SuccessKIlled并携带秒杀产品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("id") long seckillId,@Param("userPhone") long userPhone);


}
