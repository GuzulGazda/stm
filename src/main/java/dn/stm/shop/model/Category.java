package dn.stm.shop.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author home
 */
public class Category implements Serializable {

    private final int id;
    private final String name;
    private final List<ItemGroup> itemGroups;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
        this.itemGroups = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ItemGroup> getItemGroups() {
        return itemGroups;
    }

    public boolean add(ItemGroup e) {
        return itemGroups.add(e);
    }

    @Override
    public String toString() {
        return "Category{" + "id=" + id + ", name=" + name + ", itemGroups=" + itemGroups.size() + '}';
    }

    public List<Item> getAllItemsUnsorted() {
        List<Item> result = new ArrayList<>();
        for (ItemGroup itemGroup : itemGroups) {
            result.addAll(itemGroup.getItems());
        }
        return result;
    }

}
