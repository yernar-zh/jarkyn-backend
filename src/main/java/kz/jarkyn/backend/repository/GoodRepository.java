package kz.jarkyn.backend.repository;

import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.good.GoodEntity;
import kz.jarkyn.backend.model.group.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoodRepository extends JpaRepository<GoodEntity, UUID> {
    List<GoodEntity> findByGroup(GroupEntity group);

    @Query("""
                SELECT god FROM GoodEntity god
                JOIN GoodAttributeEntity gda ON god = gda.good
                WHERE gda.attribute = :attribute
            """)
    List<GoodEntity> findByAttribute(@Param("attribute") AttributeEntity attribute);
}