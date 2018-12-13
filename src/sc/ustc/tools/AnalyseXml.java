package sc.ustc.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.*;

public class AnalyseXml {
    private static Logger logger = LogManager.getLogger(AnalyseXml.class.getName());

    public static Map<String,String> getActionAttribute(String actionName, File file)
            throws DocumentException {
        Map<String,String> map = new HashMap<>(16);
        Element element ;
        if((element= getActionElement(actionName,file))!= null) {
            logger.info("ActionElement>>>>>>>>>>>>>>>>>>>>"+ element.getText());
            map.put("name", element.attribute("name").getText());
            map.put("class", element.attribute("class").getText());
            map.put("method", element.attribute("method").getText());
            return map;
        }else {
            return null;
        }
    }

    public static Map<String,String> getResultAttribute(String actionName,String resultName,File file)
            throws DocumentException {
        Map<String,String> map = new HashMap<>(16);
        Element actionElement = getActionElement(actionName,file);
        List<Element> resultElements = actionElement.elements("result");
        for(Iterator it = resultElements.iterator();it.hasNext();){
            Element resultElement = (Element) it.next();
            logger.info("resultElement>>>>>>>>>>>>>>>>>>>"+ resultElement.getText());
            if(resultElement.attribute("name").getText().equals(resultName)){
                map.put("name",resultElement.attribute("name").getText());
                map.put("type",resultElement.attribute("type").getText());
                map.put("value",resultElement.attribute("value").getText());
                return map;
            }
        }
        return null;
    }

    public static List<Map<String,String>> getActionInterceptorAttribute(String actionName,File file)
            throws DocumentException{
        List<Element> interceptorRefElementsList = getInterceptorRefElements(actionName,file);
        Map<String,String> map = new HashMap<>(16);
        List<Map<String,String>> listMap = new ArrayList<>();
        if(interceptorRefElementsList.isEmpty() || interceptorRefElementsList == null){
            return null;
        }else{
            for(Iterator it = interceptorRefElementsList.iterator();it.hasNext();){
                Element interceptorRefElement = (Element) it.next();
                logger.info("interceptorRefElement>>>>>>>>>>>>>>>>>>>"+ interceptorRefElement.getName());
                String interceptorName = interceptorRefElement.attribute("name").getText();
                logger.info("interceptorName>>>>>>>>>>>>>>>>>>>"+ interceptorName);
                Element interceptorElement = getInterceptorElement(interceptorName,file);
                map.put("name",interceptorElement.attribute("name").getText());
                map.put("class",interceptorElement.attribute("class").getText());
                map.put("predo",interceptorElement.attribute("predo").getText());
                map.put("afterdo",interceptorElement.attribute("afterdo").getText());
                logger.info("interceptor的属性个数>>>>>>>>>>>>>>>>>>>"+ map.size());
                listMap.add(map);
            }
            return listMap;
        }
    }

    private static Element getActionElement(String actionName, File file)
            throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element root = document.getRootElement();
        logger.info("xmlRoot>>>>>>>>>>>>>>>>>>>>>>>>>"+ root.getName());
        List<Element> actionNodes = root.element("controller").elements("action");
        for(Iterator it = actionNodes.iterator();it.hasNext();){
            Element element = (Element) it.next();
            if(element.attribute("name").getText().equals(actionName)){
                return element;
            }
        }
        return null;
    }

    private static List<Element> getInterceptorRefElements(String actionName, File file) throws  DocumentException{
        Element actionElement = getActionElement(actionName,file);
        List<Element> interceptorRefElementsList = actionElement.elements("interceptro-ref");
        return interceptorRefElementsList;
    }

    private static Element getInterceptorElement(String interceptorName, File file) throws  DocumentException{
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        List<Element> interceptorElementsList = document.getRootElement().elements("interceptor");

        for(Iterator it = interceptorElementsList.iterator();it.hasNext();) {
            Element interceptorElement = (Element) it.next();
            if(interceptorElement.attribute("name").getText().equals(interceptorName)){
                logger.info("找到interceptorElement>>>>>>>>>>>>>>>>>>>>>>>>>"+ interceptorElement.attribute("name").getText());
                return interceptorElement;
            }
        }
        return null;
    }


}
