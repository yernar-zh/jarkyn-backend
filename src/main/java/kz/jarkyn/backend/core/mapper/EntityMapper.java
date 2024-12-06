
package kz.jarkyn.backend.core.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.counterparty.model.*;
import kz.jarkyn.backend.good.model.*;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.stock.mode.TurnoverEntity;
import kz.jarkyn.backend.user.model.UserEntity;
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

    // New methods for your entities
    public AccountEntity toAccountEntity(IdDto api) {
        return toEntity(api, AccountEntity.class);
    }

    public CounterpartyEntity toCounterpartyEntity(IdDto api) {
        return toEntity(api, CounterpartyEntity.class);
    }

    public CustomerEntity toCustomerEntity(IdDto api) {
        return toEntity(api, CustomerEntity.class);
    }

    public OrganizationEntity toOrganizationEntity(IdDto api) {
        return toEntity(api, OrganizationEntity.class);
    }

    public SupplierEntity toSupplierEntity(IdDto api) {
        return toEntity(api, SupplierEntity.class);
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

    private <T extends AbstractEntity> T toEntity(IdDto api, Class<T> clazz) {
        if (api == null) {
            return null;
        }
        T entity = entityManager.find(clazz, api.getId());
        return ExceptionUtils.requireNonNullApi(entity, clazz.getName());
    }
}