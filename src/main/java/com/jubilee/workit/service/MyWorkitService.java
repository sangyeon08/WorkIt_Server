package com.jubilee.workit.service;

import com.jubilee.workit.dto.MyWorkitSummaryDto;
import com.jubilee.workit.repository.ApplicationRepository;
import com.jubilee.workit.repository.BookmarkRepository;
import com.jubilee.workit.repository.ChatMessageRepository;
import com.jubilee.workit.repository.NotificationRepository;
import com.jubilee.workit.repository.RecentlyViewedJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MyWorkitService {

    private final ProfileService profileService;
    private final BookmarkRepository bookmarkRepository;
    private final RecentlyViewedJobRepository recentlyViewedJobRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationRepository notificationRepository;
    private final ChatMessageRepository chatMessageRepository;

    public MyWorkitService(ProfileService profileService,
                           BookmarkRepository bookmarkRepository,
                           RecentlyViewedJobRepository recentlyViewedJobRepository,
                           ApplicationRepository applicationRepository,
                           NotificationRepository notificationRepository,
                           ChatMessageRepository chatMessageRepository) {
        this.profileService = profileService;
        this.bookmarkRepository = bookmarkRepository;
        this.recentlyViewedJobRepository = recentlyViewedJobRepository;
        this.applicationRepository = applicationRepository;
        this.notificationRepository = notificationRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public MyWorkitSummaryDto getSummary(Long userId) {
        MyWorkitSummaryDto dto = new MyWorkitSummaryDto();
        dto.setProfile(profileService.getProfile(userId));
        dto.setBookmarkedCount(bookmarkRepository.countByUser_Id(userId));
        dto.setRecentlyViewedCount(recentlyViewedJobRepository.countByUser_Id(userId));
        dto.setApplicationCount(applicationRepository.countByUser_Id(userId));
        dto.setUnreadNotificationCount(notificationRepository.countByUser_IdAndReadFalse(userId));
        dto.setUnreadChatCount(chatMessageRepository.countUnreadMessagesForUser(userId));
        return dto;
    }
}
