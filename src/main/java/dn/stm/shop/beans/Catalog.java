package dn.stm.shop.beans;

import dn.stm.shop.model.Item;
import dn.stm.shop.model.ItemGroup;
import dn.stm.shop.model.ItemUnit;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

@ManagedBean(name = "Catalog")
@SessionScoped
public class Catalog implements Serializable {

    // Constants
    private static final Logger LOGGER = Logger.getLogger(Catalog.class.getName());

    private static final int SHEET = 0;

    public static final String MAIN_GROUP_ID = "00016";

    private static final int COLUMN_NAME = 0;
    private static final int COLUMN_DESCRIPTION = 1;
    private static final int COLUMN_ID = 5;
    private static final int COLUMN_PARENT_ID = 6;
    private static final int COLUMN_UNIT = 7;
    private static final int COLUMN_PRICE_1 = 8;
    private static final int COLUMN_PRICE_2 = 9;
    private static final int COLUMN_PRICE_3 = 10;
    private static final int COLUMN_IMG = 13;

    private final String FILE_NAME = "18102016.XLSX";

    // Variables
    private final List<Item> allItems = new ArrayList<>();          // all items in catalog
    private final List<ItemGroup> allGroups = new ArrayList<>();    // all groups in catalog
    private final List<Item> items = new ArrayList<>();             // items that belong to first level
    private final List<ItemGroup> groups = new ArrayList<>();       // groups that belnong to first level

    // Getters
    public List<Item> getItems() {
        return items;
    }

    public List<ItemGroup> getGroups() {
        return groups;
    }

    public List<Item> getAllItems() {
        return allItems;
    }

    public List<ItemGroup> getAllGroups() {
        return allGroups;
    }

    @PostConstruct
    public void init() {
        loadCatalog();
    }

    private void loadCatalog() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        String name = "";
        try (InputStream inputStream = classLoader.getResourceAsStream(FILE_NAME)) {
            Workbook workbook = WorkbookFactory.create(inputStream);
//            if (FILE_NAME.toLowerCase().endsWith("xlsx")) {
//                workbook = new XSSFWorkbook(fis);
//
//            } else if (FILE_NAME.toLowerCase().endsWith("xls")) {
//                workbook = new HSSFWorkbook(fis);
//            } else {
//                //TODO - not xsl, not xlss
//                return;
//            }
            int numOfSheets = workbook.getNumberOfSheets();

            Sheet sheet = workbook.getSheetAt(SHEET);

            // Get iterator to all the rows in current sheet 
            Iterator<Row> rowIterator = sheet.iterator();
            // Traversing over each row of XLSX file 
//            boolean startReadingData = false;
            boolean read = false;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    name = row.getCell(COLUMN_NAME).getStringCellValue();
                } catch (Exception e) {
                }

                if ("СТРОЙМАТЕРИАЛЫ".equalsIgnoreCase(name)) {
                    read = true;
                    System.out.println("FOIND!!!");
                    continue;
                }
                if (!read) {
                    continue;
                }
                // For each row, iterate through each columns 

