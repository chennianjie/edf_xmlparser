package service.impl;

import common.OracleConnection;
import service.IqmConfigService;

import java.sql.*;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/28/2019
 */
public class IqmConfigServiceImp implements IqmConfigService {

    private static final String GET_SEQ_NUM = " select config_value from iqm_core.iqm_config where category_code = 'RDC_SEQ' and rownum = 1";
    private static final String UPDATE_SEQ_NUM = "Update Iqm_Core.Iqm_Config Set Config_Value = ? Where Category_Code = 'RDC_SEQ' And Rownum = 1";

    @Override
    public Integer getSequenceNum(){
        Connection con = null;
        Statement statement = null;
        try {
            con = OracleConnection.getConnection();
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(GET_SEQ_NUM);
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
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = OracleConnection.getConnection();
            statement = con.prepareStatement(UPDATE_SEQ_NUM);
            statement.setInt(1, fileSequence);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            OracleConnection.close(statement, con);
        }
    }
}
