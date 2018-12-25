package sc.ustc.bean;

import java.util.List;
import java.util.Map;

/**
 * @author : csx
 * @description : or_mapping.xml中class节点信息
 * @date : 2018/12/19 20:31
 */
public class AccountConfigBean {
    String name ;
    String table;
    DAOClassPropertyBean pkDaoClassPropertyBean;
    List<DAOClassPropertyBean> daoClassPropertyBeans;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public DAOClassPropertyBean getPkDaoClassPropertyBean() {
        return pkDaoClassPropertyBean;
    }

    public void setPkDaoClassPropertyBean(DAOClassPropertyBean pkDaoClassPropertyBean) {
        this.pkDaoClassPropertyBean = pkDaoClassPropertyBean;
    }

    public List<DAOClassPropertyBean> getDaoClassPropertyBeans() {
        return daoClassPropertyBeans;
    }

    public void setDaoClassPropertyBeans(List<DAOClassPropertyBean> daoClassPropertyBeans) {
        this.daoClassPropertyBeans = daoClassPropertyBeans;
    }
}
