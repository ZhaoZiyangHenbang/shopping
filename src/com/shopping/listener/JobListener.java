package com.shopping.listener;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by HuifengWang on 2017/7/4.
 */
public class JobListener implements ServletContextListener {

        public void contextDestroyed(ServletContextEvent arg0) {
            Scheduler job = UtilsSpringContext.getBean("quartzScheduler");
            try {
                if(job.isStarted()){
                    job.shutdown();
                    Thread.sleep(1000);
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        public void contextInitialized(ServletContextEvent arg0) {


        }
}
