package com.jubilee.workit.controller;

import com.jubilee.workit.dto.BookmarkDto;
import com.jubilee.workit.dto.BulkJobIdsRequest;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Bookmark", description = "북마크 API")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping(value = "/bookmarks/{jobId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "북마크 추가", description = "특정 공고를 북마크에 추가합니다.")
    @ApiResponse(responseCode = "201", description = "북마크 추가 성공")
    public void addBookmark(
            @Parameter(description = "공고 ID") @PathVariable Long jobId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        bookmarkService.addBookmark(jobId, userId);
    }

    @DeleteMapping(value = "/bookmarks/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "북마크 삭제", description = "특정 공고를 북마크에서 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "북마크 삭제 성공")
    public void removeBookmark(
            @Parameter(description = "공고 ID") @PathVariable Long jobId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        bookmarkService.removeBookmark(jobId, userId);
    }

    @DeleteMapping(value = "/bookmarks", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "북마크 일괄 삭제", description = "여러 공고를 북마크에서 일괄 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "일괄 삭제 성공")
    public void removeBookmarks(
            @RequestBody BulkJobIdsRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        bookmarkService.removeBookmarks(userId, request.getJobIds());
    }

    @GetMapping(value = "/bookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "내 북마크 목록 조회", description = "로그인한 사용자의 북마크 목록을 조회합니다.")
    public PageResponse<BookmarkDto> getMyBookmarks(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return bookmarkService.getMyBookmarks(userId, page, size);
    }

    @GetMapping(value = "/bookmarks/{jobId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "북마크 상태 확인", description = "특정 공고의 북마크 여부를 확인합니다.")
    public BookmarkStatusDto checkBookmarkStatus(
            @Parameter(description = "공고 ID") @PathVariable Long jobId,
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
