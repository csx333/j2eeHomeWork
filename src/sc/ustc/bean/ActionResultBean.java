package sc.ustc.bean;
/**
 * ActionResultBean class
 * @description : 存储Action返回的result相关信息
 * @author : csx
 * @date : 2018/12/14
 */
public class ActionResultBean {
    private String actionResultName;
    private String actionResultType;
    private String actionResultValue;

    public String getActionResultName() {
        return actionResultName;
    }

    public void setActionResultName(String actionResultName) {
        this.actionResultName = actionResultName;
    }

    public String getActionResultType() {
        return actionResultType;
    }

    public void setActionResultType(String actionResultType) {
        this.actionResultType = actionResultType;
    }

    public String getActionResultValue() {
        return actionResultValue;
    }

    public void setActionResultValue(String actionResultValue) {
        this.actionResultValue = actionResultValue;
    }
}
