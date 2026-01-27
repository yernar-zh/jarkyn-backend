package kz.jarkyn.backend.good.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.good.model.GroupEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends AppRepository<GroupEntity> {
    List<GroupEntity> findByParent(GroupEntity parent);
}