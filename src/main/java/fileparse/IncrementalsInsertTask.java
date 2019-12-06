package fileparse;

import common.IQMLogUtil;
import common.OracleConnection;
import entity.IncrementalStg;
import entity.ProcessBatchQueues;
import common.PropertyUtil;
import entity.PropsStr;
import common.TimeTools;
import fileparse.saxparse.ParseXMLBySaxThread;
import oracle.jdbc.OracleCallableStatement;
import service.IncrementalStgService;
import service.RdcFileBatchService;
import service.impl.IncrementalStgServiceImp;
import service.impl.RdcFileBatchServiceImp;

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
    private static int batchNum = Integer.parseInt(PropertyUtil.getPropValue(PropsStr.BatchNumber));
    private String fileName;
    private String uuid;
    private Connection connection;
    private Connection arrayConnection;
    private OracleCallableStatement proc;
    private CountDownLatch endControl;
    private IncrementalStgService incrementalStgServiceImp = new IncrementalStgServiceImp();
    private RdcFileBatchService rdcFileBatchServiceImp = new RdcFileBatchServiceImp();
    private Integer batchIndex;
    private IQMLogUtil iqmLogUtil = IQMLogUtil.getSingleton();

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
        List<IncrementalStg> incList = new ArrayList<>();
        try {
            while (!done){
                if (ProcessBatchQueues.IncrementalQueue.size() == 0) {
                        TimeTools.ms(Integer.valueOf(PropertyUtil.getPropValue("InsertThreadGapTime")));
                }
                IncrementalStg stg = ProcessBatchQueues.IncrementalQueue.take();
                if (stg == ParseXMLBySaxThread.getDUMMY()) {
                    ProcessBatchQueues.IncrementalQueue.put(stg);
                    done = true;
                }else {
                    incList.add(stg);
                }
                if (incList.size() == batchNum){
                    batchIndex = SDIFileInsertProcessor.batch_index.getAndIncrement();
                    rdcFileBatchServiceImp.insert(uuid, batchIndex);
                    incrementalStgServiceImp.insertByBatch(incList, batchIndex, uuid, fileName);
                    rdcFileBatchServiceImp.updateState(uuid, batchIndex, PDP_END);
                    ProcessBatchQueues.insertNum.addAndGet(batchNum);
                    incList.clear();
                }
            }

            if (!incList.isEmpty()) {
                batchIndex = SDIFileInsertProcessor.batch_index.get();
                rdcFileBatchServiceImp.insert(uuid, batchIndex);
                incrementalStgServiceImp.insertByBatch(incList, SDIFileInsertProcessor.batch_index.getAndIncrement(), uuid, fileName);
                rdcFileBatchServiceImp.updateState(uuid, batchIndex, PDP_END);
                ProcessBatchQueues.insertNum.addAndGet(incList.size());
                incList.clear();
            }
        } catch (InterruptedException e) {
            iqmLogUtil.logging("ERROR", OracleConnection.getUser(), "IncrementalQueue Take",
                    "FileName:" + fileName +"  || UUID:" + uuid,
                    new Date(System.currentTimeMillis()));
            try {
                done = true;
                ProcessBatchQueues.IncrementalQueue.put(ParseXMLBySaxThread.getDUMMY());
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
