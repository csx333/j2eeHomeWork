package sc.ustc.tools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author : csx
 * @description :
 * @date : 2018/12/16 20:40
 */
public class BasicXslt {

    public static void transformXmlByXslt(HttpServletRequest request, HttpServletResponse response) {
        // 获取转换器工厂
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            // 获取转换器对象实例
            Transformer transformer = tf.newTransformer(new StreamSource(request.getSession().getServletContext().getRealPath("pages/logout.xsl")));
            // 进行转换
            transformer.transform(new StreamSource(request.getSession().getServletContext().getRealPath("pages/success_view.xml")),
                    new StreamResult(response.getOutputStream()));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
