package dn.stm.shop.model;

import java.io.Serializable;

/**
 *
 * @author home
 */
public class Item implements Serializable {

    private String id;
    private String groupId;
    private String name;
    private ItemUnit itemUnit;
    private final double price_1;
    private final double price_2;
    private final double price_3;
    private String description;
    private String imgFileName;
    private boolean ordered;
    private double amount;

    public Item(String id, String groupId, String name, ItemUnit itemUnit,
            double price_1, double price_2, double price_3, String description, String imgFileName) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.itemUnit = itemUnit;
        this.price_1 = price_1;
        this.price_2 = price_2;
        this.price_3 = price_3;
        this.ordered = false;
        this.description = description;
        this.imgFileName = imgFileName;
        this.ordered = false;
        this.amount = 0;
    }

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

    public String getImgFileName() {
        System.out.println("IMAGE IS " + imgFileName);
        return imgFileName;
    }

    public void setImgFileName(String imgFileName) {
        this.imgFileName = imgFileName;
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

    public boolean canBePartial() {
        if (itemUnit == ItemUnit.EDINICA
                || itemUnit == ItemUnit.SHTUKA
                || itemUnit == ItemUnit.PARA) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Item{" + "id=" + id + ", groupId=" + groupId + ", name=" + name + ", itemUnit=" + itemUnit + ", cost=" + price_1 + ", costs=" + price_2 + ", costt=" + price_3 + '}';
    }
}
