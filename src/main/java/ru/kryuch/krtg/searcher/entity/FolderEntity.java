package ru.kryuch.krtg.searcher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "krrg_folders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    private String title;

    private Boolean target = false;

    @Column(name = "tg_id")
    private Integer tgId;
}
