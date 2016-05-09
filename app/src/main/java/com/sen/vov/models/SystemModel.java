package com.sen.vov.models;

import com.google.gson.annotations.SerializedName;

/**
 * API系统级的model
 * Created by Sen on 2016/3/14.
 */
public class SystemModel {
    private int showapi_res_code;
    private String showapi_res_error;
    @SerializedName("showapi_res_body")
    private PageModel showapi_res_body;
    public int getShowapi_res_code() {
        return showapi_res_code;
    }

    public void setShowapi_res_code(int showapi_res_code) {
        this.showapi_res_code = showapi_res_code;
    }

    public String getShowapi_res_error() {
        return showapi_res_error;
    }

    public void setShowapi_res_error(String showapi_res_error) {
        this.showapi_res_error = showapi_res_error;
    }

    public PageModel getShowapi_res_body() {
        return showapi_res_body;
    }

    public void setShowapi_res_body(PageModel showapi_res_body) {
        this.showapi_res_body = showapi_res_body;
    }

    /**
     * 一个系统级过渡参数，链接常用参数
     */
   public static class PageModel {
        @SerializedName("pagebean")
        private CommonModel pagebean;

        public CommonModel getPagebean() {
            return pagebean;
        }

        public void setPagebean(CommonModel pagebean) {
            this.pagebean = pagebean;
        }
    }
}
