package kz.jarkyn.backend.core.model.filter;

public abstract class PagedApiFilter {
    public final Integer page;
    public final Integer pageSize;

    protected PagedApiFilter(Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getPageSize() {
        return pageSize;
    }
}
