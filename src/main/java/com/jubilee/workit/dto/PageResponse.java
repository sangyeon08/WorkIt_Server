package com.jubilee.workit.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public static <T> PageResponse<T> of(Page<T> page) {
        PageResponse<T> r = new PageResponse<>();
        r.setContent(page.getContent());
        r.setPage(page.getNumber());
        r.setSize(page.getSize());
        r.setTotalElements(page.getTotalElements());
        r.setTotalPages(page.getTotalPages());
        r.setFirst(page.isFirst());
        r.setLast(page.isLast());
        return r;
    }

    public void setContent(List<T> content) { this.content = content; }

    public void setPage(int page) { this.page = page; }

    public void setSize(int size) { this.size = size; }

    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public void setFirst(boolean first) { this.first = first; }

    public void setLast(boolean last) { this.last = last; }
}
