import common.PropertyUtil;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 12/4/2019
 */
public class Main {

    public static void main(String[] args) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(XmlParserJob.class)
                .withIdentity("myJob","myGroup").build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("mtTrigger", "myGroup").startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(Integer.parseInt(PropertyUtil.getPropValue("XmlParserJobGapTime"))).repeatForever()).build();
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
