package kz.jarkyn.backend.model.good.apiFilter;

import kz.jarkyn.backend.model.common.filter.PagedApiFilter;

import java.util.UUID;

public class GoodApiFilter extends PagedApiFilter {
    private final String search;
    private final UUID groupId;
    private final UUID attributeId;
    private final Boolean archived;

    public GoodApiFilter(
            String search, UUID groupId, UUID attributeId, Integer page,
            Integer pageSize, Boolean archived
    ) {
        super(page, pageSize);
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
