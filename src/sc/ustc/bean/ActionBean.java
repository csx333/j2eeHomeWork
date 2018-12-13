package sc.ustc.bean;

import java.util.ArrayList;

public class ActionBean {
    private String actionName;
    private String actionClass;
    private String actionMethod;
    private ArrayList<InterceptorBean> interceptorBeans;

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

    public ArrayList<InterceptorBean> getInterceptorBeans() {
        return interceptorBeans;
    }

    public void setInterceptorBeans(ArrayList<InterceptorBean> interceptorBeans) {
        this.interceptorBeans = interceptorBeans;
    }
}
