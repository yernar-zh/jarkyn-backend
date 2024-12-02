package kz.jarkyn.backend.core.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public interface CriteriaAttribute<E> {
    Expression<?> get(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb);
}