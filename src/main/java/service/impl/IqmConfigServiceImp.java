package service.impl;

import common.OracleConnection;
import service.IqmConfigService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/28/2019
 */
public class IqmConfigServiceImp implements IqmConfigService {

    private static final String SEQ_NUM = " select config_value from iqm_core.iqm_config where category_code = 'RDC_SEQ' and rownum = 1";

    @Override
    public Integer getSequenceNum(){
        Connection con = null;
        Statement statement = null;
        try {
            con = OracleConnection.getConnection();
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SEQ_NUM);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            OracleConnection.close(statement, con);
        }
        return -1;
    }

    @Override
    public void update(String categoryCode, Integer fileSequence) {

    }
}
