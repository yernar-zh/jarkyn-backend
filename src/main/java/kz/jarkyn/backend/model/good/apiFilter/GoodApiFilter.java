package kz.jarkyn.backend.model.good.apiFilter;

import kz.jarkyn.backend.model.common.filter.PagedApiFilter;

import java.util.UUID;

public class GoodApiFilter extends PagedApiFilter {
    private final String search;
    private final UUID groupId;
    private final UUID attributeId;

    public GoodApiFilter(String search, UUID groupId, int page, int pageSize, UUID attributeId) {
        super(page, pageSize);
        this.search = search;
        this.groupId = groupId;
        this.attributeId = attributeId;
    }

    public String getSearch() {
        return search;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public UUID getAttributeId() {
        return attributeId;
    }
}
