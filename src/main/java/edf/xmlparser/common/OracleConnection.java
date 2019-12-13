package edf.xmlparser.common;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 9/24/2019
 */
public class OracleConnection {
    private static Logger logger = Logger.getLogger(OracleConnection.class);

    private String driverName = "";
    private String dbURL = "";
    private static String user = "";
    private String password = "";
    public static OracleConnection connection = null;

    private OracleConnection(){
    driverName = PropertyUtil.getPropValue("driverName");
    dbURL = PropertyUtil.getPropValue("dbURL");
    user = PropertyUtil.getPropValue("user");
    password = PropertyUtil.getPropValue("password");
    }

    public static String getUser() {
        return user;
    }

    public static Connection getConnection() {
        Connection conn = null;
        if (connection == null) {
            try {
                connection = new OracleConnection();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection(connection.dbURL,
                    connection.user, connection.password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }



    public static void close(Statement statement, Connection con) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = getConnection();
        logger.info(connection);
        ArrayList<Long> propertyIds = PropertyUtil.getPropertyIds();
        for (Long i : propertyIds) {
            logger.info(i);
        }
    }
}
