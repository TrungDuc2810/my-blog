package com.springboot.blog.springboot_blog_rest_api.payload;

import com.springboot.blog.springboot_blog_rest_api.entity.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private long id;
    private String name;
    @NotEmpty
    @Size(min = 2, max = 30, message="Username should have at least 2 characters and at most 10 characters")
    private String username;
    @NotEmpty
    private String email;
    @NotEmpty
    private String registeredAt;
    private boolean enabled;
    private Set<Role> roles;
}
