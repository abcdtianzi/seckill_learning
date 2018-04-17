package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * Created by ting on 2017/9/5.
 */
public interface SeckillService {

    /**
     * 获取所有秒杀记录
     * @return
     */
    List <Seckill> getSeckillList();

    /**
     * 查询单个记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 输出秒杀地址，没到时间输出系统时间和秒杀时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);


    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException,SeckillCloseException;

    /**
     * 执行秒杀操作 by 存储过程
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);
}
