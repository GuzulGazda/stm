package dn.stm.shop.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author home
 */
public class ItemGroup implements Serializable {

    private final int id;
    private final String name;
    private final List<Item> items;

    public ItemGroup(int id, String name) {
        this.id = id;
        this.name = name;
        this.items = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean add(Item e) {
        return items.add(e);
    }

    @Override
    public String toString() {
        return "ItemGroup{" + "id=" + id + ", name=" + name + ", items=" + items + '}';
    }

}
