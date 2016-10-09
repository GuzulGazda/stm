package dn.stm.shop.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author home
 */
public class PriceList implements Serializable {

    public PriceList() {
        categorys.add(new Category(2, "Стройматериалы"));
        categorys.add(new Category(3, "Металлоизделия"));
        categorys.add(new Category(4, "Металлопрокат"));
        categorys.add(new Category(5, "Лесопиломатериалы"));
        categorys.add(new Category(6, "Отделочные материалы"));
        categorys.add(new Category(7, "Общестроительная группа"));
    }

    private final List<Category> categorys = new ArrayList<>();

    public List<Category> getCategorys() {
        return categorys;
    }

    public Category getCategoryById(int categoryId) {
        return categorys.get(categoryId);
    }

    public Category getCategoryByName(String categoryName) throws IllegalArgumentException {
        if (categoryName.isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        for (Category category : categorys) {
            if (categoryName.equalsIgnoreCase(category.getName())) {
                return category;
            }
        }
        throw new IllegalArgumentException("Impossible category name; " + categoryName);
    }

    public Item getItemById(int itemId) {
        List<Item> allItems = new ArrayList<>();
        for (Category category : categorys) {
            List<ItemGroup> itemGroups = category.getItemGroups();
            for (ItemGroup itemGroup : itemGroups) {
                allItems.addAll(itemGroup.getItems());
            }
        }

        for (Item item : allItems) {
//            System.out.println("item.getId() = " + item.getId() + "\titemId = " + itemId);
            if (item.getId() == itemId) {
                return item;
            }
        }
        return null;
    }
    
    public int getNumOfGroups(){
        int counter = 0;
        for (Category category : categorys) {
            counter += category.getItemGroups().size();
        }
        return counter;
    }
    
    public int getNumOfItems(){
        int counter = 0;
        for (Category category : categorys) {
            counter += category.getAllItemsUnsorted().size();
        }
        return counter;
    }
}
