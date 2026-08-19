package ru.kryuch.krtg.searcher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@MappedSuperclass
public class BasedAccessEntity {

    @Column(name = "user_id")
    protected Integer userId;
}
