package dn.stm.shop.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author home
 */
public class Item implements Serializable, Cloneable {

    private String id;                  // item id
    private String groupId;             // item group id
    private String name;                // item name
    private ItemUnit itemUnit;          // item  unit
    private final double price_1;       // item price 1
    private final double price_2;       // item price 2 
    private final double price_3;       // item price 3
    private String description;         // item description
    private boolean ordered;            // true if item is ordered
    private double amount;              // amoumt of ordered item - cam be partoa;
//    private String imgFileName;         // name of the file in resources/img/items folder

    /**
     * Constructor
     * 
     * @param id - item id   
     * @param groupId - item group id
     * @param name - item name
     * @param itemUnit - item's ItemUnit
     * @param price_1 - item price 1
     * @param price_2 - item price 2
     * @param price_3 - item price 3
     * @param description - item description
     */
    public Item(String id, String groupId, String name, ItemUnit itemUnit,
            double price_1, double price_2, double price_3, String description) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.itemUnit = itemUnit;
        this.price_1 = price_1;
        this.price_2 = price_2;
        this.price_3 = price_3;
        this.ordered = false;
        this.description = description;
        this.ordered = false;
        this.amount = 0;
        // find item image in resources/img/items
//        String path = new String()
//        try {
//            File imgFile = new File
//        } catch (Fil e) {
//        }
    }


    private Item(Item anotherItem) {
        this.id = anotherItem.getId();
        this.groupId = anotherItem.getGroupId();
        this.name = anotherItem.getName();
        this.itemUnit = anotherItem.getItemUnit();
        this.price_1 = anotherItem.getPrice_1();
        this.price_2 = anotherItem.getPrice_2();
        this.price_3 = anotherItem.getPrice_3();
        this.description = anotherItem.getDescription();
        this.ordered = false;
        this.amount = 0;
    }
    
    @Override
    public Item clone() {
        return new Item(this);
    }


// Getters and setters start
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemUnit getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(ItemUnit itemUnit) {
        this.itemUnit = itemUnit;
    }

    public double getPrice_1() {
        return price_1;
    }

    public double getPrice_2() {
        return price_2;
    }

    public double getPrice_3() {
        return price_3;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    // Getters and setters end
    
    /**
     * TODO
     * @return true if amount of this item can be partial
     */
    public boolean canBePartial() {
        if (itemUnit == ItemUnit.EDINICA
                || itemUnit == ItemUnit.SHTUKA
                || itemUnit == ItemUnit.PARA) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item other = (Item) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }   
  
}
