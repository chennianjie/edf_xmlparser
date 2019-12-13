import edf.xmlparser.common.PropertyUtil;
import edf.xmlparser.job.XmlParserJob;
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
                .withIdentity("xmlParserJob","edf_parser").build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("xmlParserJobTrigger", "edf_parser").startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(Integer.parseInt(PropertyUtil.getPropValue("XmlParserJobGapTime"))).repeatForever()).build();
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
