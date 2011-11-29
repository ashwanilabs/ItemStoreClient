/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.gwt.itemstoreclient.client;

/**
 *
 * @author ashwanilabs
 */
public class Bucket {

    private static final long serialVersionUID = 1L;
    private String bucketnm;
    private String userid;
    private String uri;
    private String item_nos;
    private String lastModified;

    public Bucket() {
    }

    public Bucket(String bucketnm, String uri, String item_nos, String userid, String lastModified) {
        this.bucketnm = bucketnm;
        this.uri = uri;
        this.item_nos = item_nos;
        this.userid = userid;
        this.lastModified = lastModified;
    }

    public String getBucketnm() {
        return bucketnm;
    }

    public void setBucketnm(String bucketnm) {
        this.bucketnm = bucketnm;
    }

    public String getItem_nos() {
        return item_nos;
    }

    public void setItem_nos(String item_nos) {
        this.item_nos = item_nos;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
