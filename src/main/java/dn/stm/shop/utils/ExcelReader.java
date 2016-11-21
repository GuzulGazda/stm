package dn.stm.shop.utils;

import dn.stm.shop.model.Item;
import dn.stm.shop.model.ItemGroup;
import dn.stm.shop.model.ItemUnit;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelReader {

    private static final Logger LOGGER = Logger.getLogger(ExcelReader.class.getName());

    // public constants
    public static final String GROUPS = "groups";
    public static final String ITEMS = "items";
    public static final String MAIN_GROUP_ID = "00016";     // id of main group of items

    // excel file variables    
    private final int SHEET = 0;                            // sheet list nummber
    private final int COLUMN_GROUP_NAME = 0;                // column with group names
    private final int COLUMN_ITEM_NAME = 5;                 // column with item names
    private final int COLUMN_DESCRIPTION = 4;               // column with item descriptions
    private final int COLUMN_ID = 6;                        // column with item/group ids
    private final int COLUMN_PARENT_ID = 7;                 // column with item/group parent ids
    private final int COLUMN_UNIT = 8;                      // column with item unit names
    private final int COLUMN_PRICE_1 = 9;                   // column with item prices 1
    private final int COLUMN_PRICE_2 = 10;                  // column with item prices 2
    private final int COLUMN_PRICE_3 = 11;                  // column with item prices 3

    // catalog variables
    private final List<ItemGroup> allGroups = new ArrayList<>();    // all groups in catalog
    private final List<Item> allItems = new ArrayList<>();          // all items in catalog
    private String message;
    private final List<String> errorMessages = new ArrayList<>();   // error messages

    // Getters and setters start
    public String getMessage() {
        return message;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public List<ItemGroup> getAllGroups() {
        return allGroups;
    }

    public List<Item> getAllItems() {
        return allItems;
    }
    // Getters and setters end

    /**
     * TODO 
     */
     public synchronized void readFile() {
        errorMessages.clear();
        allGroups.clear();
        allItems.clear();
        LOGGER.log(Level.OFF, "Start read file");
        ClassLoader classLoader = this.getClass().getClassLoader();
        String name;
        String fileName = AppPropertiesHandler.getAppProperties().getProperty(AppPropertiesHandler.EXCEL_FILE_NAME);
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
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

            Sheet sheet = workbook.getSheetAt(SHEET);

            // Get iterator to all the rows in current sheet 
            Iterator<Row> rowIterator = sheet.iterator();
            // Traversing over each row of XLSX file 
            boolean read = false;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    name = row.getCell(COLUMN_GROUP_NAME).getStringCellValue();
                } catch (Exception e) {
                    continue;
                }
                if (name == null || name.isEmpty()) {
                    continue;
                }
                if ("СТРОЙМАТЕРИАЛЫ".equalsIgnoreCase(name)) {
                    read = true;
                    continue;
                }
                if (!read) {
                    continue;
                }
                // For each row, iterate through each columns 

                parseRow(row);
            }
            LOGGER.info("read success!!");
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Error during parse xsl {0}", ex.getMessage());
            errorMessages.add("File not found.");
            message = "File not found.";
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error during parse xsl {0}", ex.getMessage());
            errorMessages.add("Error during parse xsl.");
            message = "Error during parse xsl.";
        } catch (InvalidFormatException | EncryptedDocumentException ex) {
            LOGGER.log(Level.SEVERE, "Error during parse xsl {0}", ex.getMessage());
            errorMessages.add("Error during parse xsl - invalid format.");
            message = "Error during parse xsl - invalid format.";

        }
        LOGGER.log(Level.INFO, "DONE: groups: {0}, items: {1}", new Object[]{allGroups.size(), allItems.size()});
        message = "Файл прочитан. Найдено " + allItems.size() + " товаров в " + allGroups.size() + " группах.";
        LOGGER.log(Level.INFO, "Reading excel is OK. Items number: {0}", allItems.size());
    }

    private void parseRow(Row row) {
        double someDouble;
        try {
            someDouble = row.getCell(COLUMN_PRICE_1).getNumericCellValue();
            if (someDouble == 0.0) {
                parseGroupRow(row);
            } else {
                parseItemRow(row);
            }

        } catch (Exception e) {
            // TODO ?????
            parseGroupRow(row);
        }
    }

    private void parseGroupRow(Row row) {
        String groupName = null;
        String groupId = null;
        String parentId;
        try {
            groupName = row.getCell(COLUMN_GROUP_NAME).getStringCellValue().trim();
            groupId = row.getCell(COLUMN_ID).getStringCellValue().trim();

            parentId = row.getCell(COLUMN_PARENT_ID).getStringCellValue().trim();
            ItemGroup itemGroup = new ItemGroup(groupId, parentId, groupName);
            allGroups.add(itemGroup);
        } catch (Exception e) {
            errorMessages.add("Ошибка при чтении группы " + groupName);
            LOGGER.log(Level.SEVERE, "Error during parsing group [{0}] where id = [{1}]. Error is {2}", new Object[]{groupName, groupId, e.getMessage()});
        }
    }

    private void parseItemRow(Row row) {
        String itemName = null;
        try {
            itemName = row.getCell(COLUMN_ITEM_NAME).getStringCellValue();
            String itemId = row.getCell(COLUMN_ID).getStringCellValue().trim();
            if (itemId.contains("+")) {
                // TODO move "pluus" to constants
                // TODO replace "+" inside groupsID's
                itemId = itemId.replace("+", "plus");
            }

            String parentId = row.getCell(COLUMN_PARENT_ID).getStringCellValue().trim();
            String unitName = row.getCell(COLUMN_UNIT).getStringCellValue().trim();
            ItemUnit itemUnit = ItemUnit.fromString(unitName);
            double price_01 = row.getCell(COLUMN_PRICE_1).getNumericCellValue();
            double price_02 = price_01;
            double price_03 = price_01;
            try {
                price_02 = row.getCell(COLUMN_PRICE_2).getNumericCellValue();
                price_03 = row.getCell(COLUMN_PRICE_3).getNumericCellValue();
            } catch (Exception e) {
                // use price_01 for all prices
            }

            String description = "";
            try {
                description = row.getCell(COLUMN_DESCRIPTION).getStringCellValue().trim();
            } catch (Exception e) {
//                LOGGER.log(Level.OFF, "There is no description for item " + itemName);
            }

            Item item = new Item(itemId, parentId, itemName, itemUnit,
                    price_01, price_02, price_03, description);

            allItems.add(item);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during parsing item '{0}'", itemName);
            errorMessages.add("Ошибка при чтении товара " + itemName);
        }
    }
}
