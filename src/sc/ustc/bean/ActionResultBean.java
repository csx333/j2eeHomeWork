package sc.ustc.bean;

public class ActionResultBean {
    private String actionResultName;
    private String actionResultType;
    private String actionResultValue;
    private ActionBean actionResult;

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

    public ActionBean getActionResult() {
        return actionResult;
    }

    public void setActionResult(ActionBean actionResult) {
        this.actionResult = actionResult;
    }
}
