package sc.ustc.bean;

import java.util.List;
/**
 * ActionBean class
 * @description : 存储Action相关信息
 * @author : csx
 * @date : 2018/12/14
 */
public class ActionBean {
    private String actionName;
    private String actionClass;
    private String actionMethod;
    /**
     * interceptorBeans
     * 相应Action的的拦截器
     */
    private List<InterceptorBean> interceptorBeans;

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionClass() {
        return actionClass;
    }

    public void setActionClass(String actionClass) {
        this.actionClass = actionClass;
    }

    public String getActionMethod() {
        return actionMethod;
    }

    public void setActionMethod(String actionMethod) {
        this.actionMethod = actionMethod;
    }

    public List<InterceptorBean> getInterceptorBeans() {
        return interceptorBeans;
    }

    public void setInterceptorBeans(List<InterceptorBean> interceptorBeans) {
        this.interceptorBeans = interceptorBeans;
    }
}
