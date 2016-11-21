package dn.stm.shop.beans;

import dn.stm.shop.model.Item;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

@ManagedBean(name = "Order")
@SessionScoped
public class OrderBean implements Serializable {

    private static final long serialVersionUID = 12345678L;
    private static final Logger LOGGER = Logger.getLogger(OrderBean.class.getName());

    private final double PRICE_1_MAX = 10_000;
    private final double PRICE_2_MAX = 40_000;

    private final List<Item> orderedItems = new ArrayList<>();
    private CostUsed currentCostUsed = CostUsed.PRICE_1;

    // TODO remove this method
    public void namedChanged(AjaxBehaviorEvent event) {
    }

    public void add(Item item) {
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

    public boolean existItem(String itemId) {
        for (Item orderedItem : orderedItems) {
            if (orderedItem.getId().equals(itemId)) {
                return true;
            }
        }
        return false;
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
                currentCostUsed = CostUsed.PRICE_2;
            }
            if (result >= PRICE_2_MAX) {
                result = 0.0;
                for (Item orderedItem : orderedItems) {
                    result += orderedItem.getAmount() * orderedItem.getPrice_3();
                    currentCostUsed = CostUsed.PRICE_3;
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

    public double getCurrentCost(Item item) {
        double result = 0.0;
        switch (currentCostUsed) {
            case PRICE_1:
                result = item.getPrice_1();
                break;
            case PRICE_2:
                result = item.getPrice_1();
                break;
            case PRICE_3:
                result = item.getPrice_1();
                break;
        }
        return result;
    }

    @Override
    public String toString() {
        return "OrderBean{" + "orderedItems=" + orderedItems + '}';
    }

    /**
     * Clear order - remove all items from order
     */
    public void clear() {
        ListIterator<Item> iterator = orderedItems.listIterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            item.setOrdered(false);
        }
        orderedItems.clear();
    }

    public enum CostUsed {

        PRICE_1, PRICE_2, PRICE_3;
    }
}
