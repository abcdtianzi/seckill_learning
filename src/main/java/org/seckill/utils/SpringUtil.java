package org.seckill.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by ting on 2018/1/11.
 */
@Component
public class SpringUtil implements ApplicationContextAware {


    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContexts) throws BeansException {
            if(applicationContext == null){
                applicationContext = applicationContexts;
            }
        }

    public static void getBeansName() {
        String[] beans = applicationContext.getBeanDefinitionNames();
        for (String bean : beans) {
            System.out.println("------------***********************------------");
            System.out.println(bean);
        }
    }
}
