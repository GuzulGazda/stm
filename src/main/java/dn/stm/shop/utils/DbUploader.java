package dn.stm.shop.utils;

import dn.stm.shop.beans.Catalog;
import dn.stm.shop.model.Item;
import dn.stm.shop.model.ItemGroup;
import dn.stm.shop.model.ItemUnit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class DbUploader {

    private static final Logger LOGGER = Logger.getLogger(DbUploader.class.getName());

    // prepared statements
    private static final String TRUNCATE_GROUP = "TRUNCATE itemgroup";
    private static final String TRUNCATE_ITEM = "TRUNCATE item";
    private static final String INSERT_GROUP = "INSERT INTO itemgroup "
            + "(id, parentId, name) "
            + " VALUES (?,?,?)";
    private static final String INSERT_ITEM = "INSERT INTO item "
            + "(id, groupId, itemunit, name, description, price_1, price_2, price_3) "
            + " VALUES(?,?,?,?,?,?,?,?)";
    private static final String SELECT_GROUPS = "SELECT * FROM itemgroup";
    private static final String SELECT_ITEMS = "SELECT * FROM item";

    // TODO - return list of error messages
    // TODO - realize revert changes if something wrong
    public static void upload(List<ItemGroup> groups, List<Item> items) {

//        Context ctx = null;
        Connection conn = null;

        PreparedStatement truncateGroup = null;
        PreparedStatement truncateItem = null;
        PreparedStatement insertGroup = null;
        PreparedStatement insertItem = null;

        ResultSet rs = null;
        boolean success = true;
        try {
            DataSource ds = DataSourceFactory.getMySQLDataSource();
            conn = ds.getConnection();
            
            //transaction block start
            conn.setAutoCommit(false);

            // truncate table itemGroup
            truncateGroup = conn.prepareStatement(TRUNCATE_GROUP);
            truncateGroup.execute();
            // truncate table item
            LOGGER.info("\tGroups truncated");
            truncateItem = conn.prepareStatement(TRUNCATE_ITEM);
            truncateItem.execute();
            LOGGER.info("\tIrems truncated");
            // fill table itemGroup with new data
            insertGroup = conn.prepareStatement(INSERT_GROUP);
            for (ItemGroup group : groups) {
                insertGroup.setString(1, group.getId());
                insertGroup.setString(2, group.getParentId());
                insertGroup.setString(3, group.getName());
                insertGroup.addBatch();
            }
            insertGroup.executeBatch();
            // fill table item with new data
            insertItem = conn.prepareStatement(INSERT_ITEM);
            int i = 0;
            for (Item item : items) {
                try {
                    insertItem.setString(1, item.getId());                  // id
                    insertItem.setString(2, item.getGroupId());             // groupId
                    insertItem.setString(3, item.getItemUnit().getName());  // itemUnit name
                    insertItem.setString(4, item.getName());                // name
                    insertItem.setString(5, item.getDescription());         // description
                    insertItem.setDouble(6, item.getPrice_1());             // price_1
                    insertItem.setDouble(7, item.getPrice_2());             // price_2
                    insertItem.setDouble(8, item.getPrice_3());             // price_3
                    insertItem.addBatch();
                    i++;
                    if (i % 1000 == 0 || i == items.size()) {
                        insertItem.executeBatch();                      // Execute every 1000 items.
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "WRONG ITEM:  '{'0'}'{0}", item.getId());
                    LOGGER.log(Level.SEVERE, "ERROR 100:  {0}" + ex.getClass().getName(), ex.getMessage());
                }

            }
            LOGGER.info("\tIrems loaded");
            conn.commit();
            LOGGER.info("Data Base loaded successfully....");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "ERROR 10:  {0}" + ex.getClass().getName(), ex.getMessage());
        } finally {
            if (truncateGroup != null) {
                try {
                    truncateGroup.close();
                } catch (SQLException ex) {
                }// do nothing
            }

            if (truncateItem != null) {
                try {
                    truncateItem.close();
                } catch (SQLException ex) {
                }// do nothing

            }

            if (insertGroup != null) {
                try {
                    insertGroup.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "ERROR 4:  {0}", ex.getMessage());
                }
            }

            if (insertItem != null) {
                try {
                    insertItem.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "ERROR 5:  {0}", ex.getMessage());
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "ERROR 6:  {0}", ex.getMessage());
                }
            }
        }
    }

    public  boolean loadDb(Catalog catalog) {
        
        boolean success = true;
        java.util.Map<String, ItemGroup> groups = catalog.getGroupsMap();
        java.util.Map<String, Item> items = catalog.getItemsMap();
        
        
        Connection conn = null;
        PreparedStatement pstmtLoadGroups = null;
        PreparedStatement pstmtLoadItems = null;
        ResultSet rs = null;
        try {
            DataSource ds = DataSourceFactory.getMySQLDataSource();
            conn = ds.getConnection();
            pstmtLoadGroups = conn.prepareStatement(SELECT_GROUPS);
            pstmtLoadItems = conn.prepareStatement(SELECT_ITEMS);
            // read all groups
            rs = pstmtLoadGroups.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String parentId = rs.getString("parentId");
                String name = rs.getString("name");
                groups.put(id, new ItemGroup(id, parentId, name));
            }
            // read all items
            rs = pstmtLoadItems.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String groupId = rs.getString("groupId");
                ItemUnit itemUnit = ItemUnit.fromString(rs.getString("itemunit"));
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price_1 = rs.getDouble("price_1");
                double price_2 = rs.getDouble("price_2");
                double price_3 = rs.getDouble("price_3");
                items.put(id, new Item(id, groupId, name, itemUnit, price_1, price_2, price_3, description));
            }
            rs.close();
        } catch (SQLException ex) {
            success = false;
            LOGGER.log(Level.SEVERE, "Error 70: {0}", ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException se) {
            }// do nothing
            try {
                if (pstmtLoadGroups != null) {
                    pstmtLoadGroups.close();
                }
            } catch (SQLException se) {
            }// do nothing
            try {
                if (pstmtLoadItems != null) {
                    pstmtLoadItems.close();
                }
            } catch (SQLException e) {
            }// do nothing
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }// do nothing
            }
        }//end try
        if (!success) {
            groups.clear();
            items.clear();
        }
        return success;
    } // end uploadDb

}
