package sc.ustc.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sc.ustc.bean.ActionBean;
import sc.ustc.bean.ActionResultBean;
import sc.ustc.tools.ActionProxyOfPreAndAfterDo;
import sc.ustc.tools.AnalyseXml;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        logger.info("正在访问的action路径>>>>>>>>>" + actionName);
        String[] actionUrl = actionName.split("/");
        actionName = actionUrl[actionUrl.length - 1];
        logger.info("正在访问的action名字（带后缀）>>>>>>>>>>>" + actionName);
        String path = request.getSession().getServletContext().getRealPath("WEB-INF/classes/controller.xml");
        logger.info("将要访问并解析的controller.xml的路径是>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + path);
        try {
            File fd = new File(path);
            ActionBean actionBean = AnalyseXml.getActionAttributeAndInterceptorAttribute(actionName.substring(0, actionName.indexOf(".")), fd);
            if ( actionBean!= null ) {
                logger.info("返回的了actionBean的名字>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + actionBean.getActionName());
                String className = actionBean.getActionClass();
                String methodName = actionBean.getActionMethod();
                Class clazz = Class.forName(className);
                Method m = clazz.getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class,ActionBean.class);
                String result = null;
                if(actionBean.getInterceptorBeans().size()>0){
                    logger.info("准备交给代理>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
                    Object actionProxy = ActionProxyOfPreAndAfterDo.getInstance(clazz);
                    logger.info("交给代理了>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + actionProxy.getClass().getName());
                    result = (String) m.invoke(actionProxy, request, response,actionBean);
                }else {
                    logger.info("没有交给代理了>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
                    result = (String) m.invoke(clazz.getDeclaredConstructor().newInstance(), request, response,actionBean);
                }
                logger.info("正在获取result的信息>>>>>>>>>>>>>>>>>>>>>>>>>>>"+result);
                ActionResultBean actionResultBean = AnalyseXml.getActionResultAttribute(actionName.substring(0, actionName.indexOf(".")),result, fd);
                if (actionResultBean!= null ) {
                    String resultType = actionResultBean.getActionResultType();
                    String resultValue = actionResultBean.getActionResultValue();
                    logger.info(resultValue+">>>>>>>>>>>>>>>>>>" + resultValue);
                    if (resultType.equals("forward")) {
                        logger.info("正在送往>>>>>>>>>>>>>>>>>>" + resultValue);
                        request.getRequestDispatcher(resultValue).forward(request, response);
                    } else if (resultType.equals("redirect")) {
                        logger.info("正在送往>>>>>>>>>>>>>>>>>>" + resultValue);
                        response.sendRedirect(resultValue);
                    }
                }
            } else {
                logger.info("正在送往>>>>>>>>>>>>>>>>>>login.jsp" );
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
