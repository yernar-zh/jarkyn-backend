package kz.jarkyn.backend.warehouse.repository;

import kz.jarkyn.backend.warehouse.model.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {
    List<GroupEntity> findByParent(GroupEntity parent);
}