package kz.jarkyn.backend.party.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;

@Entity
@Table(name = "organization")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class OrganizationEntity extends PartyEntity {
}
