package kz.jarkyn.backend.repository;

import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.attribute.AttributeGroupEntity;
import kz.jarkyn.backend.model.good.GoodAttributeEntity;
import kz.jarkyn.backend.model.good.GoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttributeRepository extends JpaRepository<AttributeEntity, UUID> {
    List<AttributeEntity> findByGroup(AttributeGroupEntity entity);
    @Query("""
                SELECT goa.attribute FROM GoodAttributeEntity goa
                WHERE goa.good = :good
            """)
    List<AttributeEntity> findByGood(@Param("good") GoodEntity good);
}