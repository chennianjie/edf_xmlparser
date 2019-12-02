package fileparse;

import common.*;
import entity.ProcessBatchQueues;
import entity.PropsStr;
import common.exception.DBFeedException;
import fileparse.saxparse.ParseXMLBySaxThread;
import fileparse.staxparse.ParseXmlByStaxThread;
import org.apache.log4j.Logger;
import service.RdcFileStatusService;
import service.impl.RdcFileStatusServiceImp;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 9/30/2019
 */
public class SDIFileInsertProcessor implements IFeedFileProcessor {

    private static Logger logger = Logger.getLogger(SDIFileInsertProcessor.class);
    private Connection DBConnection;
    public static AtomicInteger batch_index = null;
    public CountDownLatch endControl;
    private RdcFileStatusService rdcFileStatusService = new RdcFileStatusServiceImp();

    @Override
    public void process() {
        List<File> files = FileUtils.getLocalAbsFiles(PropertyUtil.getPropValue(PropsStr.WorkPath));
        String fileType = PropertyUtil.getPropValue(PropsStr.FileType);
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
                    this.DBConnection = OracleConnection.getConnection();
//                    insertFileStatus(this.DBConnection, uuid, insertFile.getName(), "StartPDP");
                    rdcFileStatusService.insert(insertFile.getName(), uuid);
                    this.DBConnection.close();
                    Thread parseXmlThread = new Thread(new ParseXmlByStaxThread(insertFile, uuid));
                    parseXmlThread.setPriority(10);
                    parseXmlThread.start();
                    //start thread deal data in queue
                    insertThreadNum = Integer.parseInt(PropertyUtil.getPropValue(PropsStr.InsertThreadNum));
                    this.endControl = new CountDownLatch(insertThreadNum);
                    logger.info("insert DB thread number{}" + insertThreadNum);
                    while (ProcessBatchQueues.IncrementalQueue.size() == 0) {
                        SleepTools.ms(5000);
                    }
                    for (int i = 0; i < insertThreadNum; i++) {
                        new Thread(new IncrementalsInsertTask(fileName, uuid, OracleConnection.getConnection(), OracleConnection.getConnection(), endControl)).start();
                    }
                    //wait util this file analysis is complete then change status
                    endControl.await();
                    this.DBConnection = OracleConnection.getConnection();
                    logger.info("property's number of parsed "+ fileName + "：" + ProcessBatchQueues.parseNum);
                    logger.info("insert to DB nums：" + ProcessBatchQueues.insertNum);
                    if (ProcessBatchQueues.IncrementalQueue.size() == 1 && ProcessBatchQueues.IncrementalQueue.take() == ParseXMLBySaxThread.getDUMMY()) {
                        logger.info("=========parse success filename{}" + fileName +"  ||  uuid{}" + uuid +"========");
                    }
                    FileUtils.moveAndRenameFile(insertFile, PropertyUtil.getPropValue(PropsStr.FileAchievePath), uuid);
                    rdcFileStatusService.updateStateByUUId("EndPDP", uuid);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        DBConnection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void init() {
        ProcessBatchQueues.insertNum = new AtomicInteger(0);
        ProcessBatchQueues.parseNum = new AtomicInteger(0);
        batch_index = new AtomicInteger(1);
    }


    private void insertFileStatus(Connection dbConnection2, String uuid_file, String name, String string) {
        CallableStatement cs = null;
        try {
            cs = dbConnection2.prepareCall("call RDC_FILE_status_PRC(?,?,?)");
            cs.setString(1, uuid_file);
            cs.setString(2, name);
            cs.setString(3, string);
            cs.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBFeedException(e.getMessage());
        } finally {
            try {
                if (cs != null && !cs.isClosed()) {
                    cs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DBFeedException(e.getLocalizedMessage());
            }
        }
    }


}
