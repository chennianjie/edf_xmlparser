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
    public static AtomicInteger batchIndex;
    private RdcFileStatusService rdcFileStatusService = new RdcFileStatusServiceImp();
    private String fileType = PropertyUtil.getPropValue(PropsStr.FileType);
    private int insertThreadNum;

    public void process(List<File> files, String fileAchievePath) {
        if (files == null || files.size()<=0) {
            return;
        }
        FileUtils.showFileName(files);
        String uuid;
        String fileName;
        CountDownLatch endControl;
        for (File insertFile : files) {
            fileName= insertFile.getName();
            if (fileType != null && fileType.equals(FileUtils.getFileType(fileName))) {
                try {
                    this.init();
                    uuid = UUID.randomUUID().toString();
                    logger.info("=========parsing filename{}" + fileName + "  ||  uuid{}" + uuid +"========");
                    logUtil.logInit("PDP_STATUS", "PDP_STATUS","PDP_STATUS","PDP_STATUS");
                    rdcFileStatusService.insert(insertFile.getName(), uuid);
                    new Thread(new ParseXmlByStaxThread(insertFile, uuid)).start();
                    //start thread deal data in queue
                    insertThreadNum = Integer.parseInt(PropertyUtil.getPropValue(PropsStr.InsertThreadNum));
                    endControl = new CountDownLatch(insertThreadNum);
                    logger.info("insert DB thread number{}" + insertThreadNum);
                    while (ProcessBatchQueues.EntityQueue.size() == 0) {
                        TimeTools.ms(3000);
                    }
                    for (int i = 0; i < insertThreadNum; i++) {
                        new Thread(new IncrementalsInsertTask(fileName, uuid, OracleConnection.getConnection(), OracleConnection.getConnection(), endControl)).start();
                    }
                    //wait util this file analysis is complete then change status
                    endControl.await();
                    logger.info("insert to DB entity nums：" + ProcessBatchQueues.insertEntityNum);
                    logger.info("insert to DB property nums：" + ProcessBatchQueues.insertPropertyNum);
                    if (ProcessBatchQueues.EntityQueue.size() == 1 && ProcessBatchQueues.EntityQueue.take() == ParseXmlByStaxThread.getDUMMY()) {
                        logger.info("=========parse end filename{}" + fileName +"  ||  uuid{}" + uuid +"========");
                    }
                    FileUtils.moveAndRenameFile(insertFile, fileAchievePath, uuid);
                    // TODO: 12/24/2019 如果有错误数据，或者没有完全入库，是否需要更新状态
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
        ProcessBatchQueues.insertPropertyNum = new AtomicInteger(0);
        ProcessBatchQueues.insertEntityNum = new AtomicInteger(0);
        ProcessBatchQueues.parsePropertyNum = new AtomicInteger(0);
        ProcessBatchQueues.parseEntityNum = new AtomicInteger(0);
        batchIndex = new AtomicInteger(1);
    }

}
