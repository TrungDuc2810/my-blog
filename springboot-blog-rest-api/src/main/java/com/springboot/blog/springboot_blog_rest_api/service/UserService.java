package com.springboot.blog.springboot_blog_rest_api.service;

import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.UserDto;

public interface UserService {
    ListResponse<UserDto> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir);
    UserDto getUserByUsername(String username);
    int getTotalUsers();
    void deleteUserById(long id);
}
