package sc.ustc.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sc.ustc.bean.AccountConfigBean;
import sc.ustc.bean.DAOClassPropertyBean;
import sc.ustc.tools.*;
import java.sql.*;
import java.util.*;

/**
 * @author : csx
 * @description : 定义数据操作CRUD方法，每个方法将对象操作解释成目标数据库的 DML 或 DDL，通过 JDBC 完成数据持久化。
 * @date : 2018/12/19 18:54
 */
public class Conversation {
    private Map<String,String> jdbcMap;
    private Map<String,AccountConfigBean> accountConfigBeansMap;
    private Connection conn = null;
    private ResultSet rs= null;
    private PreparedStatement ps=null;
    private Class<?> clazz;
    private static Logger logger = LogManager.getLogger(Conversation.class.getName());

    public Conversation(Map<String, String> jdbcMap, Map<String,AccountConfigBean> accountConfigBeansMap) {
        this.jdbcMap = jdbcMap;
        this.accountConfigBeansMap = accountConfigBeansMap;
    }

    public void openConversation(){
        try {
            Class.forName(jdbcMap.get("driver_class"));
            logger.info("驱动加载成功");
            conn = DriverManager.getConnection(jdbcMap.get("url_path"),
                    jdbcMap.get("db_username"), jdbcMap.get("db_userpassword"));
            logger.info("连接成功");
        }catch (SQLException e){
            e.printStackTrace();
            logger.info("数据库连接失败SQLException");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            logger.info("数据库连接失败");
        }
    }

