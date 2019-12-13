package edf.xmlparser.fileparse;

import edf.xmlparser.common.*;
import edf.xmlparser.entity.ProcessBatchQueues;
import edf.xmlparser.entity.PropsStr;
import edf.xmlparser.fileparse.staxparse.ParseXmlByStaxThread;
import org.apache.log4j.Logger;
import edf.xmlparser.service.RdcFileStatusService;
import edf.xmlparser.service.impl.RdcFileStatusServiceImp;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 9/30/2019
 */
public class SDIFileInsertProcessor{

    private static Logger logger = Logger.getLogger(SDIFileInsertProcessor.class);
    private IQMLogUtil logUtil = IQMLogUtil.getSingleton();
    public static AtomicInteger batchIndex = null;
    public CountDownLatch endControl;
    private RdcFileStatusService rdcFileStatusService = new RdcFileStatusServiceImp();
    private String fileType = PropertyUtil.getPropValue(PropsStr.FileType);

    public void process() {
        List<File> files = FileUtils.getLocalAbsFiles(PropertyUtil.getPropValue(PropsStr.WorkPath));
        if (files.size()<=0) {
            return;
        }
        FileUtils.showFileName(files);
        int insertThreadNum;
        String uuid;
        for (File insertFile : files) {
            String fileName = insertFile.getName();
            if (fileType != null && fileType.equals(FileUtils.getFileType(fileName))) {
                try {
                    this.init();
                    uuid = UUID.randomUUID().toString();
                    logger.info("=========parsing filename{}" + fileName + "  ||  uuid{}" + uuid +"========");
                    logUtil.logInit("PDP_STATUS", "PDP_STATUS","PDP_STATUS","PDP_STATUS");
                    rdcFileStatusService.insert(insertFile.getName(), uuid);
                    Thread parseXmlThread = new Thread(new ParseXmlByStaxThread(insertFile, uuid));
                    parseXmlThread.start();
                    //start thread deal data in queue
                    insertThreadNum = Integer.parseInt(PropertyUtil.getPropValue(PropsStr.InsertThreadNum));
                    this.endControl = new CountDownLatch(insertThreadNum);
                    logger.info("insert DB thread number{}" + insertThreadNum);
                    while (ProcessBatchQueues.EntityQueue.size() == 0) {
                        TimeTools.ms(5000);
                    }
                    for (int i = 0; i < insertThreadNum; i++) {
                        new Thread(new IncrementalsInsertTask(fileName, uuid, OracleConnection.getConnection(), OracleConnection.getConnection(), endControl)).start();
                    }
                    //wait util this file analysis is complete then change status
                    endControl.await();
                    logger.info("property's number of parsed "+ fileName + "：" + ProcessBatchQueues.parseNum);
                    logger.info("insert to DB entity nums：" + ProcessBatchQueues.insertNum);
                    if (ProcessBatchQueues.EntityQueue.size() == 1 && ProcessBatchQueues.EntityQueue.take() == ParseXmlByStaxThread.getDUMMY()) {
                        logger.info("=========parse success filename{}" + fileName +"  ||  uuid{}" + uuid +"========");
                    }
                    FileUtils.moveAndRenameFile(insertFile, PropertyUtil.getPropValue(PropsStr.FileAchievePath), uuid);
                    rdcFileStatusService.updateStateByUUId("EndPDP", uuid, fileName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void init() {
        ProcessBatchQueues.insertNum = new AtomicInteger(0);
        ProcessBatchQueues.parseNum = new AtomicInteger(0);
        batchIndex = new AtomicInteger(1);
    }

}
