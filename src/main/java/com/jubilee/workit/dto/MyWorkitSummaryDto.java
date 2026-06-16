package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MyWorkitSummaryDto {
    private ProfileDto profile;
    private long bookmarkedCount;
    private long recentlyViewedCount;
    private long applicationCount;
    private long unreadNotificationCount;
    private long unreadChatCount;
}
