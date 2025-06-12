package kz.jarkyn.backend.core.search;

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import kz.jarkyn.backend.core.model.EnumTypeEntity;
import kz.jarkyn.backend.core.model.EnumTypeEntity_;
import kz.jarkyn.backend.core.model.ReferenceEntity;
import kz.jarkyn.backend.core.model.ReferenceEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.party.model.OrganizationEntity;
import org.springframework.data.util.Pair;

import java.util.*;

public class CriteriaAttributes<E> {
    private final Map<String, CriteriaAttribute<E>> attributes;

    private CriteriaAttributes(Map<String, CriteriaAttribute<E>> attributes
    ) {
        this.attributes = attributes;
    }

    public Map<String, CriteriaAttribute<E>> getAttributes() {
        return attributes;
    }


    public static <E> Builder<E> builder() {
        return new Builder<>();
    }

    public static class Builder<E> {
        private final Map<String, CriteriaAttribute<E>> attributes;

        public Builder() {
            this.attributes = new LinkedHashMap<>();
        }

        public Builder<E> add(String fieldName, CriteriaAttribute<E> attribute) {
            attributes.put(fieldName, attribute);
            return this;
        }

        public Builder<E> add(String fieldName, ShortCriteriaAttribute<E> attribute) {
            attributes.put(fieldName, (root, query, cb, expressions) -> attribute.get(root));
            return this;
        }

        public Builder<E> addReference(String fieldName, ReferenceCriteriaAttribute<E> attribute) {
            add(fieldName + ".id", root -> attribute.get(root).get(ReferenceEntity_.id));
            add(fieldName + ".name", root -> attribute.get(root).get(ReferenceEntity_.name));
            add(fieldName + ".archived", root -> attribute.get(root).get(ReferenceEntity_.archived));
            return this;
        }

        public Builder<E> addEnumType(String fieldName, EnumTypeCriteriaAttribute<E> attribute) {
            add(fieldName + ".id", root -> attribute.get(root).get(EnumTypeEntity_.id));
            add(fieldName + ".name", root -> attribute.get(root).get(EnumTypeEntity_.name));
            add(fieldName + ".code", root -> attribute.get(root).get(EnumTypeEntity_.code));
            add(fieldName + ".archived", root -> attribute.get(root).get(EnumTypeEntity_.archived));
            return this;
        }

        public CriteriaAttributes<E> build() {
            return new CriteriaAttributes<>(attributes);
        }
    }

    public interface CriteriaAttribute<E> {
        Expression<?> get(Root<E> root, CriteriaQuery<?> query,
                          CriteriaBuilder cb, Map<String, Expression<?>> map);
    }

    public interface ShortCriteriaAttribute<E> {
        Expression<?> get(Root<E> root);
    }

    public interface ReferenceCriteriaAttribute<E> {
        Path<? extends ReferenceEntity> get(Root<E> root);
    }

    public interface EnumTypeCriteriaAttribute<E> {
        Path<? extends EnumTypeEntity> get(Root<E> root);
    }
}
