package com.zx.jdkill.test;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

/**
 * @author ZhaoXu
 * @date 2022/5/31 17:03
 */
@Slf4j
@Service
public class BaseQuartzManager {

    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

    public BaseQuartzManager() throws SchedulerException {
    }


    public void createJob(Class<? extends Job> jobClass, String jobName, String jobGroupName, String cronExpression, JSONObject params, boolean exeOnce) {
        // 创建scheduler，调度器, 策略采用错过之后立即执行一次
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionFireAndProceed();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName, jobGroupName)
                .startNow()
                .withSchedule(scheduleBuilder)
                .build();
        // 定义一个JobDetail
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroupName)
                .build();
        trigger.getJobDataMap().putAll(params);
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            // 启动任务调度
            scheduler.start();
        } catch (Exception e) {
            log.error("创建定时任务失败，jobName：{}，jobGroupName：{}", jobName, jobGroupName);
            throw new RuntimeException(e);
        }
        log.info("创建定时任务成功，jobName：{}，jobGroupName：{}", jobName, jobGroupName);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteJob(String jobName, String jobGroupName) {
        try {
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, jobGroupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroupName));
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
        } catch (Exception e) {
            log.error("删除定时任务失败，jobName：{}，jobGroupName：{}", jobName, jobGroupName);
        }
        log.info("删除定时任务成功，jobName：{}，jobGroupName：{}", jobName, jobGroupName);
    }
}
