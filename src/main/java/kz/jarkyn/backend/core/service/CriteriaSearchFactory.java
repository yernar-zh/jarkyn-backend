package kz.jarkyn.backend.core.service;

import jakarta.persistence.EntityManager;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class CriteriaSearchFactory {
    private final EntityManager entityManager;
    private final ConversionService conversionService;

    public CriteriaSearchFactory(EntityManager entityManager, ConversionService conversionService) {
        this.entityManager = entityManager;
        this.conversionService = conversionService;
    }

    public <T> CriteriaSearch<T> createAsdf(Class<T> javaClass) {
        return new CriteriaSearch<>(entityManager, javaClass, conversionService);
    }

}
