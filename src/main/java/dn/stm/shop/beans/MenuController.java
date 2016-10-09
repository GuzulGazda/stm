package dn.stm.shop.beans;

import dn.stm.shop.model.MenuItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

@ManagedBean(name = "Menu")
@RequestScoped
public class MenuController implements Serializable {

    private List<MenuItem> menuItems;

    @PostConstruct
    private void init() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        
        MenuItem mainPage = new MenuItem("index.xhtml", "Главная", "first");
        MenuItem aboutPage = new MenuItem("about.xhtml", "О компании", "");
        MenuItem catalogPage = new MenuItem("catalog.xhtml", "Продукция","");
        MenuItem actionsPage = new MenuItem("actions.xhtml", "Акции","");
        MenuItem contactsPage = new MenuItem("contacts.xhtml", "Контакты", "");
        
        
        String url =  req.getRequestURI();
        
        menuItems = new ArrayList<>();
        
        menuItems.add(mainPage);
        menuItems.add(aboutPage);
        menuItems.add(catalogPage);
        menuItems.add(actionsPage);
        menuItems.add(contactsPage);
        
        if (url == null || url.isEmpty()){
            mainPage.setHrefCss("active");
            return;
        }
        
        boolean pageSet = false;
        for (MenuItem menuItem : menuItems) {
            if (url.toLowerCase().contains(menuItem.getUrl().toLowerCase())){
                menuItem.setHrefCss("active");
                pageSet = true;
                break;
            }
        }
        
        if (!pageSet){
            mainPage.setHrefCss("active");
        }
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

}
