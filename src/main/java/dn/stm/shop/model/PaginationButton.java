package dn.stm.shop.model;

import java.io.Serializable;

/**
 *
 * @author home
 */
public class PaginationButton implements Serializable {

    public static final String ACTIVE = "active";
    public static final String DISABLED = "disabled";
    public static final String CURRENT = "current";

    private final String label;
    private final String className;
    private final int page;

    public PaginationButton(String label, String className, int page) {
        this.label = label;
        this.className = className;
        this.page = page;
    }

    public String getLabel() {
        return label;
    }

    public String getClassName() {
        return className;
    }

    public int getPage() {
        return page;
    }


}
