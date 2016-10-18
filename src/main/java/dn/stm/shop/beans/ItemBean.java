package dn.stm.shop.beans;

import dn.stm.shop.model.Item;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "Item")
@RequestScoped
public class ItemBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ItemBean.class.getName());

    @ManagedProperty(value = "#{Catalog}")
    private Catalog catalog;

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    @ManagedProperty(value = "#{param.itemId}")
    private String itemId;
    private Item item;

    @PostConstruct
    public void init() {

    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Item getItem() {
        if (itemId == null || itemId.isEmpty()) {
            return null;
        }
        LOGGER.log(Level.SEVERE, "Get item with id {0}", itemId);

        item = catalog.getItemById(itemId);
        return item;
    }

}
