package edf.xmlparser.common;

import edf.xmlparser.common.exception.BaseException;
import edf.xmlparser.enums.LogTypeEnum;
import edf.xmlparser.service.impl.IqmConfigServiceImp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 12/3/2019
 */
public class IQMLogUtil {


    private volatile static IQMLogUtil singleton;
    private IQMLogUtil (){}
    public static IQMLogUtil getSingleton() {
        if (singleton == null) {
            synchronized (IQMLogUtil.class) {
                if (singleton == null) {
                    singleton = new IQMLogUtil();
                }
            }
        }
        return singleton;
    }


    private static final String PDP_STATUS = "PDP_STATUS";
    private static final String ERROR_TYPE = "ERROR";
    private static final String WARNING_TYPE = "WARNING";
    private static final String INFO_TYPE = "INFO";
    private static final String DEBUG_TYPE = "DEBUG";

    private static final String INSERT_SQL = "INSERT INTO IQM_LOG(LOG_ID, LOG_TYPE, LOG_COMPOSITE_NAME, LOG_COMPOSITE_REVISION," +
            "LOG_COMPOSITE_INSTANCE_ID, LOG_ACTIVITY_NAME, LOG_USER, LOG_TITLE, LOG_DESC, LOG_TIMESTAMP, LOG_DATALOAD_VERSION_ID)" +
            "VALUES (IQM_LOG_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, sysdate, ?)";

    private String compositeName, compositeReversion, compositeInstanceId, activityName;
    private Integer dataLoadVersionId = 0;

    private IqmConfigServiceImp iqmConfigServiceImp = new IqmConfigServiceImp();

    public void logInit(String compositeName, String compositeReversion, String compositeInstanceId, String activityName) {
        this.compositeName = compositeName;
        this.compositeReversion = compositeReversion;
        this.compositeInstanceId = compositeInstanceId;
        this.activityName = activityName;
    }

    public void logging(String type, String user, String title, String detail, Date time) {
        if (type == null || user == null || title == null || detail == null || time == null) {
            throw new BaseException("logging variable exception");
        }
        Integer logLevelConfig = iqmConfigServiceImp.getLogLevel();
        Integer curLogLevel = LogTypeEnum.getByValue(type).getRank();
        if (curLogLevel <= logLevelConfig) {
            //insert into db
            insert(type, user, title, detail, time);
        }
    }


    private void insert(String type, String user, String title, String detail, Date time) {
        int seqNum = new IqmConfigServiceImp().getSequenceNum();
        if (seqNum == -1) {
            throw new BaseException("seqNum variable empty.");
        }
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = OracleConnection.getConnection();
            pst = con.prepareStatement(INSERT_SQL);
            pst.setString(1, type);
            pst.setString(2, this.compositeName);
            pst.setString(3, this.compositeReversion);
            pst.setString(4, this.compositeInstanceId);
            pst.setString(5, this.activityName);
            pst.setString(6, user);
            pst.setString(7, title);
            pst.setString(8, detail);
            pst.setInt(9, this.dataLoadVersionId);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            OracleConnection.close(pst, con);
        }
    }
}
