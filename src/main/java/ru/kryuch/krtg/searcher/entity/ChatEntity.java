package ru.kryuch.krtg.searcher.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
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
}
