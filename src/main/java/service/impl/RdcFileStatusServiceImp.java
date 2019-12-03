package service.impl;

import common.IQMLogUtil;
import common.OracleConnection;
import common.TimeTools;
import common.exception.*;
import service.IqmConfigService;
import service.RdcFileStatusService;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/28/2019
 */
public class RdcFileStatusServiceImp implements RdcFileStatusService {

    private IQMLogUtil singleton = IQMLogUtil.getSingleton();

    private IqmConfigService iqmConfigService = new IqmConfigServiceImp();

    private static final String INSERT_SQL = "INSERT INTO RDC_file_status(id,FILE_NAME,BPM_batch_guid,FILE_TYPE,SEQUENCE_NUMBER,ingestion_state,START_DATETIME)VALUES" +
                                             "(RDC_FILE_BATCH_SEQ.nextval, ?, ?, 'INCREMENTAL', ?, 'PDP_RUNNING', sysdate)";

    private static final String UPDATE_SQL = "UPDATE RDC_file_status b SET b.ingestion_state = ?, b.end_datetime = sysdate WHERE b.BPM_batch_guid = ?";

    @Override
    public void insert(String fileName, String uuid) throws ParseException{
        if (fileName == null || uuid == null) {
            throw new BaseException("rdcFileStatus variable empty.");
        }
        singleton.logInit("PDP_STATUS", "PDP_STATUS","PDP_STATUS","PDP_STATUS");
        int seqNum = iqmConfigService.getSequenceNum();
        if (seqNum == -1) {
            throw new BaseException("seqNum variable empty.");
        }
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = OracleConnection.getConnection();
            pst = con.prepareStatement(INSERT_SQL);
            pst.setString(1, fileName);
            pst.setString(2, uuid);
            pst.setInt(3, seqNum);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            singleton.logging("INFO", OracleConnection.getUser(), "PDP_STATUS",
                    "FileName:" + fileName +"  || UUID:" + uuid +"  ||  Start Time:" + TimeTools.getCurrTime("yyyy-MM-dd HH:mm:ss"),
                    (java.sql.Date) new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(TimeTools.getCurrTime("yyyy-MM-dd HH:mm:ss")));

            OracleConnection.close(pst, con);
        }
    }

    @Override
    public void updateStateByUUId(String state, String uuid) {
        if (state == null || uuid == null) {
            throw new BaseException("state or uuid variable empty.");
        }
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = OracleConnection.getConnection();
            statement = con.prepareStatement(UPDATE_SQL);
            statement.setString(1, state);
            statement.setString(2, uuid);
            statement.execute();
            if (state == "EndPDP") {
                iqmConfigService.updateSequenceNum(iqmConfigService.getSequenceNum()+1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            OracleConnection.close(statement, con);
        }
    }
}
