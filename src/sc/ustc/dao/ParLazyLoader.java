package sc.ustc.dao;

import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sc.ustc.bean.AccountConfigBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author : csx
 * @description :
 * @date : 2018/12/24 1:45
 */
public  class ParLazyLoader implements LazyLoader {
    public static String name;
    public static String sql1;
    public static String column;
    public static AccountConfigBean accountConfigBean;
    public static Connection conn = null;
    public static PreparedStatement ps=null;
    private static Logger logger = LogManager.getLogger(ParLazyLoader.class.getName());

    public  static Object getInstance(Class<?> clazz1, String sql1, String column,
                                      AccountConfigBean accountConfigBean, String name,Connection conn){
        ParLazyLoader.accountConfigBean = accountConfigBean;
        ParLazyLoader.column =column;
        ParLazyLoader.name =name;
        ParLazyLoader.sql1 =sql1;
        ParLazyLoader.conn = conn;
        //创建加强器，用来创建动态代理类
        Enhancer enhancer = new Enhancer();
        //为加强器指定要代理的业务类（即：为下面生成的代理类指定父类）
        enhancer.setSuperclass(clazz1);
        ParLazyLoader pl = new ParLazyLoader();
        // 创建动态代理类对象并返回
        return enhancer.create(clazz1,pl);
    }
    @Override
    public Object loadObject(){
        Object testLazy =null;
        logger.info(">>>>>>>>>>进入loadObject()方法内，开始执行sql语句>>>>>>>>>>>");
        try {
            logger.info("loadObject>>>>>>>>>>>>>" + sql1+name);
            ps = conn.prepareStatement(sql1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                testLazy = Conversation.getProObject(accountConfigBean,name,rs.getString(column));
            }
            logger.info(">>>>>>>>>>>>>执行完loadObject()>>>>>>>.");
            return testLazy;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
