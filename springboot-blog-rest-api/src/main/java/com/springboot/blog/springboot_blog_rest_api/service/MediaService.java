package com.springboot.blog.springboot_blog_rest_api.service;

import com.springboot.blog.springboot_blog_rest_api.payload.MediaDto;

import java.util.List;

public interface MediaService {
    List<MediaDto> getAllMedia();
    int getTotalMedia();
    MediaDto getMediaByPostId(long postId);
}
