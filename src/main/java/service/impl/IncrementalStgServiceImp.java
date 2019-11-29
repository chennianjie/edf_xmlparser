package service.impl;

import common.OracleConnection;
import entity.IncrementalStg;
import service.IncrementalStgService;
import common.exception.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/28/2019
 */
public class IncrementalStgServiceImp implements IncrementalStgService {

    private static final String INSERT_SQL = "INSERT INTO RDC_INCREMENTAL_STG VALUES(RDC_INCR_STG_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?)";

    @Override
    public void insert(IncrementalStg incrementalStg) {

    }

    @Override
    public void insertByBatch(List<IncrementalStg> list, Integer batchIndex, String uuid) {
        if (list == null || list.size() > 100 || batchIndex == null) {
            throw new BaseException("insertByBatch variable empty.");
        }

        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = OracleConnection.getConnection();
            con.setAutoCommit(false);
            pst = con.prepareStatement(INSERT_SQL);
            for (IncrementalStg inc : list) {
                pst.setLong(1, inc.getNda_pi());
                pst.setString(2, inc.getVersion());
                pst.setString(3, inc.getEntity_type());
                pst.setString(4, inc.getEntity_sub_type());
                pst.setString(5, inc.getEntity_rcs_sub_type());
                pst.setString(6, inc.getEntity_event());
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
                pst.setString(18, inc.getReference_flag());

                pst.addBatch();
            }
            pst.executeBatch();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            OracleConnection.close(pst, con);
        }
    }
}
