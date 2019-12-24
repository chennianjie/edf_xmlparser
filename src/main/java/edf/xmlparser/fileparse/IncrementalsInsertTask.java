package edf.xmlparser.fileparse;

import edf.xmlparser.common.IQMLogUtil;
import edf.xmlparser.common.OracleConnection;
import edf.xmlparser.entity.Entity;
import edf.xmlparser.entity.IncrementalStg;
import edf.xmlparser.entity.ProcessBatchQueues;
import edf.xmlparser.common.PropertyUtil;
import edf.xmlparser.common.TimeTools;
import edf.xmlparser.entity.PropsStr;
import edf.xmlparser.fileparse.staxparse.ParseXmlByStaxThread;
import edf.xmlparser.service.IncrementalStgService;
import edf.xmlparser.service.RdcFileBatchService;
import edf.xmlparser.service.impl.IncrementalStgServiceImp;
import edf.xmlparser.service.impl.RdcFileBatchServiceImp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Description: import to database
 * @Author: nianjie.chen
 * @Date: 9/30/2019
 */
public class IncrementalsInsertTask implements Runnable {

    private final String PDP_END = "PDP_END";
    private String fileName;
    private String uuid;
    private Connection connection;
    private Connection arrayConnection;
    private CountDownLatch endControl;
    private IncrementalStgService incrementalStgServiceImp = new IncrementalStgServiceImp();
    private RdcFileBatchService rdcFileBatchServiceImp = new RdcFileBatchServiceImp();
    private Integer batchIndex;
    private IQMLogUtil iqmLogUtil = IQMLogUtil.getSingleton();
    private Integer batchNum = Integer.parseInt(PropertyUtil.getPropValue(PropsStr.BatchNumber));

    public IncrementalsInsertTask() {
    }

    public IncrementalsInsertTask(String fileName, String uuid, Connection connection, Connection arrayConnection, CountDownLatch endControl) {
        this.fileName = fileName;
        this.uuid = uuid;
        this.connection = connection;
        this.arrayConnection =arrayConnection;
        this.endControl = endControl;
    }

    @Override
    public void run() {
        //get data from queue by batch then insert into DB
        boolean done = false;
        List<Entity> entityList = new ArrayList<>();
        Entity entity;
        try {
            while (!done){
                if (ProcessBatchQueues.EntityQueue.size() == 0) {
                        TimeTools.ms(Integer.valueOf(PropertyUtil.getPropValue("InsertThreadGapTime")));
                }
                entity = ProcessBatchQueues.EntityQueue.take();
                if (entity == ParseXmlByStaxThread.getDUMMY()) {
                    ProcessBatchQueues.EntityQueue.put(entity);
                    done = true;
                }else {
                    entityList.add(entity);
                }
                if (entityList.size() == batchNum){
                    batchIndex = SDIFileInsertProcessor.batchIndex.getAndIncrement();
                    rdcFileBatchServiceImp.insert(uuid, batchIndex);
                    incrementalStgServiceImp.insertByBatch(entityList, batchIndex, uuid, fileName);
                    rdcFileBatchServiceImp.updateState(uuid, batchIndex, PDP_END);
                    entityList.clear();
                }

            }

            if (!entityList.isEmpty()) {
                batchIndex = SDIFileInsertProcessor.batchIndex.get();
                rdcFileBatchServiceImp.insert(uuid, batchIndex);
                incrementalStgServiceImp.insertByBatch(entityList, SDIFileInsertProcessor.batchIndex.getAndIncrement(), uuid, fileName);
                rdcFileBatchServiceImp.updateState(uuid, batchIndex, PDP_END);
                entityList.clear();
            }

        } catch (InterruptedException e) {
            iqmLogUtil.logging("ERROR", OracleConnection.getUser(), "IncrementalQueue Take",
                    "FileName:" + fileName +"  || UUID:" + uuid,
                    new Date(System.currentTimeMillis()));
            try {
                done = true;
                ProcessBatchQueues.EntityQueue.put(ParseXmlByStaxThread.getDUMMY());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            Thread.interrupted();
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (arrayConnection != null) {
                    arrayConnection.close();
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
            //endControl change status
            endControl.countDown();
        }

    }
}
