package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ting on 2017/9/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList() throws Exception {
        SpringUtil.getBeansName();
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list = {}", list);
    }

    @Test
    public void testGetById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("list = {}", seckill);

    }

    @Test
    public void testSeckillLogic() throws Exception {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        long phone = 13015181190L;
        //开始秒杀
        if (exposer.isExposed()) {
            logger.info("exposer = {}", exposer);
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, exposer.getMd5());
                logger.info("seckillExecution = {}", seckillExecution);
            } catch (RepeatKillException ex) {
                logger.error(ex.getMessage(), ex);
            } catch (SeckillCloseException e2) {
                logger.error(e2.getMessage(), e2);
            }
        } else {
            //秒杀未开启
            logger.warn("exposer = {}", exposer);
        }

    }


    @Test
    public void executeSeckillProcedure() {
        long seckillId = 1001;
        long phone = 13122234433l;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            logger.info(execution.getStateInfo());
        }
    }

}