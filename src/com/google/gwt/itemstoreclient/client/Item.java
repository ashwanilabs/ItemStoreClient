/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.gwt.itemstoreclient.client;

/**
 *
 * @author ashwanilabs
 */
public class Item {

    private static final long serialVersionUID = 1L;
    private String itemnm;
    private String type;
    private String uri;
    private String lastModified;

    public Item() {
    }


    public Item(String itemnm, String type, String uri, String lastModified) {
        this.itemnm = itemnm;
        this.type = type;
        this.uri = uri;
        this.lastModified = lastModified;
    }

    public String getItemnm() {
        return itemnm;
    }

    public String getLastModified() {
        return lastModified;
    }

    public String getType() {
        return type;
    }

    public String getUri() {
        return uri;
    }

    public void setItemnm(String itemnm) {
        this.itemnm = itemnm;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
