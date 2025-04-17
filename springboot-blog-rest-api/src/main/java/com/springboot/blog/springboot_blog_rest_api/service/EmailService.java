package com.springboot.blog.springboot_blog_rest_api.service;

public interface EmailService {
    void sendMail(String email, String subject, String content);
}
