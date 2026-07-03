package ru.kryuch.krtg.searcher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "krrg_chats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String username;

    private String name;

    private Integer status;

    public ChatEntity(Long id) {
        this.id = id;
    }
}
