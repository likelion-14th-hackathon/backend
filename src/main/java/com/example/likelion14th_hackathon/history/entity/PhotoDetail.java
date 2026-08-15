package com.example.likelion14th_hackathon.history.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class PhotoDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long photoId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_item_id", nullable = false)
    private HistoryItem historyItem;

    @Column(nullable = false)
    private String photoUrl;

    private String userMemo;

    private String aiMemo;   // 나중에 생성될 수 있어 null 허용

    protected PhotoDetail() {}

    public PhotoDetail(HistoryItem historyItem, String photoUrl, String userMemo) {
        this.historyItem = historyItem;
        this.photoUrl = photoUrl;
        this.userMemo = userMemo;
    }
}
