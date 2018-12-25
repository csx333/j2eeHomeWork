package sc.ustc.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import sc.ustc.bean.AccountConfigBean;
import sc.ustc.bean.DAOClassPropertyBean;
import java.io.File;
import java.util.*;

/**
 * @author : csx
 * @description : 负责解析 UseSC 工程的配置or_mapping.xml,并将解析结果传递给Conversation类
 * @date : 2018/12/19 18:54
 */
public class Configuration {
    /**
     * jdbcMap 存放or_mapping.xml中jdbc的信息
     */
    Map<String,String> jdbcMap;
    /**
     * jdbcMap 存放or_mapping.xml中class的信息，可根据类名获取类及相应表的信息
     */
    Map<String,AccountConfigBean> accountConfigBeansMap;

    private static Logger logger = LogManager.getLogger(Configuration.class.getName());
    public Configuration(){}

    /**
     * @Description : 解析文件，并传入Conversation类
     * @param ： file  UseSC 工程的配置or_mapping.xml
     * @Return :
     * @Author : csx
     * @Date : 2018/12/23 23:44
     */
    public Configuration(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element root = document.getRootElement();
        jdbcMap = new HashMap<>();
        logger.info("这个控制文件的xmlRoot是>>>>>>>>>>>>>>>>>>>>>>>>>"+ root.getName());
        List<Element> jdbcPropertyNodes = root.element("jdbc").elements("property");
        for(Iterator it = jdbcPropertyNodes.iterator(); it.hasNext();){
            Element element = (Element) it.next();
            jdbcMap.put(element.element("name").getText(),element.element("value").getText());
        }
        logger.info("这个控制文件的jdbc,property是>>>>>>>>>>>>>>>>>>>>>>>>>"+jdbcMap.size());

        accountConfigBeansMap = new HashMap<>();
        List<Element> classNodes = root.elements("class");
        for(Iterator classIt = classNodes.iterator(); classIt.hasNext();){
            Element classElement = (Element) classIt.next();
            AccountConfigBean accountConfigBean = new AccountConfigBean();
            List<DAOClassPropertyBean> daoClassPropertyBeanList = new ArrayList<>();
            List<Element> classPropertyNodes = classElement.elements("property");
            for(Iterator it = classPropertyNodes.iterator(); it.hasNext();){
                Element element = (Element) it.next();
                DAOClassPropertyBean daoClassPropertyBean = new DAOClassPropertyBean();
                daoClassPropertyBean.setName(element.element("name").getText());
                daoClassPropertyBean.setColumn(element.element("column").getText());
                daoClassPropertyBean.setType(element.element("type").getText());
                daoClassPropertyBean.setLazy(element.element("lazy").getText());
                daoClassPropertyBeanList.add(daoClassPropertyBean);
            }
            accountConfigBean.setDaoClassPropertyBeans(daoClassPropertyBeanList);
            accountConfigBean.setName(root.element("class").element("name").getText());
            accountConfigBean.setTable(root.element("class").element("table").getText());

            //配置主键
            DAOClassPropertyBean pkDaoClassPropertyBean = new DAOClassPropertyBean();
            pkDaoClassPropertyBean.setName(root.element("class").element("id").element("name").getText());
            pkDaoClassPropertyBean.setColumn(root.element("class").element("id").element("column").getText());
            pkDaoClassPropertyBean.setLazy(root.element("class").element("id").element("type").getText());
            pkDaoClassPropertyBean.setType(root.element("class").element("id").element("lazy").getText());
            accountConfigBean.setPkDaoClassPropertyBean(pkDaoClassPropertyBean);
            accountConfigBeansMap.put(accountConfigBean.getName(),accountConfigBean);
            logger.info("now class 's number is>>>>>>>>>>>>>>>>>>>>>>>>>"+ accountConfigBeansMap.size());
        }

    }
    /**
     * @Description :获得操作数据库的Conversation对象
     * @param ：
     * @Return : sc.ustc.dao.Conversation 业务代码可以传入bean对象进行CRUD 操作的类
     * @Author : csx
     * @Date : 2018/12/23 23:43
     */
    public Conversation createConversation(){

        return new Conversation(jdbcMap,accountConfigBeansMap);
    }
}
