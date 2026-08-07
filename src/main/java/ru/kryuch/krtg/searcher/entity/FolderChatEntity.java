package ru.kryuch.krtg.searcher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "krrg_folder_chat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FolderChatId.class)
public class FolderChatEntity {


    @Id
    @Column(name = "folder_id")
    private Integer folderId;

    @Id
    @Column(name = "chat_user_id")
    private Long chatUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", insertable = false, updatable = false)
    private FolderEntity folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_user_id", insertable = false, updatable = false)
    private TgUserEntity chatUser;

    public FolderChatEntity(Integer folderId, Long chatUserId) {
        this.folderId = folderId;
        this.chatUserId = chatUserId;
    }
}