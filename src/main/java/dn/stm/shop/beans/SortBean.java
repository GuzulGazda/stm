package dn.stm.shop.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.event.AjaxBehaviorEvent;

@ManagedBean(name = "Sort")
@SessionScoped
public class SortBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(SortBean.class.getName());

    private static final long serialVersionUID = 1L;

    private static final Map<String, String> sortFields;

    private String sortField = "name";  //default valu
    private boolean sortAsc = true;     // default value
    String result;

    static {
        sortFields = new HashMap<>();
        sortFields.put("По названию", "name");
        sortFields.put("По цене", "price");
    }

    public String sortFieldChanged(AjaxBehaviorEvent event) {
        try {
            sortField = (String) ((UIOutput) event.getSource()).getValue();
            result = sortField;
            LOGGER.log(Level.INFO, "!!!!Change sortField to {0}", sortField);
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Change sortField to {0}", "Error during get sort field value");
            return "error!!";
        }

    }

    public Map<String, String> getCountryInMap() {
        return this.sortFields;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public boolean isSortAsc() {
        return sortAsc;
    }

    public void setSortAsc(boolean sortAsc) {
        this.sortAsc = sortAsc;
    }
}
