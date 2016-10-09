package dn.stm.shop.model;

import java.io.Serializable;

/**
 *
 * @author home
 */
public class Item implements Serializable {

    private int id;
    private int groupId;
    private String name;
    private ItemUnit itemUnit;
    private int price_1;
    private int price_2;
    private int price_3;
    private String description;
    private String imgFileName;
    private boolean ordered;
    private int amount;

    public Item(int id, int groupId, String name, ItemUnit itemUnit,
            int price_1, int price_2, int price_3, String description, String imgFileName) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
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

    public int getPrice_1() {
        return price_1;
    }

    public void setPrice_1(int price_1) {
        this.price_1 = price_1;
    }

    public int getPrice_2() {
        return price_2;
    }

    public void setPrice_2(int price_2) {
        this.price_2 = price_2;
    }

    public int getPrice_3() {
        return price_3;
    }

    public void setPrice_3(int price_3) {
        this.price_3 = price_3;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Item{" + "id=" + id + ", groupId=" + groupId + ", name=" + name + ", itemUnit=" + itemUnit + ", cost=" + price_1 + ", costs=" + price_2 + ", costt=" + price_3 + '}';
    }
}