    public void closeConversation(){
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

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  boolean save(Object object)throws Exception{
        String colnamesql = null;
        String valuesql = null;
        if(object!=null && clazz.isInstance(object)){
            AccountConfigBean accountConfigBean = accountConfigBeansMap.get(object.getClass().getName());
            Map<String,Object> sqlCondArray = getCondValue(object,accountConfigBean);
            logger.info("save" + object.getClass().getName());
            for(String col : sqlCondArray.keySet()){
                Object value = sqlCondArray.get(col);
                if(colnamesql == null){
                    colnamesql = col;
                }else{
                    colnamesql =colnamesql+","+col;
                }

                if(valuesql == null){
                    valuesql = "\""+value+ "\"";
                }else{
                    valuesql =valuesql+","+ "\""+value+ "\"";
                }
            }
            String sql1 ="insert into com." + accountConfigBean.getTable()+"(";
            String sql2 =") values(";
            String sql3 =")";
            String sql = sql1+colnamesql+sql2+valuesql+sql3;
            PreparedStatement psql = conn.prepareStatement(sql);
            psql.executeUpdate();
            return true;
        }else{
            return false;
        }
    }

    public boolean update(Object object)throws Exception{
        String updateSql = null;
        String idsql = null;
        if(object!=null && clazz.isInstance(object)){
            AccountConfigBean accountConfigBean = accountConfigBeansMap.get(object.getClass().getName());
            String pkCol=accountConfigBean.getPkDaoClassPropertyBean().getColumn();
            Map<String,Object> sqlCondArray = getCondValue(object,accountConfigBean);
            Object pkValue = sqlCondArray.get(pkCol);
            idsql = pkCol+"="+"\""+pkValue+"\"";
            for(String col : sqlCondArray.keySet()){
                Object value = sqlCondArray.get(col);
                if(updateSql == null){
                    updateSql = col+"="+"\""+value+"\"";
                }else{
                    updateSql =updateSql+","+ col+"="+"\""+value+"\"";
                }
            }
            String sql1 ="update com." + accountConfigBean.getTable()+"set";
            String sql2 ="where id = ";
            String sql = sql1+updateSql+sql2+idsql;
            PreparedStatement psql = conn.prepareStatement(sql);
            psql.executeUpdate();
            return true;
        }else{
            return false;
        }
    }

    public List<Object> get(Object object) throws Exception {
        List<Object> returnList = new ArrayList<>();
        //临时存储返回对象和对象的pk的Map集合以便存储懒加载的查询结果
        Map<String,Object> objectsMap = new HashMap<>();
        logger.info("我是Conversation的get方法,拿到的类名>>>>>>>" + object.getClass().getName());
        AccountConfigBean accountConfigBean = accountConfigBeansMap.get(object.getClass().getName());
        //判断传入object是否为空且已在or_mapping.xml登记
        if (object != null && accountConfigBean!=null) {
            clazz = object.getClass();
            Object pkValue = ReflectTool.invokeGet(object,accountConfigBean.getPkDaoClassPropertyBean().getName());
            logger.info("我是Conversation的get方法,拿到的主键的值>>>>>>>" + pkValue);
            String sql1 = "select "+accountConfigBean.getPkDaoClassPropertyBean().getColumn()+",";
            String sql2 = " from com." + accountConfigBean.getTable() + " where " ;
            String sqlPKCond =null;
            String sqlCond = null;
            String sqlSeach=null;
            String sqlLazySeach = null;
            String sql =null;
            Map<String,String> lazySeach= null;
            Map<String,String> notLazySeach = null;
            //以下代码取得对象中存在值得属性，并根据这些值构建查询条件
            //获取查询的条件
            if (pkValue != null){
                sqlPKCond = accountConfigBean.getPkDaoClassPropertyBean().getColumn() + " ="+ "\"" + pkValue + "\" ";
            }
            for (Iterator it = accountConfigBean.getDaoClassPropertyBeans().iterator(); it.hasNext(); ) {
                DAOClassPropertyBean daoClassPropertyBean = (DAOClassPropertyBean) it.next();
                Object temp = ReflectTool.invokeGet(object, daoClassPropertyBean.getName());
                //获取查询的条件
                if(temp!=null){
                    if(sqlCond == null) {
                        sqlCond =daoClassPropertyBean.getColumn() + "=" + "\"" + temp.toString() + "\" ";
                    }else{
                        sqlCond = sqlCond+"and"+daoClassPropertyBean.getColumn() + "=" + "\"" + temp.toString() + "\" ";
                    }
                }
                //获取急加载的属性的名字和对应的在表中的列名字的map集合
                if(daoClassPropertyBean.getLazy().equals("false")){
                    if(notLazySeach == null){
                        notLazySeach = new HashMap<>();
                        notLazySeach.put(accountConfigBean.getPkDaoClassPropertyBean().getName(),
                                accountConfigBean.getPkDaoClassPropertyBean().getColumn());
                    }
                    notLazySeach.put(daoClassPropertyBean.getName(),daoClassPropertyBean.getColumn());
                    if(sqlSeach == null ){
                        sqlSeach= ""+daoClassPropertyBean.getColumn();
                        logger.info(">>>>>>>>>>>>"+sqlSeach);
                    }else {
                        sqlSeach =sqlSeach+","+daoClassPropertyBean.getColumn();
                    }
                //获取查询的懒加载的属性的名字和对应的在表中的列名字的map集合
                }else{
                    if(lazySeach == null){
                        lazySeach = new HashMap<>();
                    }
                    lazySeach.put(daoClassPropertyBean.getName(),daoClassPropertyBean.getColumn());
                    if(sqlLazySeach == null){
                        sqlLazySeach= ""+daoClassPropertyBean.getColumn();
                    }else {
                        sqlLazySeach =sqlLazySeach+","+daoClassPropertyBean.getColumn();
                    }
                }
            }

            logger.info("现在情况>>>"+" sqlSeach"+sqlSeach+" sqlLazySeach"+sqlLazySeach+" lazySeachmap"+lazySeach.size()+" notLazySeachmap"+notLazySeach.size());

            if (sqlSeach != null) {
                logger.info("急加载>>>>>>>>>>>>>");
                if(sqlPKCond != null && sqlCond != null){
                    sql = sql1+sqlSeach+sql2+sqlPKCond+"and"+sqlCond;
                }else if(sqlPKCond == null){
                    sql = sql1+sqlSeach+sql2+sqlCond;
                }else if(sqlCond == null){
                    sql = sql1+sqlSeach+sql2+sqlPKCond;
                }
                logger.info("sql>>>>>>>>>>>>>"+sql);
                try {
                    ps = conn.prepareStatement(sql);
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        Object returnObject = clazz.getDeclaredConstructor().newInstance();
                        String ObjectPKValue = null;
                        for (String name : notLazySeach.keySet()) {
                            String column = notLazySeach.get(name);
                            System.out.println(name + "  " + column);
                            logger.info("returnObject>>>>>>>>>>>>>" + returnObject.toString());
                            //将查询属性存入对象
                            ReflectTool.invokeSet(returnObject,name,rs.getString(column));
                            logger.info("returnObject2>>>>>>>>>>>>>" + returnObject.toString());
                            if(name.equals(accountConfigBean.getPkDaoClassPropertyBean().getName())){
                                ObjectPKValue=rs.getString(column);
                            }
                        }
                        //将已设置急加载值的对象和相应的主键存入map，
                        //这样懒加载可以根据主键获取唯一记录，并将懒属性存入对应的对象
                        objectsMap.put(ObjectPKValue,returnObject);
                        logger.info("ObjectPKValue>>>>>>>>>>>>>" +ObjectPKValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            if(sqlLazySeach != null) {
                logger.info("懒加载>>>>>>>>>>>>>");
                //根据主键从objectsMap取出对象，并根据这个对象的主键设置lazy属性
                for (String ObjectPKValueTemp : objectsMap.keySet()) {
                    for (String name : lazySeach.keySet()) {
                        String column = lazySeach.get(name);
                        String sqlLazy = "select " + column + sql2
                                + accountConfigBean.getPkDaoClassPropertyBean().getColumn()
                                + " =" + "\"" + ObjectPKValueTemp + "\" ";
                        logger.info("sqlLazy>>>>>>>>>>>>>" +sqlLazy);
                        Object returnObject = objectsMap.get(ObjectPKValueTemp);
                        String nameField = Conversation.getProType(accountConfigBean,name);
                        logger.info("获取属性的名字>>>>>>>>>>>>>" + name+ "  nameField  "+nameField);
                        //返回懒属性的代理对象
                        Object parLazyLoader = ParLazyLoader.getInstance(Class.forName(nameField),
                                sqlLazy,column,accountConfigBean,name,conn);
                        ReflectTool.invokeSet(returnObject,name, parLazyLoader);
                        logger.info(">>>>>>>>>>>>懒加载已经完成，已返回代理对象>>>>>>>>>>>>>");
                    }
                }
            }
            for(String temp : objectsMap.keySet()){
                Object returnObject = objectsMap.get(temp);
                returnList.add(returnObject);
            }
            return returnList;
        }else{
            return null;
        }
    }

    public boolean delete(Object object) throws Exception {
        String deleteSql = null;
        if(object!=null && clazz.isInstance(object)){
            AccountConfigBean accountConfigBean = accountConfigBeansMap.get(object.getClass().getName());
            Map<String,Object> sqlCondArray = getCondValue(object,accountConfigBean);
            for(String col : sqlCondArray.keySet()){
                Object value = sqlCondArray.get(col);
                if(deleteSql == null){
                    deleteSql = col+"="+"\""+value+"\"";
                }else{
                    deleteSql =deleteSql+","+ col+"="+"\""+value+"\"";
                }
            }
            String sql1 ="delete from com." + accountConfigBean.getTable()+"where";
            String sql = sql1+deleteSql;
            PreparedStatement psql = conn.prepareStatement(sql);
            psql.executeUpdate();
            return true;
        }else{
            return false;
        }
    }

    public static Object getProObject(AccountConfigBean ac,String name,Object o){
        String type = Conversation.getProType(ac,name);
        try{
            Class<?> clazz = Class.forName(type);
            Object object = clazz.getDeclaredConstructor().newInstance();
            logger.info("getProObject>>>>>>>>>>>>>" + object.getClass());
            ReflectTool.invokeSet(object,"money", o);
            logger.info("getProObject>>>>>>>>>>>>>" + object);
            return object;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static String getProType(AccountConfigBean ac,String name){
        String type;
        for(Iterator it = ac.getDaoClassPropertyBeans().iterator(); it.hasNext(); ) {
            DAOClassPropertyBean db = (DAOClassPropertyBean) it.next();
            if (db.getName().equals(name)) {
                type = db.getType();
                return type;
            }
        }
        return null;
    }
    /**
     * @Description : 取得对象的属性名字和值
     * @param ： object
     * @param ： accountConfigBean
     * @Return : java.util.Map<java.lang.String,java.lang.Object>
     * @Author : csx
     * @Date : 2018/12/24 14:51
     */
    private Map<String,Object> getCondValue(Object object ,AccountConfigBean accountConfigBean) {
        Map<String,Object> tempMap = new HashMap<>();
        Object pkValue = ReflectTool.invokeGet(object, accountConfigBean.getPkDaoClassPropertyBean().getName());
        if (pkValue != null) {
            tempMap.put(accountConfigBean.getPkDaoClassPropertyBean().getColumn(),pkValue);
        }
        for (Iterator it = accountConfigBean.getDaoClassPropertyBeans().iterator(); it.hasNext(); ) {
            DAOClassPropertyBean daoClassPropertyBean = (DAOClassPropertyBean) it.next();
            Object temp = ReflectTool.invokeGet(object, daoClassPropertyBean.getName());
            if (temp != null) {
                tempMap.put(daoClassPropertyBean.getColumn(),temp);
            }
        }
        return tempMap;
    }

}
