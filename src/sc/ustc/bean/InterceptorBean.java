package sc.ustc.bean;
/**
 * InterceptorBean class
 * @description : 存储拦截器的相关信息
 * @author : csx
 * @date : 2018/12/14
 */
public class InterceptorBean {
    private String interceptorName;
    private String interceptorClass;
    private String interceptorPredo;
    private String interceptorAfterdo;
    public String getInterceptorName() {
        return interceptorName;
    }

    public void setInterceptorName(String interceptorName) {
        this.interceptorName = interceptorName;
    }

    public String getInterceptorClass() {
        return interceptorClass;
    }

    public void setInterceptorClass(String interceptorClass) {
        this.interceptorClass = interceptorClass;
    }

    public String getInterceptorPredo() {
        return interceptorPredo;
    }

    public void setInterceptorPredo(String interceptorPredo) {
        this.interceptorPredo = interceptorPredo;
    }

    public String getInterceptorAfterdo() {
        return interceptorAfterdo;
    }

    public void setInterceptorAfterdo(String interceptorAfterdo) {
        this.interceptorAfterdo = interceptorAfterdo;
    }
}
