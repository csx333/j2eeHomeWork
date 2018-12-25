package sc.ustc.bean;

/**
 * @author : csx
 * @description :
 * @date : 2018/12/19 20:35
 */
public class DAOClassPropertyBean {
    String name;
    String column;
    String type;
    String lazy;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLazy() {
        return lazy;
    }

    public void setLazy(String lazy) {
        this.lazy = lazy;
    }
}
