package sc.ustc.di;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import sc.ustc.tools.AnalyseXml;
import sc.ustc.tools.ReflectTool;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author : csx
 * @description :
 * @date : 2018/12/21 9:55
 */
public class ClassPathXmlApplicationContext {
    private File file;
    private Map map = new HashMap();
    private static Logger logger = LogManager.getLogger(ClassPathXmlApplicationContext.class.getName());

    public ClassPathXmlApplicationContext(){
    }
    public ClassPathXmlApplicationContext(File file){
        this.file = file;
        analyseXml();
    }

    public  Object getBean(String beanName){
        return map.get(beanName);
    }

    private void analyseXml(){
        try{
            SAXReader reader = new SAXReader();
            Document document = reader.read(file);
            logger.info("这个控制文件的xmlRoot是>>>>>>>>>>>>>>>>>>>>>>>>>"+ document.getRootElement().getName());
            List<Element> BeanElementsList = document.getRootElement().elements("bean");
            for(Iterator it =BeanElementsList.iterator();it.hasNext(); ){
                Element beanElement = (Element) it.next();
                String id = beanElement.attribute("id").getText();
                String clazz = beanElement.attribute("class").getText();
                Object object = Class.forName(clazz).getDeclaredConstructor().newInstance();
                List<Element> BeanFieldElementsList = beanElement.elements("field");
                for(Iterator fieldIt =BeanFieldElementsList.iterator();it.hasNext(); ){
                    Element beanFieldElement = (Element) fieldIt.next();
                    if(beanFieldElement.attribute("bean-ref")!=null){
                        logger.info("object>>>>>>>>>>>>>>>>>>>>>>>>>"+ object.toString());
                        ReflectTool.invokeSet(object,beanFieldElement.attribute("name").getText(),
                                map.get(beanFieldElement.attribute("bean-ref").getText()));
                        logger.info("object>>>>>>>>>>>>>>>>>>>>>>>>>"+ object.toString());
                    }else if(beanFieldElement.attribute("value")!=null){
                        logger.info("object>>>>>>>>>>>>>>>>>>>>>>>>>"+ object.toString());
                        ReflectTool.invokeSet(object,beanFieldElement.attribute("name").getText(),
                                beanFieldElement.attribute("value").getText());
                        logger.info("object>>>>>>>>>>>>>>>>>>>>>>>>>"+ object.toString());
                    }
                }
                map.put(id,object);
                logger.info("map的size>>>>>>>>>>>>>>>>>>>>>>>>>"+ map.size());
            }
        }catch (Exception e){
        }
    }
}
