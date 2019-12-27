package edf.xmlparser.job;

import edf.xmlparser.common.FileUtils;
import edf.xmlparser.common.PropertyUtil;
import edf.xmlparser.common.ZipTools;
import edf.xmlparser.entity.PropsStr;
import edf.xmlparser.fileparse.SDIFileInsertProcessor;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.File;
import java.util.List;

/**
 * @Description:  excute cycle by time
 * @Author: nianjie.chen
 * @Date: 10/7/2019
 */
public class XmlParserJob implements Job {
    private static Logger logger = Logger.getLogger(XmlParserJob.class);

    @Test
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        SDIFileInsertProcessor processor = new SDIFileInsertProcessor();
        Long start = System.currentTimeMillis();
        ZipTools.unzipFolder(PropertyUtil.getPropValue(PropsStr.WorkPath));
        //FileUtils.moveGzFiles(PropertyUtil.getPropValue(PropsStr.WorkPath), PropertyUtil.getPropValue(PropsStr.GzFileAchievePath));
        FileUtils.deleteGzFiles(PropertyUtil.getPropValue(PropsStr.WorkPath));
        List<File> files = FileUtils.getLocalAbsFiles(PropertyUtil.getPropValue(PropsStr.WorkPath));
        processor.process(files, PropertyUtil.getPropValue(PropsStr.FileAchievePath));
        Long end = System.currentTimeMillis();
        logger.info("sum of cost time:" + (end - start) + "ms");
    }
}
