package dn.stm.shop.beans;

import dn.stm.shop.model.Item;
import dn.stm.shop.model.PaginationButton;
import static dn.stm.shop.model.PaginationButton.ACTIVE;
import static dn.stm.shop.model.PaginationButton.CURRENT;
import static dn.stm.shop.model.PaginationButton.DISABLED;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author home
 */
@ManagedBean(name = "CatalogController")
@RequestScoped

public class CatalogController implements Serializable {

    private final String SORT_NAME = "name";
    private final String SORT_PRICE = "price";
    private final boolean sortAscending = true;

    @ManagedProperty(value = "#{Catalog}")
    private Catalog catalog;

    @ManagedProperty(value = "#{Order}")
    private OrderBean order;

    @ManagedProperty(value = "#{Search}")
    private SearchBean searchBean;

    private String sort = "name";       // default value

    private static final Logger LOGGER = Logger.getLogger(Catalog.class.getName());

    // Constants
    private final int PAGGING_BUTTONS_DIV_WIDTH = 786;
    private final int rowsPerPage = 15;

    // items to show
    private List<Item> itemList;
    private int groupId = 1;        // defautl value - show all items in catalog
//    private int categoryId;

    // Paging.
    private int totalRows;
    private int totalPages;
    private int currentPage;
    private List<PaginationButton> paginationButtons;
    private boolean showPagination;

    // Title
    private String title;

