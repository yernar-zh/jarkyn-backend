package kz.jarkyn.backend.party.spesification;

import kz.jarkyn.backend.party.model.OrganizationEntity;
import kz.jarkyn.backend.party.model.OrganizationEntity_;
import org.springframework.data.jpa.domain.Specification;

public class OrganizationSpecifications {
    public static Specification<OrganizationEntity> nonArchived() {
        return (root, query, cb) -> cb.equal(root.get(OrganizationEntity_.archived), false);
    }
}