package kz.jarkyn.backend.party.spesification;

import kz.jarkyn.backend.party.model.*;
import org.springframework.data.jpa.domain.Specification;

public class AccountSpecifications {
    public static Specification<AccountEntity> counterparty(CounterpartyEntity counterparty) {
        return (root, query, cb) -> cb.equal(root.get(AccountEntity_.counterparty), counterparty);
    }
}