package ru.kryuch.krtg.searcher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.setting.SettingGroupEntity;
import ru.kryuch.krtg.searcher.type.SettingType;


@Entity
@Table(name = "krrg_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingEntity extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private SettingGroupEntity group;

    @Enumerated(EnumType.STRING)
    @Column(name = "setting_type", nullable = false, length = 20)
    private SettingType type;

    @Column(name = "sort_order")
    private Integer sortOrder;
}