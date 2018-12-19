package sc.ustc.tools;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sc.ustc.bean.ActionBean;
import sc.ustc.bean.InterceptorBean;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

public class ActionProxyOfPreAndAfterDo implements MethodInterceptor {
    private static Logger logger = LogManager.getLogger(ActionProxyOfPreAndAfterDo.class.getName());

    public static Object getInstance(Class<?> clazz){
        //创建加强器，用来创建动态代理类
        Enhancer enhancer = new Enhancer();
        //为加强器指定要代理的业务类（即：为下面生成的代理类指定父类）
        enhancer.setSuperclass(clazz);
        ActionProxyOfPreAndAfterDo ap = new ActionProxyOfPreAndAfterDo();
        //设置回调：对于代理类上所有方法的调用，都会调用CallBack，而Callback则需要实现intercept()方法进行拦
        enhancer.setCallback(ap);
        // 创建动态代理类对象并返回
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        ActionBean actionBean = (ActionBean)args[2];
        List<InterceptorBean> interceptorsList = actionBean.getInterceptorBeans();
        logger.info("准备执行predo>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
        for(int i = 0;i<interceptorsList.size();i++){
            String interceptorClassName = interceptorsList.get(i).getInterceptorClass();
            String interceptorPreDo = interceptorsList.get(i).getInterceptorPredo();
            logger.info("拦截器的信息>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +interceptorClassName+"   "+interceptorPreDo);
            Class<?> clazz = Class.forName(interceptorClassName);
            Method preMethod = clazz.getDeclaredMethod(interceptorPreDo, HttpServletRequest.class,
                    HttpServletResponse.class, ActionBean.class);
            preMethod.invoke(clazz.getDeclaredConstructor().newInstance(),(HttpServletRequest) args[0], (HttpServletResponse) args[1], actionBean);
        }
        logger.info("准备执行本体方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );

        //调用业务类（父类中）的方法
        String result = (String)proxy.invokeSuper(obj, args);

        logger.info("准备执行afterdo>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
        for(int i = 0;i<interceptorsList.size();i++){
            String interceptorClassName = interceptorsList.get(i).getInterceptorClass();
            String interceptorAfterDo = interceptorsList.get(i).getInterceptorAfterdo();
            logger.info("拦截器的信息>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +interceptorClassName+"   "+interceptorAfterDo);
            Class<?> clazz = Class.forName(interceptorClassName);
            Method afterMethod = clazz.getDeclaredMethod(interceptorAfterDo, HttpServletRequest.class,
                    HttpServletResponse.class, String.class);
            afterMethod.invoke(clazz.getDeclaredConstructor().newInstance(),(HttpServletRequest) args[0], (HttpServletResponse) args[1],result);
        }
        return result;
    }
}
