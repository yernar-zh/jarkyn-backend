package kz.jarkyn.backend.global.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity;

@Entity
@Table(name = "image")
public class ImageEntity extends AbstractEntity {
}
