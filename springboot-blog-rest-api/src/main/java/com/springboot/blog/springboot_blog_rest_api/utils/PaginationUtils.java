package com.springboot.blog.springboot_blog_rest_api.utils;

import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;

public class PaginationUtils {
    public static PageRequest createPageRequest(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }

    public static <Entity, Dto> ListResponse<Dto> toListResponse(Page<Entity> page,
                                                                 Function<Entity, Dto> mapper) {
        List<Dto> content = page.getContent().stream().map(mapper).toList();

        ListResponse<Dto> response = new ListResponse<>();
        response.setContent(content);
        response.setPageNo(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements((int)page.getTotalElements());
        response.setLast(page.isLast());

        return response;
    }
}
