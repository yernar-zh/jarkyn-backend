
package kz.jarkyn.backend.core.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.party.model.*;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import kz.jarkyn.backend.warehouse.model.*;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import kz.jarkyn.backend.user.model.UserEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    public AccountEntity toAccountEntity(IdDto api) {
        return toEntity(api, AccountEntity.class);
    }

    public WarehouseEntity toWarehouseEntity(IdDto api) {
        return toEntity(api, WarehouseEntity.class);
    }

    public TurnoverEntity toTurnoverEntity(IdDto api) {
        return toEntity(api, TurnoverEntity.class);
    }

    public GoodAttributeEntity toGoodAttributeEntity(IdDto api) {
        return toEntity(api, GoodAttributeEntity.class);
    }

    public SellingPriceEntity toSellingPriceEntity(IdDto api) {
        return toEntity(api, SellingPriceEntity.class);
    }

    public UserEntity toUserEntity(IdDto api) {
        return toEntity(api, UserEntity.class);
    }

    public CurrencyEntity toCurrencyEntity(IdDto api) {
        return toEntity(api, CurrencyEntity.class);
    }

    public <T extends PartyEntity> T toPartyEntity(IdDto api) {
        return (T) toEntity(api, PartyEntity.class);
    }

    public <T extends DocumentEntity> T toDocumentEntity(IdDto api) {
        return (T) toEntity(api, DocumentEntity.class);
    }

    private <T extends AbstractEntity> T toEntity(IdDto api, Class<T> clazz) {
        if (api == null) {
            return null;
        }
        T entity = entityManager.find(clazz, api.getId());
        return ExceptionUtils.requireNonNullApi(entity, clazz.getName());
    }

    public <T extends AbstractEntity> T toEntity(UUID id, Class<T> clazz) {
        if (id == null) {
            return null;
        }
        T entity = entityManager.find(clazz, id);
        return ExceptionUtils.requireNonNullApi(entity, clazz.getName());
    }
}
