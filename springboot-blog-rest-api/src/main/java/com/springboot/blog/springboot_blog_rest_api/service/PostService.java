package com.springboot.blog.springboot_blog_rest_api.service;

import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.PostDto;


public interface PostService {
    PostDto createPost(PostDto postDto);
    ListResponse<PostDto> getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);
    PostDto getPostById(Long id);
    PostDto updatePost(PostDto postDTO, Long id);
    void deletePostById(Long id);
    int getTotalPosts();
    ListResponse<PostDto> getPostsByCategoryId(Long categoryId, int pageNo, int pageSize, String sortBy, String sortDir);
    ListResponse<PostDto> searchPostsByTitle(String title, int pageNo, int pageSize, String sortBy, String sortDir);
    String generateDescription(String content);
}
