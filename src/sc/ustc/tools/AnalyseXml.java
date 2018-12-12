package sc.ustc.tools;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.*;

public class AnalyseXml {

    public static Map<String,String> getActionAttribute(String actionName, File file)
            throws DocumentException {
        Map<String,String> map = new HashMap<>();
        Element element ;
        if((element= getActionElement(actionName,file))!= null) {
            System.out.println("ActionElement>>>>>>>>>>>>>>>>>>>>"+ element.getText());
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
        Map<String,String> map = new HashMap<>();
        Element actionElement = getActionElement(actionName,file);
        List<Element> resultElements = actionElement.elements("result");
        for(Iterator it = resultElements.iterator();it.hasNext();){
            Element resultElement = (Element) it.next();
            System.out.println("resultElement>>>>>>>>>>>>>>>>>>>"+ resultElement.getText());
            if(resultElement.attribute("name").getText().equals(resultName)){
                map.put("name",resultElement.attribute("name").getText());
                map.put("type",resultElement.attribute("type").getText());
                map.put("value",resultElement.attribute("value").getText());
                return map;
            }
        }
        return null;
    }

    private static Element getActionElement(String actionName, File file)
            throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element root = document.getRootElement();
        System.out.println("xmlRoot>>>>>>>>>>>>>>>>>>>>>>>>>"+ root.getName());
        List<Element> actionNodes = root.element("controller").elements("action");
        for(Iterator it = actionNodes.iterator();it.hasNext();){
            Element element = (Element) it.next();
            if(element.attribute("name").getText().equals(actionName)){
                return element;
            }
        }
        return null;
    }

}
