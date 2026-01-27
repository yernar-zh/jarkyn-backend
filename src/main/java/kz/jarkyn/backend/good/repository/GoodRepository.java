package kz.jarkyn.backend.good.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.good.model.GroupEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodRepository extends AppRepository<GoodEntity> {
    List<GoodEntity> findByGroup(GroupEntity group);

    @Query("""
                SELECT god FROM GoodEntity god
                JOIN GoodAttributeEntity gda ON god = gda.good
                WHERE gda.attribute = :attribute
            """)
    List<GoodEntity> findByAttribute(@Param("attribute") AttributeEntity attribute);
}