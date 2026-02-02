package com.jubilee.workit.controller;

import com.jubilee.workit.dto.BookmarkDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.service.BookmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping(value = "/bookmarks/{jobId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBookmark(
            @PathVariable Long jobId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        bookmarkService.addBookmark(jobId, userId);
    }

    @DeleteMapping(value = "/bookmarks/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookmark(
            @PathVariable Long jobId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        bookmarkService.removeBookmark(jobId, userId);
    }

    @GetMapping(value = "/bookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<BookmarkDto> getMyBookmarks(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return bookmarkService.getMyBookmarks(userId, page, size);
    }

    @GetMapping(value = "/bookmarks/{jobId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public BookmarkStatusDto checkBookmarkStatus(
            @PathVariable Long jobId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        boolean bookmarked = bookmarkService.isBookmarked(jobId, userId);
        BookmarkStatusDto dto = new BookmarkStatusDto();
        dto.setBookmarked(bookmarked);
        return dto;
    }

    public static class BookmarkStatusDto {
        private boolean bookmarked;

        public boolean isBookmarked() { return bookmarked; }
        public void setBookmarked(boolean bookmarked) { this.bookmarked = bookmarked; }
    }
}