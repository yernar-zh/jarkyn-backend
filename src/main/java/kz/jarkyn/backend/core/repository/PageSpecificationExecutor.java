package kz.jarkyn.backend.core.repository;

import kz.jarkyn.backend.core.PageSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PageSpecificationExecutor<T> extends JpaSpecificationExecutor<T> {
    default Page<T> findAll(PageSpecification<T> pageSpecification) {
        return findAll(pageSpecification.getSpecification(), pageSpecification.getPageable());
    }
}