package ru.kryuch.krtg.searcher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@MappedSuperclass
public class BasedAccessEntity extends TimestampedEntity {

    @Column(name = "user_id", nullable = false)
    protected Integer userId;

}