    @PostConstruct
    private void init() {
        System.out.println("CatalogContoller:: init");
        // Set default values 
//        groupId = 0;    // show all items in catalog
        currentPage = 1;
        this.showPagination = true;

        readParams();
        List<Item> allItems = getAllItems();
        totalRows = allItems.size();
        title += " Найдено товаров: " + totalRows + ".";
        showPagination = (totalRows > rowsPerPage);
        totalPages = (totalRows / rowsPerPage) + ((totalRows % rowsPerPage != 0) ? 1 : 0);
        int fromIndex = (currentPage - 1) * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, totalRows);
        itemList = allItems.subList(fromIndex, toIndex);
        if (totalRows == 0);
        preparePaginationButtons();
        if (totalPages > 1) {
            int firstItem = ((currentPage - 1) * rowsPerPage) + 1;
            int lastItem = Math.min(firstItem + rowsPerPage - 1, totalRows);
            title += " Показаны товары с " + firstItem + " по " + lastItem;
        }
//        System.out.println("INIT OK Sort is "  + sortBean.getSortField());
    }

    private void readParams() {

        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String sortParam = req.getParameter("sort");
        String pageParam = req.getParameter("page");
        String groupIdParam = req.getParameter("groupId");
        String itemToAdd = req.getParameter("itemToAdd");
        String searchParam = req.getParameter("search");

        // save search string in session
        if (searchParam != null && !searchParam.isEmpty()) {
            searchBean.setSearchString(searchParam);
        } 

        if (itemToAdd != null && !itemToAdd.isEmpty()) {
            int itemToAddId = Integer.parseInt(itemToAdd);
            order.add(catalog.getItemById(itemToAddId));
        }

        // sort param
        if (SORT_NAME.equalsIgnoreCase(sortParam)) {
            sort = SORT_NAME;
        } else if (SORT_PRICE.equalsIgnoreCase(sortParam)) {
            sort = SORT_PRICE;
        }

        // page param
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error parsing page param from {0}", pageParam);
            }
        }

        // groupId param
        if (groupIdParam != null && !groupIdParam.isEmpty()) {
            try {
                groupId = Integer.parseInt(groupIdParam);
                if (groupId != 0) {
                    System.out.println("\t Search String is null!!!!");
                    searchBean.setSearchString("");
                }

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error parsing groupId param from {0}", groupIdParam);
            }
        }

    }

    private List<Item> getAllItems() {
        // check if search
        String search = searchBean.getSearchString();
        if (search != null && !search.isEmpty()) {
            System.out.println("DO SEARCH!!!");
            title = "Результаты поиска по слову \"" + search + "\" .";
            return catalog.getSearchItemList(search, sort, sortAscending);
        } else if (groupId > 0 ){
            title = "Товары раздела \"" + catalog.getGroupNameById(groupId) + "\" .";
        }
        return catalog.getGroupList(groupId, sort, sortAscending);
    }

    private void preparePaginationButtons() {
        System.out.println("CatalogController: preparePaginationButtons ");
        paginationButtons = new ArrayList<>();
        String label;
        int buttonPage;
        System.out.println("Total");
        String buttonClassName;

        if (totalPages < 2) {
            // don't show pagination 
        } else if (totalPages <= 15) {
            for (int i = 0; i < totalPages; i++) {
                // current page's class is "current", for another pages - "active"  
                buttonClassName = (currentPage == (i + 1)) ? CURRENT : ACTIVE;
                label = Integer.toString(i + 1);
                buttonPage = i + 1;
                PaginationButton paginationButton = new PaginationButton(label, buttonClassName, buttonPage);
                paginationButtons.add(paginationButton);
            }
        } else {
            // show FIRST_PAGE & PREVIOUS_PAGE buttons
            buttonClassName = (currentPage == 1) ? DISABLED : ACTIVE;
            paginationButtons.add(new PaginationButton("<<", buttonClassName, 1));
            paginationButtons.add(new PaginationButton("<", buttonClassName, currentPage - 1));

            // show button with page numbers
            if (currentPage <= 6) {
                // show buttons 1 - 7
                for (int i = 0; i < 7; i++) {
                    buttonClassName = (currentPage == (i + 1)) ? CURRENT : ACTIVE;
                    label = Integer.toString(i + 1);
                    paginationButtons.add(new PaginationButton(label, buttonClassName, i + 1));
                }
                buttonClassName = ACTIVE;
                // ... (ellipsis button)
                paginationButtons.add(new PaginationButton("...", DISABLED, -1));
                // page N - 2
                paginationButtons.add(new PaginationButton(Integer.toString(totalPages - 2), ACTIVE, totalPages - 2));
                // page N - 1
                paginationButtons.add(new PaginationButton(Integer.toString(totalPages - 1), ACTIVE, totalPages - 1));
                // page N
                paginationButtons.add(new PaginationButton(Integer.toString(totalPages), ACTIVE, totalPages));

            } else if (currentPage > 6 && currentPage < (totalPages - 4)) {
                // page 1
                paginationButtons.add(new PaginationButton("1", ACTIVE, 1));
                // page 2
                paginationButtons.add(new PaginationButton("2", ACTIVE, 2));
                // page 3
                paginationButtons.add(new PaginationButton("3", ACTIVE, 3));
                // ... (ellipsis button)
                paginationButtons.add(new PaginationButton("...", DISABLED, -1));
                // current page - 1
                paginationButtons.add(new PaginationButton(Integer.toString(currentPage - 1), ACTIVE, currentPage - 1));
                // current page
                paginationButtons.add(new PaginationButton(Integer.toString(currentPage), CURRENT, currentPage));
                // current page + 1
                paginationButtons.add(new PaginationButton(Integer.toString(currentPage + 1), ACTIVE, currentPage + 1));
                // ... (ellipsis button)
                paginationButtons.add(new PaginationButton("...", DISABLED, -1));
                // page N - 2
                paginationButtons.add(new PaginationButton(Integer.toString(totalPages - 2), ACTIVE, totalPages - 2));
                // page N - 1
                paginationButtons.add(new PaginationButton(Integer.toString(totalPages - 1), ACTIVE, totalPages - 1));
                // page N
                paginationButtons.add(new PaginationButton(Integer.toString(totalPages), ACTIVE, totalPages));

            } else {
                // page 1
                paginationButtons.add(new PaginationButton("1", ACTIVE, 1));
                // page 2
                paginationButtons.add(new PaginationButton("2", ACTIVE, 2));
                // page 3
                paginationButtons.add(new PaginationButton("3", ACTIVE, 3));
                // ... (ellipsis button)
                paginationButtons.add(new PaginationButton("...", DISABLED, -1));
                for (int i = totalPages - 6; i <= totalPages; i++) {
                    buttonClassName = (currentPage == i) ? CURRENT : ACTIVE;
                    paginationButtons.add(new PaginationButton(Integer.toString(i), buttonClassName, i));
                }
            }

            // show NEXT_PAGE & LAST_PAGE buttons
            buttonClassName = (currentPage == totalPages) ? DISABLED : ACTIVE;
            paginationButtons.add(new PaginationButton(">", buttonClassName, currentPage + 1));
            paginationButtons.add(new PaginationButton(">>", buttonClassName, totalPages));
        }
    }

    // Getters
    public List<Item> getItemList() {
        return itemList;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List<PaginationButton> getPaginationButtons() {
        return paginationButtons;
    }

    public boolean isShowPagination() {
        return showPagination;
    }

    public String getTitle() {
        return title;
    }

    public OrderBean getOrder() {
        return order;
    }

    public SearchBean getSearchBean() {
        return searchBean;
    }

    public String getSort() {
        return sort;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public void setOrder(OrderBean order) {
        this.order = order;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setSearchBean(SearchBean searchBean) {
        this.searchBean = searchBean;
    }

}
