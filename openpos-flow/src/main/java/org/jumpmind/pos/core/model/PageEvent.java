package org.jumpmind.pos.core.model;

import java.io.Serializable;

/**
 * Corresponds to angular/material/PageEvent class and used
 * for CatalogBrowser screen.
 */
public class PageEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer pageIndex;
    private Integer previousPageIndex;
    private Integer pageSize;
    private Integer length;
    
    public Integer getPreviousPageIndex() {
        return previousPageIndex;
    }
    public void setPreviousPageIndex(Integer previousPageIndex) {
        this.previousPageIndex = previousPageIndex;
    }
    public Integer getPageIndex() {
        return pageIndex;
    }
    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }
    public Integer getPageSize() {
        return pageSize;
    }
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    public Integer getLength() {
        return length;
    }
    public void setLength(Integer length) {
        this.length = length;
    }
    
}
