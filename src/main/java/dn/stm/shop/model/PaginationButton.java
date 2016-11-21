package dn.stm.shop.model;

import java.io.Serializable;

/**
 *
 * @author home
 */
public class PaginationButton implements Serializable {

    // Constants
    public static final String ACTIVE = "active";
    public static final String DISABLED = "disabled";
    public static final String CURRENT = "current";

    private final String label;         // button label
    private final String className;     // CSS class of button
    private final int page;             // page number

    /**
     * Constructor
     *
     * @param label - button label
     * @param className - CSS class of button
     * @param page - page number
     */
    public PaginationButton(String label, String className, int page) {
        this.label = label;
        this.className = className;
        this.page = page;
    }

    // Getters and setters end
    public String getLabel() {
        return label;
    }

    public String getClassName() {
        return className;
    }

    public int getPage() {
        return page;
    }
    // Getters and setters end
}
