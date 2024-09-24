package kz.jarkyn.backend.model.counterparty;


import jakarta.persistence.*;
import kz.jarkyn.backend.model.common.AbstractEntity;
import kz.jarkyn.backend.model.common.AbstractEntity_;

@Entity
@Table(name = "organization")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class OrganizationEntity extends CounterpartyEntity {
}
