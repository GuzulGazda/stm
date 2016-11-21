package dn.stm.shop.beans;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "Search")
@SessionScoped
public class SearchBean implements Serializable {

    private String searchString = "";
    private boolean active = false;

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
        active = searchString.trim().length() > 0;
    }

    public boolean isActive() {
        return active;
    }
}
