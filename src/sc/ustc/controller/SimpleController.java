package sc.ustc.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sc.ustc.tools.AnalyseXml;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SimpleController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static Logger logger = LogManager.getLogger(SimpleController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "text/html; charset=UTF-8");

        String actionName = request.getServletPath();
        logger.info("actionName.getServletPath()>>>>>>>>>" + actionName);
        String[] actionUrl = actionName.split("/");
        actionName = actionUrl[actionUrl.length - 1];
        logger.info("actionName>>>>>>>>>>>" + actionName);
        String path = request.getSession().getServletContext().getRealPath("WEB-INF/classes/controller.xml");
        logger.info("controller.xml.path>>>>>>" + path);
        try {
            File fd = new File(path);
            Map<String, String> actionMap = AnalyseXml.getActionAttribute(actionName.substring(0, actionName.indexOf(".")), fd);

            if (!actionMap.isEmpty()&& actionMap!= null ) {

                String className = actionMap.get("class");
                String methodName = actionMap.get("method");

                Class clazz = Class.forName(className);
                Method m = clazz.getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
                String result = (String) m.invoke(clazz.getDeclaredConstructor().newInstance(), request, response);

                Map<String, String> resultMap = AnalyseXml.getResultAttribute(actionName.substring(0, actionName.indexOf(".")),result, fd);
                if (!resultMap.isEmpty()&& resultMap!= null ) {
                    String resultType = resultMap.get("type");
                    String resultValue = resultMap.get("value");
                    logger.info(resultType+">>>>>>" + resultValue);
                    if (resultType.equals("forward")) {
                        request.getRequestDispatcher(resultValue).forward(request, response);
                    } else if (resultType.equals("redirect")) {
                        response.sendRedirect(resultValue);
                    }
                }
            } else {
                response.sendRedirect("login.jsp");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
