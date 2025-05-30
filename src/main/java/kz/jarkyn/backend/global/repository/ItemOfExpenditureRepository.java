package kz.jarkyn.backend.global.repository;

import kz.jarkyn.backend.global.model.ItemOfExpenditureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ItemOfExpenditureRepository extends JpaRepository<ItemOfExpenditureEntity, UUID> {
}