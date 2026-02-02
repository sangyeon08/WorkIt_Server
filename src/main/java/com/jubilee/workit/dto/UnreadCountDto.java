package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UnreadCountDto {
    private Long unreadCount;

    public UnreadCountDto() {}

    public UnreadCountDto(Long unreadCount) {
        this.unreadCount = unreadCount;
    }

}