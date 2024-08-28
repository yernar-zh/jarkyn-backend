package kz.jarkyn.backend.model.counterparty;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "organization")
public class OrganizationEntity extends CounterpartyEntity {
}
