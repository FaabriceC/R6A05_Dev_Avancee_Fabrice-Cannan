package com.master.air.dto;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> items;
    private long totalCount;
    private int page;
    private int pageSize;
    private int totalPages;

    public PaginatedResponse() {}

    public PaginatedResponse(List<T> items, long totalCount, int page, int pageSize) {
        this.items = items;
        this.totalCount = totalCount;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
    }

    public List<T> getItems() { return items; }
    public void setItems(List<T> i) { this.items = i; }
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long t) { this.totalCount = t; }
    public int getPage() { return page; }
    public void setPage(int p) { this.page = p; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int ps) { this.pageSize = ps; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int tp) { this.totalPages = tp; }
}
