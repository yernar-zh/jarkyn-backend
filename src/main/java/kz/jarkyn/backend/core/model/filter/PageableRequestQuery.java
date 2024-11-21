package kz.jarkyn.backend.core.model.filter;

public abstract class PageableRequestQuery {
    public final Integer pageFirst;
    public final Integer pageSize;

    protected PageableRequestQuery(Integer pageFirst, Integer pageSize) {
        this.pageFirst = pageFirst;
        this.pageSize = pageSize;
    }

    public Integer getPageFirst() {
        return pageFirst;
    }

    public Integer getPageSize() {
        return pageSize;
    }
}
