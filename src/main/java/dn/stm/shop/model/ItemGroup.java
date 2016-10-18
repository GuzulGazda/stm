package dn.stm.shop.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author home
 */
public class ItemGroup implements Serializable {

    private final String id;
    private final String parentId;
    private final String name;
    private final List<Item> items;
    private final List<ItemGroup> groups;

    public ItemGroup(String id, String parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.items = new ArrayList<>();
        this.groups = new ArrayList();
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public List<Item> getItems() {
        List<Item> result = items;
        for (ItemGroup itemGroup : groups) {
            result.addAll(itemGroup.getItems());
        }
        return result;
    }

    public List<ItemGroup> getGroups() {
        return groups;
    }

    public boolean add(Item e) {
        return items.add(e);
    }

    public boolean add(ItemGroup group) {
        return groups.add(group);
    }

    @Override
    public String toString() {
        return "ItemGroup{" + "id=" + id + ", name=" + name + ", items=" + items + '}';
    }

    public ItemGroup getGroupById(String id) {
        ItemGroup result = null;
        for (ItemGroup group : groups) {
            if (group.getId().equals(id)) {
                return group;
            }
            result = group.getGroupById(id);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public Item getItemById(String id) {
        Item result = null;
        for (Item item : items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        for (ItemGroup group : groups) {
            result = group.getItemById(id);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
