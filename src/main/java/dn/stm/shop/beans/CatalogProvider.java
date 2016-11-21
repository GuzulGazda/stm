package dn.stm.shop.beans;

import dn.stm.shop.model.Item;
import dn.stm.shop.model.ItemGroup;
import dn.stm.shop.utils.ExcelReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author home
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class CatalogProvider implements Serializable{

    private static final Logger LOGGER = Logger.getLogger(CatalogProvider.class.getName());

    public final static String MAIN_GROUP_ID = "00016";
    private final String MAIN_GROUP_NAME = "Все товары каталога";

    private static java.util.Map<String, ItemGroup> groupsMap = new HashMap();
    private static java.util.Map<String, Item> itemsMap = new HashMap();
    private static ItemGroup mainGroup;

    public CatalogProvider() {
        LOGGER.info("================== Catalog provider init ================== ");

        ExcelReader excelReader = new ExcelReader();
        excelReader.readFile();
        List<ItemGroup> groups = excelReader.getAllGroups();
        List<Item> items = excelReader.getAllItems();
        groupsMap = new HashMap();
        itemsMap = new HashMap();
        for (ItemGroup group : groups) {
            groupsMap.put(group.getId(), group);
        }
        for (Item item : items) {
            itemsMap.put(item.getId(), item);
        }
        createMainItemGroup();
//            dataLoaded = DbUploader.loadDb(this);
    }
    
        private void fillGroup(ItemGroup group) {
        for (Map.Entry<String, ItemGroup> entrySet : groupsMap.entrySet()) {
            ItemGroup thisGroup = entrySet.getValue();
            if (group.getId().equals(thisGroup.getParentId())) {
                group.getGroups().add(thisGroup);
            }
        }
        for (Map.Entry<String, Item> entrySet : itemsMap.entrySet()) {
            Item thisItem = entrySet.getValue();
            if (group.getId().equals(thisItem.getGroupId())) {
                group.getItems().add(thisItem);
            }
        }

        for (ItemGroup childGroup : group.getGroups()) {
            fillGroup(childGroup);
        }
    }

    private void createMainItemGroup() {
        System.out.println("MAIN GROU ID  " + mainGroup);
        if (mainGroup != null) {
            return;
        }
        mainGroup = new ItemGroup(MAIN_GROUP_ID, "000", MAIN_GROUP_NAME);
        fillGroup(mainGroup);
    }

    public synchronized void fillCatalog(Catalog catalog) {
        Map<String, ItemGroup> catalogGroupsMap = new HashMap<>();
        for (Map.Entry<String, ItemGroup> entrySet : groupsMap.entrySet()) {
            catalogGroupsMap.put(entrySet.getKey(), entrySet.getValue().clone());
        }
        
        Map<String, Item> catalogItemsMap = new HashMap<>();
        for (Map.Entry<String, Item> entrySet : itemsMap.entrySet()) {
            catalogItemsMap.put(entrySet.getKey(), entrySet.getValue().clone());
        }
        
        catalog.setGroupsMap(catalogGroupsMap);
        catalog.setItemsMap(catalogItemsMap);
        catalog.setMainGroup(mainGroup.clone());
        LOGGER.info("End");
    }
}
