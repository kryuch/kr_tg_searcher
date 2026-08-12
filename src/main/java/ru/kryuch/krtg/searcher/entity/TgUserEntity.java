package ru.kryuch.krtg.searcher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "krrg_tg_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TgUserEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String username;

    private String name;

    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar;

}
