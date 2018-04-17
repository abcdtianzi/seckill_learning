package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ting on 2017/9/5.
 */
@Service(value = "seckillServiceImpl")
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;



    //md5盐值字符串，用于混淆md5
    private final String slat = "asdjkaks12@&#&!9nsadh((^$";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //优化点：缓存优化，减少数据库连接
        /**
         * get from cache
         * if null
         *  get db
         * else
         *  put cache
         */
        //Seckill seckill = seckillDao.queryById(seckillId);
        //redis优化
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null){
            seckill = seckillDao.queryById(seckillId);
            if(seckill != null){
                redisDao.putSeckill(seckill);
            }else{
                return new Exposer(false, seckillId);
            }

        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime()
                ||nowTime.getTime()>endTime.getTime()){

            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());

        }
        String md5 = getMD5(seckillId);//不可逆
        return new Exposer(true,md5,seckillId);
    }

    /**
     * 执行秒杀
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
    throws SeckillException, RepeatKillException,SeckillCloseException{

        if(md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
//        执行秒杀逻辑：减库存+记录购买行为
        Date nowTime = new Date();
        try { //记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
            if(insertCount <= 0){
                //意味重复秒杀
                throw new RepeatKillException("seckill repeated");
            }else{
                //减库存
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);

                if(updateCount <= 0){
                    //没有更新记录操作
                    throw  new SeckillCloseException("seckill is closed");
                }else{
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
                }
            }

        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            //所有编译期异常转化为运行期异常，spring 认识到运行期异常会帮我们rollback
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }

    }

    private String getMD5(long seckillId){
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5){
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId, SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("id",seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        //执行存储过程，result被复制
        try {
            seckillDao.killByProcedure(map);
            //获取result,没有数据取-2
            int result = MapUtils.getInteger(map,"result",-2);
            if(result == 1){
                SuccessKilled sk = successKilledDao.
                        queryByIdWithSeckill(seckillId,userPhone);

                return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,sk);
            }else{
                return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
            }

        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
        }
    }


}
