package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.seckill.utils.SpringUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by ting on 2017/9/17.
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController  {

    @Autowired
    private SeckillService seckillService;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private static ApplicationContext applicationContext;





    @RequestMapping(value = "/list",method = RequestMethod.GET,
            produces = {"application/json;charset=UTF-8"})
    public String list(Model model){
//        System.out.print(seckillService.getClass().getName());
//        SpringUtil.getBeansName();

        //获取列表页
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);
        return "list";

    }

    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId,  Model model){
        if(seckillId == null){
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if(seckillId == null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }


    //返回json
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable Long seckillId){
        SeckillResult<Exposer> result;

        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }


        return result ;
    }


    //CookieValue取cookie值，如果没此值sprinf mvc会报错，加上require = false，告诉mvc不是必须值，不会向框架报错
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
    method = RequestMethod.POST,
    produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone",required = false) Long phone){

        if(phone == null){
            return new SeckillResult<SeckillExecution>(false,"未注册");
        }
        SeckillResult<SeckillExecution> result;

        try {
            //普通
            //SeckillExecution execution = seckillService.executeSeckill(seckillId,phone, md5);
            //使用存储过程优化效率
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId,phone, md5);
           return new SeckillResult<SeckillExecution>(true,execution);
        } catch (RepeatKillException e){
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch (SeckillCloseException e){
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true,execution);

        }catch (Exception e) {
            logger.error(e.getMessage());
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,execution);
        }


    }


    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date date = new Date();
        return new  SeckillResult<Long>(true,date.getTime());
    }




}
