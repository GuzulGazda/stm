/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dn.stm.shop.model;

import java.io.Serializable;

/**
 *
 * @author home
 */
public class MenuItem implements Serializable {

    private String url;
    private String name;
    private String hrefCss;
    private String liCss;

    public MenuItem(String url, String name, String liCss) {
        this.url = url;
        this.name = name;
        this.liCss = liCss;
    }

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

    
    
    

}
