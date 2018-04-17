package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by ting on 2017/8/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Autowired
    private SuccessKilledDao successKilledDao;
    @Test
    public void testInsertSuccessKilled() throws Exception {
        long id = 1000L;
        long phone = 13543439131L;
        int insertCount = successKilledDao.insertSuccessKilled(id,phone);
        System.out.println(insertCount);
    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception {
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(1000,1354343913L);
        System.out.println(successKilled);
    }
}