package dn.stm.shop.beans;

import dn.stm.shop.model.Item;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

@ManagedBean(name = "Order")
@SessionScoped
public class OrderBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(OrderBean.class.getName());

    private final double PRICE_1_MAX = 10_000;
    private final double PRICE_2_MAX = 40_000;

    private final List<Item> orderedItems = new ArrayList<>();

    // TODO remove this method
    public void namedChanged(AjaxBehaviorEvent event) {
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
            if (orderedItem.getId().equals(item.getId())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            item.setOrdered(true);
            item.setAmount(1);
            orderedItems.add(item);
        }
    }

    public void removeItemById(String itemId) {
        Item itemToRemove = null;
        for (Item orderedItem : orderedItems) {
            if (orderedItem.getId().equals(itemId)) {
                itemToRemove = orderedItem;
                break;
            }
        }
        if (itemToRemove != null) {
            itemToRemove.setOrdered(false);
            orderedItems.remove(itemToRemove);
        }
    }

    public double getOverallCost() {
        double result = 0.0;
        for (Item orderedItem : orderedItems) {
            result += orderedItem.getAmount() * orderedItem.getPrice_1();
        }
        if (result >= PRICE_1_MAX) {
            result = 0.0;
            for (Item orderedItem : orderedItems) {
                result += orderedItem.getAmount() * orderedItem.getPrice_2();
            }
            if (result >= PRICE_2_MAX) {
                result = 0.0;
                for (Item orderedItem : orderedItems) {
                    result += orderedItem.getAmount() * orderedItem.getPrice_3();
                }
            }
        }
        return result;
    }

    public double getOverallAmount() {
        double result = 0;
        for (Item orderedItem : orderedItems) {
            result += orderedItem.getAmount();
        }
        LOGGER.log(Level.SEVERE, "Overall amount is {0}", result);
        return result;
    }

    public String getOrderedItemsCount() {
        return Integer.toString(orderedItems.size());
    }

    public void setItemAmount(String itemId, double amount) {
        for (Item orderedItem : orderedItems) {
            if (orderedItem.getId().equals(itemId)) {
                orderedItem.setAmount(amount);
                return;
            }
        }
    }

    public double getAmountForItem(String itemId) {
        for (Item orderedItem : orderedItems) {
            if (orderedItem.getId().equals(itemId)) {
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
