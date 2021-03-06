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
//    private final int PAGGING_BUTTONS_DIV_WIDTH = 786;
    private final int rowsPerPage = 30;

    // items to show
    private List<Item> itemList;
    private String groupId = Catalog.MAIN_GROUP_ID;        // defautl value - show all items in catalog
//    private String currentGroupId = null;

    // Paging
    private int totalRows;
    private int totalPages;
    private int currentPage;
    private List<PaginationButton> paginationButtons;
    private boolean showPagination;

    // Title
    private String title;

    @PostConstruct
    private void init() {
        LOGGER.info ("====================== CatalogController init ===============");
        // Set default values
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
        } else {
            searchBean.setSearchString("");
        }

        if (itemToAdd != null && !itemToAdd.isEmpty()) {
            order.add(catalog.getItemById(itemToAdd));
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
            // TODO Fix this 
//            searchBean.setSearchString("");
            try {
                groupId = groupIdParam;
                if (groupId == null || groupId.isEmpty()) {
//                    searchBean.setSearchString("");
                }

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error parsing groupId param from {0}", groupIdParam);
            }
        }

    }

    private List<Item> getAllItems() {
        // TODO move strings to constants
        // check if search
        String search = searchBean.getSearchString();
        if (search != null && !search.isEmpty()) {
//            LOGGER.log(Level.INFO, "Search by {0}", search);
            title = "Результаты поиска по слову \"" + search + "\" .";
            return catalog.getSearchItemList(search, sort, sortAscending);
        } else {
//            LOGGER.log(Level.INFO, "Fing group {0}", groupId);
            if (Catalog.MAIN_GROUP_ID.equals(groupId)) {
                title = "Все товары каталога.";
            } else {
                title = "Товары раздела \"" + catalog.getGroupNameById(groupId) + "\" .";
            }
            return catalog.getItemsListByGroupId(groupId, sort, sortAscending);
        }
    }

    private void preparePaginationButtons() {
        paginationButtons = new ArrayList<>();
        String label;
        int buttonPage;
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

    public String getGroupId() {
        return groupId;
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
