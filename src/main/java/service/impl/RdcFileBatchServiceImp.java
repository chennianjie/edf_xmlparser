package service.impl;

import common.OracleConnection;
import exception.BaseException;
import service.RdcFileBatchService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/28/2019
 */
public class RdcFileBatchServiceImp implements RdcFileBatchService {

    private static final String INSERT_SQL = "INSERT INTO RDC_file_batch(id, bpm_batch_guid, bpm_batch_index, ingestion_state, create_timestamp)values" +
                                             "(RDC_FILE_BATCH_SEQ.nextval, ?, ?, 'PDP_RUNNING', TO_CHAR(sysdate,'YYYY-MM-DD HH24:MI:SS'))";

    private static final String UPDATE_SQL = "UPDATE RDC_FILE_BATCH b SET b.ingestion_state = ?, b.MODIFY_TIMPESTAMP = TO_CHAR(sysdate,'YYYY-MM-DD HH24:MI:SS')" +
                                             "WHERE b.BPM_batch_guid = ? AND b.bpm_batch_index = ? ";
    @Override
    public void insert(String batchGuid, Integer batchIndex) {
        if (batchGuid == null || batchIndex == null) {
            throw new BaseException("rdcFileBatchInsert variable empty.");
        }
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = OracleConnection.getConnection();
            pst = con.prepareStatement(INSERT_SQL);
            pst.setString(1, batchGuid);
            pst.setInt(2, batchIndex);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            OracleConnection.close(pst, con);
        }
    }

    @Override
    public void updateState(String batchGuid, String batchIndex, String state) {
        if (batchGuid == null || batchIndex == null || state == null) {
            throw new BaseException("rdcFileBatchUpdate variable empty.");
        }
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = OracleConnection.getConnection();
            pst = con.prepareStatement(UPDATE_SQL);
            pst.setString(1, state);
            pst.setString(2, batchGuid);
            pst.setString(3, batchIndex);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            OracleConnection.close(pst, con);
        }
    }
}
