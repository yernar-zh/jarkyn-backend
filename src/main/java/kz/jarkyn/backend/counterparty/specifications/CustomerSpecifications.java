package kz.jarkyn.backend.counterparty.specifications;

import jakarta.persistence.criteria.Root;
import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.counterparty.model.AccountEntity_;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.CustomerEntity_;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.model.SaleEntity_;
import org.springframework.data.jpa.domain.Specification;


public class CustomerSpecifications {
    public static Specification<CustomerEntity> fetchCustomerDetails() {
        return (root, query, criteriaBuilder) -> {
            Root<AccountEntity> accountRoot = query.from(AccountEntity.class);
            Root<SaleEntity> saleRoot = query.from(SaleEntity.class);
            query.multiselect(
                    root.get(CustomerEntity_.id),
                    root.get(CustomerEntity_.name),
                    root.get(CustomerEntity_.phoneNumber),
                    root.get(CustomerEntity_.shippingAddress),
                    root.get(CustomerEntity_.discount),
                    accountRoot.get(AccountEntity_.balance),
                    criteriaBuilder.least(saleRoot.get(SaleEntity_.moment)),
                    criteriaBuilder.greatest(saleRoot.get(SaleEntity_.moment)),
                    criteriaBuilder.count(saleRoot),
                    criteriaBuilder.sum(saleRoot.get(SaleEntity_.amount)),
                    root.get(CustomerEntity_.discount)
            );
            query.where(
                    criteriaBuilder.equal(root, accountRoot.get(AccountEntity_.counterparty)),
                    criteriaBuilder.equal(root, saleRoot.get(SaleEntity_.customer))
            );
            query.groupBy(root);
            return query.getRestriction();
        };
    }

    public static Specification<CustomerEntity> filterByField(String field, Object value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(field), value);
    }

    public static Specification<CustomerEntity> sortByField(String field, boolean descending) {
        return (root, query, criteriaBuilder) -> {
            if (descending) {
                query.orderBy(criteriaBuilder.desc(root.get(field)));
            } else {
                query.orderBy(criteriaBuilder.asc(root.get(field)));
            }
            return null;
        };
    }
}
