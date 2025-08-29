package kz.jarkyn.backend.document.core.specifications;

import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity_;
import kz.jarkyn.backend.document.core.model.DocumentTypeEntity;
import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.party.model.CounterpartyEntity;
import kz.jarkyn.backend.party.model.OrganizationEntity;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecifications {
    public static Specification<DocumentEntity> type(DocumentTypeEntity type) {
        return (root, query, cb) -> cb.equal(root.get(DocumentEntity_.type), type);
    }

    public static Specification<DocumentEntity> warehouse(WarehouseEntity warehouse) {
        return (root, query, cb) -> cb.equal(root.get(DocumentEntity_.warehouse), warehouse);
    }

    public static Specification<DocumentEntity> organization(OrganizationEntity organization) {
        return (root, query, cb) -> cb.equal(root.get(DocumentEntity_.organization), organization);
    }

    public static Specification<DocumentEntity> account(AccountEntity account) {
        return (root, query, cb) -> cb.equal(root.get(DocumentEntity_.account), account);
    }

    public static Specification<DocumentEntity> counterparty(CounterpartyEntity counterparty) {
        return (root, query, cb) -> cb.equal(root.get(DocumentEntity_.counterparty), counterparty);
    }
}