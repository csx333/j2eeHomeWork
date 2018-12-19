package sc.ustc.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * @author : csx
 * @description :
 * @date : 2018/12/16 20:59
 */
public abstract class BaseDAO {
    protected String driver;
    protected String url;
    protected String userName;
    protected String userPassword;
    private static Logger logger = LogManager.getLogger(BaseDAO.class.getName());

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Connection openDBConnection() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName(driver);
        logger.info("驱动加载成功");
        conn = DriverManager.getConnection(url, userName, userPassword);
        logger.info("连接成功");
        return conn;
    }
    public boolean closeDBConnection(ResultSet rs, PreparedStatement ps,Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
            if (conn == null && ps == null && rs == null) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public abstract Object query(String sql);
    public abstract boolean insert(String sql);
    public abstract boolean update(String sql);
    public abstract boolean delete(String sql);
}
