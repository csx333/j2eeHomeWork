package sc.ustc.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import sc.ustc.bean.ActionBean;
import sc.ustc.bean.ActionResultBean;
import sc.ustc.bean.InterceptorBean;
import java.io.File;
import java.util.*;
/**
 * AnalyseXml class
 * @description : 根据传入Action和controller.xml文件取得ActionBean，以及根据Action返回的Result取得ActionResultBean
 * @author : csx
 * @date : 2018/12/14
 */
public class AnalyseXml {

    private static Logger logger = LogManager.getLogger(AnalyseXml.class.getName());
    /**
     * @Description :根据Action返回的Result取得ActionResultBean
     * @param ： actionName Action的名字
     * @param ： resultName Action返回的Result值
     * @param ： file 控制Action的controller.xml文件
     * @Return : sc.ustc.bean.ActionResultBean controller.xml文件中相应Action的返回result的bean
     * @Author : csx
     * @Date : 2018/12/14 12:46
     */
    public static ActionResultBean getActionResultAttribute(String actionName, String resultName, File file)
            throws DocumentException {
        ActionResultBean actionResultBean = new ActionResultBean();
        Element actionElement = getActionElement(actionName,file);
        List<Element> resultElements = actionElement.elements("result");
        for(Iterator it = resultElements.iterator();it.hasNext();){
            Element resultElement = (Element) it.next();
            logger.info("resultElement's name>>>>>>>>>>>>>>>>>>>"+ resultElement.attribute("name").getText());
            if(resultElement.attribute("name").getText().equals(resultName)){
                actionResultBean.setActionResultName(resultElement.attribute("name").getText());
                actionResultBean.setActionResultType(resultElement.attribute("type").getText());
                actionResultBean.setActionResultValue(resultElement.attribute("value").getText());
                return actionResultBean;
            }
        }
        return null;
    }
    /**
     * @Description :
     * @param ： actionName 需要取得的Action的name
     * @param ： file controller.xml文件
     * @Return : sc.ustc.bean.ActionBean
     * @Author : csx
     * @Date : 2018/12/14 17:52
     */
    public static ActionBean getActionAttributeAndInterceptorAttribute(String actionName,File file)
            throws DocumentException{
        ActionBean actionBean = getActionAttribute(actionName,file);
        List<Element> interceptorRefElementsList = getInterceptorRefElements(actionName,file);
        List<InterceptorBean> interceptorsList = new ArrayList<>();
        if(interceptorRefElementsList.isEmpty() || interceptorRefElementsList == null){
            actionBean.setInterceptorBeans(interceptorsList);
            logger.error("actionBean的拦截器数量"+interceptorsList.size());
            return actionBean;
        }else{
            for(Iterator it = interceptorRefElementsList.iterator();it.hasNext();){
                Element interceptorRefElement = (Element) it.next();
                logger.info("interceptorRefElement's name>>>>>>>>>>>>>>>>>>>"+ interceptorRefElement.attribute("name").getText());
                String interceptorName = interceptorRefElement.attribute("name").getText();
                Element interceptorElement = getInterceptorElement(interceptorName,file);
                InterceptorBean interceptorBean = new InterceptorBean();
                interceptorBean.setInterceptorName(interceptorElement.attribute("name").getText());
                interceptorBean.setInterceptorClass(interceptorElement.attribute("class").getText());
                interceptorBean.setInterceptorPredo(interceptorElement.attribute("predo").getText());
                interceptorBean.setInterceptorAfterdo(interceptorElement.attribute("afterdo").getText());
                logger.info("这个interceptor的名字>>>>>>>>>>>>>>>>>>>"+ interceptorBean.getInterceptorName());
                interceptorsList.add(interceptorBean);
            }
            actionBean.setInterceptorBeans(interceptorsList);
            logger.error("actionBean的拦截器数量"+interceptorsList.size());
            return actionBean;
        }
    }
    /**
     * @Description :
     * @param ： actionName
     * @param ： file
     * @Return : sc.ustc.bean.ActionBean
     * @Author : csx
     * @Date : 2018/12/14 17:54
     */
    private static ActionBean getActionAttribute(String actionName, File file)
            throws DocumentException {
        ActionBean actionBean = new ActionBean();
        Element element ;
        if((element= getActionElement(actionName,file))!= null) {
            logger.info("ActionElement>>>>>>>>>>>>>>>>>>>>"+ element.attribute("name").getText());
            actionBean.setActionName(element.attribute("name").getText());
            actionBean.setActionClass(element.attribute("class").getText());
            actionBean.setActionMethod(element.attribute("method").getText());
            return actionBean;
        }else {
            return null;
        }
    }
    /**
     * @Description : 取得Action在xml的节点
     * @param ： actionName
     * @param ： file
     * @Return : org.dom4j.Element
     * @Author : csx
     * @Date : 2018/12/14 17:54
     */
    private static Element getActionElement(String actionName, File file)
            throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element root = document.getRootElement();
        logger.info("这个控制文件的xmlRoot是>>>>>>>>>>>>>>>>>>>>>>>>>"+ root.getName());
        List<Element> actionNodes = root.element("controller").elements("action");
        for(Iterator it = actionNodes.iterator();it.hasNext();){
            Element element = (Element) it.next();
            if(element.attribute("name").getText().equals(actionName)){
                logger.info("拿到Action的名字是>>>>>>>>>>>>>>>>>>>>>>>>>"+ element.attribute("name").getText());
                return element;
            }
        }
        return null;
    }
    /**
     * @Description : 取得Action的interceptro-ref节点列表
     * @param ： actionName
     * @param ： file
     * @Return : java.util.List<org.dom4j.Element>
     * @Author : csx
     * @Date : 2018/12/14 17:55
     */
    private static List<Element> getInterceptorRefElements(String actionName, File file)
            throws  DocumentException{
        Element actionElement = getActionElement(actionName,file);
        List<Element> interceptorRefElementsList = actionElement.elements("interceptro-ref");
        logger.info("这个action的interceptorRefElementsList大小是>>>>>>>>>>>>>>>>>>>>>>>>>"+ interceptorRefElementsList.size());
        return interceptorRefElementsList;
    }
    /**
     * @Description : 取得相应Action的拦截器节点
     * @param ： interceptorName
     * @param ： file
     * @Return : org.dom4j.Element
     * @Author : csx
     * @Date : 2018/12/14 17:55
     */
    private static Element getInterceptorElement(String interceptorName, File file)
            throws  DocumentException{
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        List<Element> interceptorElementsList = document.getRootElement().elements("interceptor");
        for(Iterator it = interceptorElementsList.iterator();it.hasNext();) {
            Element interceptorElement = (Element) it.next();
            if(interceptorElement.attribute("name").getText().equals(interceptorName)){
                logger.info("找到interceptorElement的名字>>>>>>>>>>>>>>>>>>>>>>>>>"+ interceptorElement.attribute("name").getText());
                return interceptorElement;
            }
        }
        return null;
    }
}
