package dn.stm.shop.beans;

import dn.stm.shop.model.Category;
import dn.stm.shop.model.Item;
import dn.stm.shop.model.ItemGroup;
import dn.stm.shop.model.ItemUnit;
import dn.stm.shop.model.PriceList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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

    private static final Logger LOGGER = Logger.getLogger(Catalog.class.getName());

    private final String FILE_NAME = "price_stm.xls";
    private static int GROUP_COUNTER = 1;
    private static int ITEM_COUNTER = 200;
    private ItemGroup currentItemGroup;

    private final PriceList priceList = new PriceList();

    private static Object lock = new Object();
    private static volatile Catalog instance = null;

    @PostConstruct
    public void init() {
        System.out.println("Catalog:: init");
        loadCatalog();
    }

    private void loadCatalog() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(FILE_NAME)) {
            System.out.println("");
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

            Sheet sheet = workbook.getSheetAt(0);

            // Get iterator to all the rows in current sheet 
            Iterator<Row> rowIterator = sheet.iterator();
            // Traversing over each row of XLSX file 
//            boolean startReadingData = false;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                // For each row, iterate through each columns 

                // Scip empty rows
                if (row.getCell(2) == null) {
                    continue;
                }
                parseRow(row);
            }
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Error during parse xsl {0}", ex.getMessage());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error during parse xsl {0}", ex.getMessage());
        } catch (InvalidFormatException | EncryptedDocumentException ex) {
            LOGGER.log(Level.SEVERE, "Error during parse xsl {0}", ex.getMessage());
        }
        LOGGER.log(Level.INFO, "Catalog loaded. Number of groups:  {0}, number of items is: {1}", new Object[]{GROUP_COUNTER, ITEM_COUNTER});
        LOGGER.log(Level.INFO, "\t\t. Prce list contains:  {0} items, and {1} groups", new Object[]{priceList.getNumOfItems(), priceList.getNumOfGroups()});

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("priceList", priceList);
    }

    private void parseRow(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        try {
            if (row.getCell(3).getNumericCellValue() == 0.0) {
                parseGroupRow(row);
            } else {
                parseItemRow(row);
            }
        } catch (Exception e) {
        }

    }

    private void parseGroupRow(Row row) {
        String groupName = row.getCell(1).getStringCellValue().trim();
        String categoryName = row.getCell(2).getStringCellValue().trim();
        if (categoryName.isEmpty()) {
            return;
        }
        Category category = priceList.getCategoryByName(categoryName);
        ItemGroup itemGroup = new ItemGroup(GROUP_COUNTER++, groupName);
        category.add(itemGroup);
        currentItemGroup = itemGroup;
    }

    private void parseItemRow(Row row) {
        try {
            String itemName = row.getCell(1).getStringCellValue().trim();
            String unitName = row.getCell(2).getStringCellValue().trim();
            ItemUnit itemUnit = ItemUnit.fromString(unitName);
            double price_01 = row.getCell(3).getNumericCellValue();
            double price_02 = row.getCell(4).getNumericCellValue();
            double price_03 = row.getCell(5).getNumericCellValue();
            String description;
            try {
                description = row.getCell(6).getStringCellValue().trim();

            } catch (Exception e) {
                // TODO Log oboun description error
                description = "Здесь должно быть описание для товара \"" + itemName + "\"";
            }

            String imgFileName;
            try {
                imgFileName = row.getCell(7).getStringCellValue().trim();

            } catch (Exception e) {
                // TODO Log oboun description error
                imgFileName = "unknown.jpg";
            }

            int cost_01 = (int) price_01 * 100;
            int cost_02 = (int) price_02 * 100;
            int cost_03 = (int) price_03 * 100;

            Item item = new Item(ITEM_COUNTER++, currentItemGroup.getId(), itemName, itemUnit,
                    cost_01, cost_02, cost_03, description, imgFileName);
            currentItemGroup.add(item);
        } catch (Exception e) {
            // TODO log to file
        }
    }

    public PriceList getPriceList() {
        return priceList;
    }

    private List<Item> getAllItems(String sortField, boolean sortAscending) {
        System.out.println("Catalog::: Get Item List: sortField: " + sortField + ", sortAsc = " + sortAscending);
        List<Item> allItems = new ArrayList<>();
        List<Category> categorys = priceList.getCategorys();
        for (Category category : categorys) {
            allItems.addAll(category.getAllItemsUnsorted());
        }
        return sorted(allItems, sortField, sortAscending);
    }

    List<Item> getGroupList(int groupId, String sortField, boolean sortAscending) {
        System.out.println("Catalog::: getGrpoupList " + groupId);

        // If groupId == 1 - show all items from catalog
        if (groupId == 1) {
            return getAllItems(sortField, sortAscending);
        }

        List<Item> items = new ArrayList<>();
        List<Category> categorys = priceList.getCategorys();
        for (Category category : categorys) {
            if (category.getId() == groupId) {
                return sorted(category.getAllItemsUnsorted(), sortField, sortAscending);
            }
            List<ItemGroup> itemGroups = category.getItemGroups();
            for (ItemGroup itemGroup : itemGroups) {
                if (itemGroup.getId() == groupId) {
                    return sorted(itemGroup.getItems(), sortField, sortAscending);
                }
            }
        }
        LOGGER.log(Level.SEVERE, "Cannot find group for groupId {}. ", groupId);
        return new ArrayList<>();
    }

    List<Item> getCategoryList(int categoryId, String sortField, boolean sortAscending) {
        System.out.println("Catalog::: getCategoryList " + categoryId);
        List<Item> items = new ArrayList<>();
        List<Category> categorys = priceList.getCategorys();
        for (Category category : categorys) {
            if (category.getId() == categoryId) {
                List<ItemGroup> itemGroups = category.getItemGroups();
                for (ItemGroup itemGroup : itemGroups) {
                    items.addAll(itemGroup.getItems());
                }
                break;
            }
        }
        return sorted(items, sortField, sortAscending);
    }

    List<Item> getSearchItemList(String search, String sortField, boolean sortAscending) {
        List<Item> allItems = getAllItems(sortField, sortAscending);
        List<Item> result = new ArrayList<>();

        List<Category> categorys = priceList.getCategorys();
        for (Category category : categorys) {
            List<ItemGroup> groups = category.getItemGroups();
            for (ItemGroup group : groups) {
                if (group.getName().toLowerCase().contains(search.toLowerCase())) {
                    result.addAll(group.getItems());
                } else {
                    List<Item> items = group.getItems();
                    for (Item item : items) {
                        if (item.getName().toLowerCase().contains(search.toLowerCase())) {
                            result.add(item);
                        }
                    }
                }
            }
        }

        return sorted(result, sortField, sortAscending);
    }

    private List<Item> sorted(List<Item> items, String sortField, boolean sortAscending) {
        // sort data 
        switch (sortField) {
            case "name":
                if (sortAscending) {
                    Collections.sort(items, ItemNameComparatorAsc);
                    System.out.println("sorted ASC");
                } else {
                    Collections.sort(items, ItemNameComparatorDesc);
                    System.out.println("sorted DESC");
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

    String getGroupNameById(int groupId) {
        List<Category> categorys = priceList.getCategorys();
        for (Category category : categorys) {
            List<ItemGroup> itemGroups = category.getItemGroups();
            for (ItemGroup itemGroup : itemGroups) {
                if (itemGroup.getId() == groupId) {
                    return itemGroup.getName();
                }
            }
        }
        return null;
    }

    String getCategoryNameById(int categoryId) {
        List<Category> categorys = priceList.getCategorys();
        for (Category category : categorys) {
            if (category.getId() == categoryId) {
                return category.getName();
            }
        }
        return null;
    }

    Item getItemById(int itemId) {
        List<Item> allItems = getAllItems("name", true);
        for (Item item : allItems) {
            if (item.getId() == itemId) {
                return item;
            }
        }
        return null;
    }

}
