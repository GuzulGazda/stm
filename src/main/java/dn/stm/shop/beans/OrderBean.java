package dn.stm.shop.beans;

import dn.stm.shop.model.Item;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

@ManagedBean(name = "Order")
@SessionScoped
public class OrderBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(OrderBean.class.getName());
    private final List<Item> orderedItems = new ArrayList<>();

    @PostConstruct
    private void init() {
        System.out.println("Order:: init");
    }

    public void namedChanged(AjaxBehaviorEvent event) {
        System.out.println("named CHANGED!!!");
//        result = "Hello, you entered " + named;
    }

    public void changeAmount(AjaxBehaviorEvent event) {
        System.out.println("Change amount:::");
    }

    public void add(Item item) {
        LOGGER.log(Level.SEVERE, "Try to add item {0} to order.", item);
        if (item == null) {
            LOGGER.log(Level.SEVERE, "Cannot add item to order - item is null");
            return;
        }
        // do not add item to order if item is already in order
        boolean exists = false;
        for (Item orderedItem : orderedItems) {
            if (orderedItem.getId() == item.getId()) {
                LOGGER.log(Level.SEVERE, "Item {0} is already in a order. Scip adding. ", item);
                exists = true;
                break;
            }
        }
        if (!exists) {
            item.setOrdered(true);
            item.setAmount(1);
            orderedItems.add(item);
            LOGGER.log(Level.SEVERE, "Added {0} to order", item);
        }
    }

    public void removeItemById(int itemId) {
        Item itemToRemove = null;
        for (Item orderedItem : orderedItems) {
            if (orderedItem.getId() == itemId) {
                itemToRemove = orderedItem;
                break;
            }
        }
        if (itemToRemove != null) {
            itemToRemove.setOrdered(false);
            orderedItems.remove(itemToRemove);
            LOGGER.log(Level.SEVERE, "Remove {0} from order", itemToRemove);
        }
    }

    public int getOverallCost() {
        int result = 0;
        for (Item orderedItem : orderedItems) {
            result += orderedItem.getAmount() * orderedItem.getPrice_1();
        }
        return result;
    }

    public int getOverallAmount() {
        int result = 0;
        for (Item orderedItem : orderedItems) {
            result += orderedItem.getAmount();
        }
        LOGGER.log(Level.SEVERE, "Overall amount is {0}", result);
        return result;
    }

    public void setItemAmount(int itemId, int amount) {
        System.out.println("OrderBean:: SetItemAmount");
        for (Item orderedItem : orderedItems) {
            if (orderedItem.getId() == itemId) {
                orderedItem.setAmount(amount);
                return;
            }
        }
    }

    public int getAmountForItem(int itemId) {
        for (Item orderedItem : orderedItems) {
            if (orderedItem.getId() == itemId){
                return orderedItem.getAmount();
            }
        }
        return -1;
    }

    // Getters and Setters
    public List<Item> getOrderedItems() {
        return orderedItems;
    }

    @Override
    public String toString() {
        return "OrderBean{" + "orderedItems=" + orderedItems + '}';
    }
}
