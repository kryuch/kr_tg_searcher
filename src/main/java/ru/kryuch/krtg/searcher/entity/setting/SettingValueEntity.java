package ru.kryuch.krtg.searcher.entity.setting;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import ru.kryuch.krtg.searcher.entity.BasedAccessEntity;
import ru.kryuch.krtg.searcher.entity.SettingEntity;

@Entity
@Table(name = "krrg_setting_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingValueEntity extends BasedAccessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setting_id", nullable = false)
    private SettingEntity setting;

    @Column(name = "bool_value")
    private Boolean boolValue;

    @Column(name = "int_value")
    private Integer intValue;

    @Column(name = "double_value")
    private Double doubleValue;

    @Column(name = "string_value")
    private String stringValue;
}