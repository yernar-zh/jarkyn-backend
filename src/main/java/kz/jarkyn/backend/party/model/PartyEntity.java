package kz.jarkyn.backend.party.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.ReferenceEntity;

@Entity
@Table(name = "party")
@Inheritance(strategy = InheritanceType.JOINED)
public class PartyEntity extends ReferenceEntity {
}
