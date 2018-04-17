package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;


/**
 * Created by ting on 2017/8/15.
 */
//    配置spring和junit整合，junit启动时加载spring ioc容器
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit  spring配置文件位置
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    @Autowired
    public SeckillDao seckillDao;

    @Test
    public void testReduceNumber() throws Exception {
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000, killTime);
        System.out.println(updateCount);
    }

    @Test
    public void testQueryById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill);
    }

    @Test
    public void testQueryAll() throws Exception {
        List<Seckill> seckills =  seckillDao.queryAll(0, 100);
        System.out.println(seckills);
        for (Seckill seckill : seckills){
            System.out.println(seckill);
        }
    }
}