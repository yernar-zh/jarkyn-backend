package kz.jarkyn.backend.core.sorts;

import kz.jarkyn.backend.core.model.AbstractEntity_;
import org.springframework.data.domain.Sort;

public class EntitySorts {
    public static Sort byCreatedDesc() {
        return Sort.by(AbstractEntity_.CREATED_AT).descending();
    }
}