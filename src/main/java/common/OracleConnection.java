package common;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 9/24/2019
 */
public class OracleConnection {
    private String driverName = "";
    private String dbURL = "";
    private String user = "";
    private String password = "";
    public static OracleConnection connection = null;

    private OracleConnection(){
    driverName = PropertyUtil.getPropValue("driverName");
    dbURL = PropertyUtil.getPropValue("dbURL");
    user = PropertyUtil.getPropValue("user");
    password = PropertyUtil.getPropValue("password");
    System.out.println("dbURL:" + dbURL);
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

    public static void main(String[] args) {
        Connection connection = getConnection();
        System.out.println(connection);
        ArrayList<Long> propertyIds = PropertyUtil.getPropertyIds();
        for (Long i : propertyIds) {
            System.out.println(i);
        }
    }
}
