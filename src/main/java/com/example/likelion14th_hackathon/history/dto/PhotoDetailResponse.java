package com.example.likelion14th_hackathon.history.dto;

import com.example.likelion14th_hackathon.history.entity.PhotoDetail;
import lombok.Getter;

@Getter
public class PhotoDetailResponse {

    private final Long historyId;
    private final String photoUrl;
    private final String userMemo;
    private final String aiMemo;

    public PhotoDetailResponse(PhotoDetail photoDetail) {
        this.historyId = photoDetail.getHistoryItem().getHistoryId();
        this.photoUrl = photoDetail.getPhotoUrl();
        this.userMemo = photoDetail.getUserMemo();
        this.aiMemo = photoDetail.getAiMemo();
    }
}