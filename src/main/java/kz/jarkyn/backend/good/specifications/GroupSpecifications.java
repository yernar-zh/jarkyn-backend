package kz.jarkyn.backend.good.specifications;

import kz.jarkyn.backend.good.model.*;
import org.springframework.data.jpa.domain.Specification;

public class GroupSpecifications {
    public static Specification<GroupEntity> root() {
        return (root, query, cb) -> cb.isNull(root.get(GroupEntity_.parent));
    }
}