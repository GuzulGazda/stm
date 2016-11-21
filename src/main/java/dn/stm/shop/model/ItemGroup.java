package dn.stm.shop.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author home
 */
public class ItemGroup implements Serializable, Cloneable {

    private final String id;                // group id
    private final String parentId;          // parent group id
    private final String name;              // group name
    private final Set<Item> items;         // id of items that belong to this group
    private final List<ItemGroup> groups;   // id of groups that belong to this group

    /**
     * Constructor
     *
     * @param id - group id
     * @param parentId - id of parent group
     * @param name - name of group
     */
    public ItemGroup(String id, String parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.items = new HashSet<>();
        this.groups = new ArrayList();
    }

    /**
     * Copy constructor
     *
     *
     */
    private ItemGroup(ItemGroup anotherGroup) {
        this.id = anotherGroup.getId();
        this.parentId = anotherGroup.getParentId();
        this.name = anotherGroup.getName();
        this.items = new HashSet<>();
        this.groups = new ArrayList();

        for (Item item : anotherGroup.getItems()) {
            this.items.add(item.clone());
        }
        for (ItemGroup group : anotherGroup.getGroups()) {
            this.groups.add(group.clone());
        }
    }

    // Getters and setters start
    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public Set<Item> getItems() {
        Set<Item> result = items;
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

    // Getters and setters end
    @Override
    public ItemGroup clone() {
        return new ItemGroup(this);
    }

}
