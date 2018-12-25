package sc.ustc.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sc.ustc.bean.ActionBean;
import sc.ustc.bean.ActionResultBean;
import sc.ustc.di.ClassPathXmlApplicationContext;
import sc.ustc.tools.ActionProxyOfPreAndAfterDo;
import sc.ustc.tools.AnalyseXml;
import sc.ustc.tools.BasicXslt;

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
        String result = null;
        String actionName = request.getServletPath();
        {
            logger.info("正在访问的action路径>>>>>>>>>" + request.getRequestURL());
            logger.info("正在访问的action路径>>>>>>>>>" + actionName);
        }
        String[] actionUrl = actionName.split("/");
        actionName = actionUrl[actionUrl.length - 1];
        logger.info("正在访问的action名字（带后缀）>>>>>>>>>>>" + actionName);

        String path = request.getSession().getServletContext().getRealPath("WEB-INF/classes/controller.xml");
        logger.info("将要访问并解析的controller.xml的路径是>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + path);
        try {
            File fd = new File(path);
            //根据action名字解析controller.xml后返回actionBean（封装了controller.xml中action节点信息）
            ActionBean actionBean = AnalyseXml.getActionAttributeAndInterceptorAttribute(
                    actionName.substring(0,actionName.indexOf(".")), fd);
            if ( actionBean!= null ) {
                logger.info("返回的了actionBean的名字>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + actionBean.getActionName());

                //从actionBean取得action的class和method的名字，利用反射取得class和Method类
                String className = actionBean.getActionClass();
                String methodName = actionBean.getActionMethod();
                //取得di.xml路径
                File fileDi = new File(request.getSession().getServletContext().getRealPath("WEB-INF/classes/di.xml"));
                ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(fileDi);
                //取得className的bean实例
                Object actionObject = classPathXmlApplicationContext.getBean(className);

                if(actionObject!=null){

                }
                Class clazz = Class.forName(className);
                Method m = clazz.getDeclaredMethod(methodName,
                        HttpServletRequest.class, HttpServletResponse.class, ActionBean.class);

                //判断该action是否配置拦截器
                if(actionBean.getInterceptorBeans().size()>0){
                    logger.info("准备交给代理>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
                    Object actionProxy = ActionProxyOfPreAndAfterDo.getInstance(clazz);
                    logger.info("交给代理了>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + actionProxy.getClass().getName());
                    result = (String) m.invoke(actionProxy, request, response,actionBean);
                }else {
                    logger.info("没有交给代理了>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
                    //执行相应action的方法
                    result = (String) m.invoke(clazz.getDeclaredConstructor().newInstance(),
                            request, response,actionBean);
                }
                logger.info("正在获取result的信息>>>>>>>>>>>>>>>>>>>>>>>>>>>"+result);
                ActionResultBean actionResultBean = AnalyseXml.getActionResultAttribute(
                        actionName.substring(0, actionName.indexOf(".")),result, fd);

                if (actionResultBean!= null ) {
                    String resultType = actionResultBean.getActionResultType();
                    String resultValue = actionResultBean.getActionResultValue();
                    logger.info(resultValue+">>>>>>>>>>>>>>>>>>" + resultValue);
                    //判断是否带后缀*_view.xml
                    String[] resultValueName = resultValue.split("_");
                    String resultValueSuffix =  resultValueName[resultValueName.length - 1];
                    logger.info("resultValueSuffix（带后缀）>>>>>>>>>>>" + resultValueSuffix);
                    if(resultValueSuffix == "view.xml"){
                        //将xml转换为html
                        BasicXslt.transformXmlByXslt(request,response);
                    }
                    else if (resultType.equals("forward")) {
                        logger.info("正在送往>>>>>>>>>>>>>>>>>>" + resultValue);
                        request.getRequestDispatcher(resultValue).forward(request, response);
                        logger.info("正在访问的action路径>>>>>>>>>" + request.getRequestURL());
                    } else if (resultType.equals("redirect")) {
                        logger.info("正在送往>>>>>>>>>>>>>>>>>>" + resultValue);
                        response.sendRedirect(resultValue);
                        logger.info("正在访问的action路径>>>>>>>>>" + request.getRequestURL());
                    }
                }
            } else {
                logger.info("不可识别的action请求，正在返回登陆界面>>>>>>>>>>>>>>>>>>" );
                response.sendRedirect("login.jsp");
                logger.info("正在访问的action路径>>>>>>>>>" + request.getRequestURL());
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
