package kz.jarkyn.backend.model.common.filter;

public abstract class PagedApiFilter {
    public final int page;
    public final int pageSize;

    public PagedApiFilter(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }
}
