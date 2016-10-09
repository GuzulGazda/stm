package dn.stm.shop.beans;

import dn.stm.shop.model.Item;
import dn.stm.shop.model.PriceList;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "Item")
@RequestScoped
public class ItemBean implements Serializable {

    @ManagedProperty(value = "#{Catalog}")
    private Catalog catalog;

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    @ManagedProperty(value = "#{param.itemId}")
    private int itemId;
    private Item item;

    @PostConstruct
    public void init() {

    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Item getItem() {
        if (itemId == 0) {
            return null;
        }
        PriceList priceList = catalog.getPriceList();
        item = priceList.getItemById(itemId);
        return item;
    }

}
