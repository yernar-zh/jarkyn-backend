package kz.jarkyn.backend.core.sorts;

import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.core.model.ReferenceEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity_;
import org.springframework.data.domain.Sort;

public class EntitySorts {
    public static Sort byCreatedAsc() {
        return Sort.by(AbstractEntity_.CREATED_AT).ascending();
    }

    public static Sort byCreatedDesc() {
        return Sort.by(AbstractEntity_.CREATED_AT).descending();
    }

    public static Sort byNameDesc() { // ALSO USE FOR document.name
        return Sort.by(ReferenceEntity_.NAME).descending();
    }

    public static Sort byMomentDesc() { // ALSO USE FOR document.name
        return Sort.by(DocumentEntity_.MOMENT).descending();
    }
}
