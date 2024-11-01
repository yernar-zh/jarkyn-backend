
package kz.jarkyn.backend.core.service.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.model.AttributeGroupEntity;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.good.model.GroupEntity;
import org.springframework.stereotype.Service;

@Service
public class EntityMapper {
    @PersistenceContext
    private EntityManager entityManager;

    public GoodEntity toGoodEntity(IdDto api) {
        return toEntity(api, GoodEntity.class);
    }

    public GroupEntity toGroupEntity(IdDto api) {
        return toEntity(api, GroupEntity.class);
    }

    public AttributeEntity toAttributeEntity(IdDto api) {
        return toEntity(api, AttributeEntity.class);
    }

    public AttributeGroupEntity toAttributeGroupEntity(IdDto api) {
        return toEntity(api, AttributeGroupEntity.class);
    }

    private <T extends AbstractEntity> T toEntity(IdDto api, Class<T> clazz) {
        if (api == null) {
            return null;
        }
        T entity = entityManager.find(clazz, api.getId());
        return ExceptionUtils.requireNonNullApi(entity, clazz.getName());
    }
}
