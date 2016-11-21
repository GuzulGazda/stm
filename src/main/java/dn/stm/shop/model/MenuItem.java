package dn.stm.shop.model;

import java.io.Serializable;

/**
 * TODO
 *
 * @author home
 */
public class MenuItem implements Serializable {

    private final String url;       // URL string of menu item
    private final String name;      // text of menu item
    private String hrefCss;         // CSS class of <href> element
    private final String liCss;     // CSS class of <li> element

    /**
     * Constructor
     *
     * @param url - URL string of menu item
     * @param name - text of menu item
     * @param liCss - CSS class of <li> element
     */
    public MenuItem(String url, String name, String liCss) {
        this.url = url;
        this.name = name;
        this.liCss = liCss;
    }

    // Getters and setters start
    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getLiCss() {
        return liCss;
    }

    public String getHrefCss() {
        return hrefCss;
    }

    public void setHrefCss(String hrefCss) {
        this.hrefCss = hrefCss;
    }
    // Getters and setters end
}
