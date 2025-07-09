package com.springboot.blog.springboot_blog_rest_api.service.impl;

import com.springboot.blog.springboot_blog_rest_api.entity.Notification;
import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.NotificationDto;
import com.springboot.blog.springboot_blog_rest_api.repository.NotificationRepository;
import com.springboot.blog.springboot_blog_rest_api.service.NotificationService;
import com.springboot.blog.springboot_blog_rest_api.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private ModelMapper modelMapper;

    private NotificationDto mapToDto(Notification notification) {
        return modelMapper.map(notification, NotificationDto.class);
    }

    private Notification mapToEntity(NotificationDto notificationDto) {
        return modelMapper.map(notificationDto, Notification.class);
    }

    @Override
    public ListResponse<NotificationDto> getAllNotifications(int pageNo, int pageSize, String sortBy, String sortDir) {
        PageRequest pageRequest = PaginationUtils.createPageRequest(pageNo, pageSize, sortBy, sortDir);

        Page<Notification> notifications = notificationRepository.findAll(pageRequest);

        return PaginationUtils.toListResponse(notifications, this::mapToDto);
    }

    @Override
    public int getTotalNotifications() {
        return (int)notificationRepository.count();
    }
}
