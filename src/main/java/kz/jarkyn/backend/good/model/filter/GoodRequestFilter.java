package kz.jarkyn.backend.good.model.filter;

import kz.jarkyn.backend.core.model.filter.PageableRequestQuery;

import java.util.UUID;

public class GoodRequestFilter extends PageableRequestQuery {
    private final String search;
    private final UUID groupId;
    private final UUID attributeId;
    private final Boolean archived;

    public GoodRequestFilter(
            String search, UUID groupId, UUID attributeId, Boolean archived,
            Integer pageFirst, Integer pageSize
    ) {
        super(pageFirst, pageSize);
        this.search = search;
        this.groupId = groupId;
        this.attributeId = attributeId;
        this.archived = archived;
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

    public Boolean getArchived() {
        return archived;
    }
}