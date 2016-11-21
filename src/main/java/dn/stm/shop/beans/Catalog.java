package dn.stm.shop.beans;

import dn.stm.shop.model.Item;
import dn.stm.shop.model.ItemGroup;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "Catalog", eager = true)
@SessionScoped
public class Catalog implements Serializable {

    @ManagedProperty("#{catalogProvider}")
    private CatalogProvider catalogProvider;

    // Constants
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Catalog.class.getName());

    public final static String MAIN_GROUP_ID = "00016";
    private final String MAIN_GROUP_NAME = "Все товары каталога";
    // Variables

    private java.util.Map<String, ItemGroup> groupsMap = new HashMap();
    private java.util.Map<String, Item> itemsMap = new HashMap();
//    private final List<Item> items = new ArrayList<>();          // all items in catalog
//    private final List<ItemGroup> groups = new ArrayList<>();    // all groups in catalog
    private ItemGroup mainGroup;

    private boolean dataLoaded = false;

    // Getters
    public Map<String, ItemGroup> getGroupsMap() {
        return groupsMap;
    }

    public Map<String, Item> getItemsMap() {
        return itemsMap;
    }

    public List<ItemGroup> getTopLevelGroups() {
        return mainGroup.getGroups();
    }

    public void setGroupsMap(Map<String, ItemGroup> groupsMap) {
        this.groupsMap = groupsMap;
    }

    public void setItemsMap(Map<String, Item> itemsMap) {
        this.itemsMap = itemsMap;
    }

    public void setMainGroup(ItemGroup mainGroup) {
        this.mainGroup = mainGroup;
    }

    public void setCatalogProvider(CatalogProvider catalogProvider) {
        this.catalogProvider = catalogProvider;
    }

    public Catalog() {
//
//            LOGGER.info("========================== Catalog constructor  " + mainGroup);
//            this.groupsMap = new HashMap<>();
//            this.itemsMap = new HashMap<>();
//            this.mainGroup = null;
//            catalogProvider.fillCatalog(this);
    }

    @PostConstruct
    public void init() {
        if (mainGroup == null) {
            LOGGER.info("========================== IHOR Catalog init  mainGroup is  " + mainGroup);
            this.groupsMap = new HashMap<>();
            this.itemsMap = new HashMap<>();
            this.mainGroup = null;
            catalogProvider.fillCatalog(this);
        }
    }

    List<Item> getItemsListByGroupId(String groupId, String sortField, boolean sortAscending) {
        Set<Item> result = new HashSet();
        // If groupId == 1 - show all items from catalog
        if (MAIN_GROUP_ID.equals(groupId)) {
            for (Map.Entry<String, Item> entrySet : itemsMap.entrySet()) {
                result.add(entrySet.getValue());
            }
            return sorted(result, sortField, sortAscending);
        }
        ItemGroup group = getGroupById(groupId);
        if (group != null) {
            return sorted(getGroupById(groupId).getItems(), sortField, sortAscending);
        }
        return sorted(mainGroup.getItems(), sortField, sortAscending);
    }

    public List<Item> getSearchItemList(String search, String sortField, boolean sortAscending) {
        Set<Item> resultSet = new HashSet<>();
        for (Map.Entry<String, Item> entrySet : itemsMap.entrySet()) {
            Item item = entrySet.getValue();
            if (item.getName().toLowerCase().contains(search.toLowerCase())) {
                resultSet.add(item);
            }
        }
//        List<Item> result = new ArrayList<>();
//        result.addAll(resultSet);
        return sorted(resultSet, sortField, sortAscending);
    }

    private List<Item> sorted(Set<Item> items, String sortField, boolean sortAscending) {
        List<Item> itemList = new ArrayList<>(items);
        // sort data 
        switch (sortField) {
            case "name":
                if (sortAscending) {
                    Collections.sort(itemList, ItemNameComparatorAsc);
                } else {
                    Collections.sort(itemList, ItemNameComparatorDesc);
                }
                break;
            case "price":
                if (sortAscending) {
                    Collections.sort(itemList, ItemPriceComparatorAsc);
                } else {
                    Collections.sort(itemList, ItemPriceComparatorDesc);
                }
                break;
            default:
            // nothing to do
        }
        return itemList;
    }

    /*Comparator for sorting the list by Item Name ASC */
    public static Comparator<Item> ItemNameComparatorAsc = new Comparator<Item>() {

        @Override
        public int compare(Item i1, Item i2) {
            String ItemName1 = i1.getName().toUpperCase();
            String ItemName2 = i2.getName().toUpperCase();

            //ascending order
            return ItemName1.compareTo(ItemName2);
        }
    };

    /*Comparator for sorting the list by Item Name DESC */
    public static Comparator<Item> ItemNameComparatorDesc = new Comparator<Item>() {

        @Override
        public int compare(Item i1, Item i2) {
            String ItemName1 = i1.getName().toUpperCase();
            String ItemName2 = i2.getName().toUpperCase();

//	   descending order
            return ItemName2.compareTo(ItemName1);
        }
    };
    /*Comparator for sorting the list by Itet Price ASC */
    public static Comparator<Item> ItemPriceComparatorAsc = new Comparator<Item>() {

        @Override
        public int compare(Item i1, Item i2) {
            Double ItemPrice1 = i1.getPrice_1();
            Double ItemPrice2 = i2.getPrice_1();

            //ascending order
            return ItemPrice1.compareTo(ItemPrice2);

        }
    };

    /*Comparator for sorting the list by Itet Price DESC */
    public static Comparator<Item> ItemPriceComparatorDesc = new Comparator<Item>() {

        @Override
        public int compare(Item i1, Item i2) {
            Double ItemPrice1 = i1.getPrice_1();
            Double ItemPrice2 = i2.getPrice_1();

            //descending order
            return ItemPrice2.compareTo(ItemPrice1);
        }
    };

    String getGroupNameById(String groupId) {
        ItemGroup group = getGroupById(groupId);
        if (group != null) {

            return group.getName();
        } else {
            return null;
        }
    }

    public Item getItemById(String id) {
        return itemsMap.get(id);
    }

    private ItemGroup getGroupById(String id) {
        return groupsMap.get(id);
    }

}