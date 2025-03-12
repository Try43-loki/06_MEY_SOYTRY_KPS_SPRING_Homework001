package com.kshrd.spring_homework001.respond;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> items;
    private int totalElements;
    private int currentPage;
    private int pageSize;
    private int totalPages;
}
