package com.springboot.blog.springboot_blog_rest_api.service;

import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.NotificationDto;

public interface NotificationService {
    ListResponse<NotificationDto> getAllNotifications(int pageNo, int pageSize, String sortBy, String sortDir);
    int getTotalNotifications();
}
