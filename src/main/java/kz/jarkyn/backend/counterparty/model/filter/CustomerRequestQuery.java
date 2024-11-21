package kz.jarkyn.backend.counterparty.model.filter;

import kz.jarkyn.backend.core.model.filter.PageableRequestQuery;

public class CustomerRequestQuery extends PageableRequestQuery {
    private final String search;

    public CustomerRequestQuery(
            String search,
            Integer page, Integer pageSize
    ) {
        super(page, pageSize);
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public enum Sort {
        NAME, PHONE_NUMBER;
    }
}