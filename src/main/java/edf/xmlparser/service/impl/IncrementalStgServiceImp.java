package edf.xmlparser.service.impl;

import edf.xmlparser.common.IQMLogUtil;
import edf.xmlparser.common.OracleConnection;
import edf.xmlparser.common.PropertyUtil;
import edf.xmlparser.entity.Entity;
import edf.xmlparser.entity.IncrementalStg;
import edf.xmlparser.entity.ProcessBatchQueues;
import edf.xmlparser.service.IncrementalStgService;
import edf.xmlparser.common.exception.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/28/2019
 */
public class IncrementalStgServiceImp implements IncrementalStgService {

    private static final String INSERT_SQL = "INSERT INTO RDC_INCREMENTAL_STG VALUES(RDC_INCR_STG_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?)";
    private IQMLogUtil iqmLogUtil = IQMLogUtil.getSingleton();

    @Override
    public void insert(IncrementalStg incrementalStg) {

    }

    @Override
    public void insertByBatch(List<Entity> entityList, Integer batchIndex, String uuid, String fileName) {
        if (entityList == null || batchIndex == null) {
            throw new BaseException("insertByBatch variable empty.");
        }
        List<IncrementalStg> incList;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = OracleConnection.getConnection();
            con.setAutoCommit(false);
            pst = con.prepareStatement(INSERT_SQL);
            for (Entity en : entityList) {
                incList = en.getPropertyList();
                pst.setLong(1, en.getPi());
                pst.setString(3, en.getType());
                pst.setString(4, en.getSubtype());
                pst.setString(5, en.getRcssubtype());
                pst.setString(6, en.getEvent());
                for (IncrementalStg inc : incList) {
                    pst.setString(2, inc.getVersion());
                    pst.setLong(7, inc.getProperty_id());
                    pst.setString(8, inc.getCurrent_value());
                    pst.setString(9, inc.getClassifier_type());
                    pst.setString(10, inc.getValid_from());
                    pst.setString(11, inc.getValid_from_inc_time());
                    pst.setString(12, inc.getValid_to());
                    pst.setString(13, inc.getValid_to_inc_time());
                    pst.setString(14, inc.getLanguage());
                    pst.setString(15, uuid);
                    pst.setInt(16, batchIndex);
                    pst.setString(17, inc.getCreate_by());
                    pst.addBatch();
                    ProcessBatchQueues.insertPropertyNum.getAndIncrement();
                }
                ProcessBatchQueues.insertEntityNum.getAndIncrement();
            }
            pst.executeBatch();
            con.commit();
        } catch (SQLException e) {
            iqmLogUtil.logging("ERROR", OracleConnection.getUser(), "PROPERTIES INSERT BY BATCH",
                    "FileName:" + fileName +"  || UUID:" + uuid +"  ||  Batch Index:" + batchIndex,
                    new Date(System.currentTimeMillis()));
            e.printStackTrace();
        } finally {
            OracleConnection.close(pst, con);
        }
    }
}
