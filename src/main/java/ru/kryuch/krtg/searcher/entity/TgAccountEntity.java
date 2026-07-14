package ru.kryuch.krtg.searcher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "krrg_tg")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TgAccountEntity extends BasedAccessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String description;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "app_hash")
    private String appHash;

    private String phone;

    @Column(name = "is_auth")
    private Boolean isAuth = false;

}
