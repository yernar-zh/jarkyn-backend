package kz.jarkyn.backend.audit.sorts;

import kz.jarkyn.backend.audit.model.AuditEntity_;
import org.springframework.data.domain.Sort;

public class AuditSorts {
    public static Sort byCreatedDesc() {
        return Sort.by(AuditEntity_.CREATED_AT).descending();
    }
}