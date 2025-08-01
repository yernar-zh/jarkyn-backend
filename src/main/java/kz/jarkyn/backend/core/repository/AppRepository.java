package kz.jarkyn.backend.core.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface AppRepository<T> extends
        JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {

    default T findOne(Specification<T> spec, Sort sort) {
        PageRequest pageRequest = PageRequest.of(0, 1, sort);
        return findAll(spec, pageRequest).stream().findFirst().orElse(null);
    }
}
