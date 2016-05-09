package com.sen.vov.models;

import java.util.List;

/**
 * Created by Sen on 2016/3/16.
 */
public class CommonModel {
    private String allNum;
    private String allPages;
    private List<ContentModel> contentlist;

    public String getAllNum() {
        return allNum;
    }

    public void setAllNum(String allNum) {
        this.allNum = allNum;
    }

    public String getAllPages() {
        return allPages;
    }

    public void setAllPages(String allPages) {
        this.allPages = allPages;
    }

    public List<ContentModel> getContentlist() {
        return contentlist;
    }

    public void setContentlist(List<ContentModel> contentlist) {
        this.contentlist = contentlist;
    }
}