                parseRow(row);
            }
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Error during parse xsl {0}", ex.getMessage());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error during parse xsl {0}", ex.getMessage());
        } catch (InvalidFormatException | EncryptedDocumentException ex) {
            LOGGER.log(Level.SEVERE, "Error during parse xsl {0}", ex.getMessage());
        }
    }

    private void parseRow(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        double someDouble;
        try {
            someDouble = row.getCell(COLUMN_PRICE_1).getNumericCellValue();
            if (someDouble == 0.0) {
                parseGroupRow(row);
            } else {
                parseItemRow(row);
            }

        } catch (Exception e) {
            parseGroupRow(row);
        }
    }

    private void parseGroupRow(Row row) {
        try {
            String groupName = row.getCell(COLUMN_NAME).getStringCellValue().trim();
            String groupId = row.getCell(COLUMN_ID).getStringCellValue().trim();
            String parentId = row.getCell(COLUMN_PARENT_ID).getStringCellValue().trim();
            ItemGroup itemGroup = new ItemGroup(groupId, parentId, groupName);

            if (MAIN_GROUP_ID.equals(parentId)) {
                groups.add(itemGroup);
            } else {
                ItemGroup parentGroup = getGroupById(parentId);
                parentGroup.add(itemGroup);
            }
            allGroups.add(itemGroup);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    private void parseItemRow(Row row) {
        try {
            String itemName = row.getCell(COLUMN_NAME).getStringCellValue();
            String itemId = row.getCell(COLUMN_ID).getStringCellValue().trim();
            String parentId = row.getCell(COLUMN_PARENT_ID).getStringCellValue().trim();
            String unitName = row.getCell(COLUMN_UNIT).getStringCellValue().trim();
            ItemUnit itemUnit = ItemUnit.fromString(unitName);
            double price_01 = row.getCell(COLUMN_PRICE_1).getNumericCellValue();
            double price_02 = price_01;
            double price_03 = price_01;
            try {
                price_02 = row.getCell(COLUMN_PRICE_2).getNumericCellValue();
                price_03 = row.getCell(COLUMN_PRICE_2).getNumericCellValue();
            } catch (Exception e) {
                // use price_01 for all prices
            }

            String description = "";
            try {
                description = row.getCell(COLUMN_DESCRIPTION).getStringCellValue().trim();
            } catch (Exception e) {
                // TODO Log oboun description error
            }
            if (description == null || description.isEmpty()) {
                // TODO move String to constant
                description = "Здесь должно быть описание для товара \"" + itemName + "\"";
            }

            String imgFileName;
            try {
                imgFileName = row.getCell(COLUMN_IMG).getStringCellValue().trim();

            } catch (Exception e) {
                // TODO Log oboun description error
                imgFileName = "unknown.jpg";
            }

            int cost_01 = (int) price_01 * 100;
            int cost_02 = (int) price_02 * 100;
            int cost_03 = (int) price_03 * 100;

            Item item = new Item(itemId, parentId, itemName, itemUnit,
                    cost_01, cost_02, cost_03, description, imgFileName);

            if (MAIN_GROUP_ID.equals(parentId)) {
                items.add(item);
            } else {
                ItemGroup parentGroup = getGroupById(parentId);
                parentGroup.add(item);
            }
            allItems.add(item);
        } catch (Exception e) {
            // TODO log to file
        }
    }

    private List<Item> getAllItems(String sortField, boolean sortAscending) {
        return sorted(allItems, sortField, sortAscending);
    }

    List<Item> getGroupItemsListById(String groupId, String sortField, boolean sortAscending) {

        // If groupId == 1 - show all items from catalog
        if (MAIN_GROUP_ID.equals(groupId)) {
            return sorted(allItems, sortField, sortAscending);
        }
        System.out.println("HERE IS ERROR: groupId =  " + groupId + ", sortField = " + sortField + ", sortAsc = " + sortAscending);
        return sorted(getGroupById(groupId).getItems(), sortField, sortAscending);
    }

//    List<ItemGroup> getGroupGroupsListById(String groupId, String sortField, boolean sortAscending) {
//
//        // If groupId == 1 - show all items from catalog
//        if ("1".equals(groupId)) {
//            return groups;
//        }
//        return getGroupById(groupId).getGroups();
//    }
    List<Item> getSearchItemList(String search, String sortField, boolean sortAscending) {
        Set<Item> resultSet = new HashSet<>();

        for (Item item : allItems) {
            if (item.getName().toLowerCase().contains(search.toLowerCase())) {
                resultSet.add(item);
            }
        }

        for (ItemGroup group : allGroups) {
            if (group.getName().toLowerCase().contains(search.toLowerCase())) {
                resultSet.addAll(group.getItems());
            }
        }
        List<Item> result = new ArrayList<>();
        result.addAll(resultSet);
        return sorted(result, sortField, sortAscending);
    }

    private List<Item> sorted(List<Item> items, String sortField, boolean sortAscending) {
        // sort data 
        switch (sortField) {
            case "name":
                if (sortAscending) {
                    Collections.sort(items, ItemNameComparatorAsc);
                } else {
                    Collections.sort(items, ItemNameComparatorDesc);
                }
                break;
            case "price":
                if (sortAscending) {
                    Collections.sort(items, ItemPriceComparatorAsc);
                } else {
                    Collections.sort(items, ItemPriceComparatorDesc);
                }
                break;
            default:
            // nothing to do
        }
        return items;
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
            Integer ItemPrice1 = i1.getPrice_1();
            Integer ItemPrice2 = i2.getPrice_1();

            //ascending order
            return ItemPrice1.compareTo(ItemPrice2);

            //descending order
        }
    };

    /*Comparator for sorting the list by Itet Price DESC */
    public static Comparator<Item> ItemPriceComparatorDesc = new Comparator<Item>() {

        @Override
        public int compare(Item i1, Item i2) {
            Integer ItemPrice1 = i1.getPrice_1();
            Integer ItemPrice2 = i2.getPrice_1();

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

    Item getItemById(String id) {
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
        LOGGER.log(Level.SEVERE, "Item not found for Id {0}", id);
        return null;
    }

    private ItemGroup getGroupById(String id) {
        if (id == null) {

        }
        for (ItemGroup group : allGroups) {
            if (group.getId().equals(id)) {
                return group;
            }
        }
        LOGGER.log(Level.SEVERE, "!!Group not found for Id {0}", id);
        LOGGER.log(Level.SEVERE, "All Groups size is {0}", allGroups.size());
        return null;
    }

}
