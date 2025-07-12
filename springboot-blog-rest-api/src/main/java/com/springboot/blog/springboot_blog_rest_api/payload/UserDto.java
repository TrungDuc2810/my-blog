package com.springboot.blog.springboot_blog_rest_api.payload;

import com.springboot.blog.springboot_blog_rest_api.entity.Role;
import com.springboot.blog.springboot_blog_rest_api.payload.common.BaseDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends BaseDto {
    private String name;
    @NotEmpty
    @Size(min = 2, max = 30, message="Username should have at least 2 characters and at most 10 characters")
    private String username;
    @NotEmpty
    private String email;
    private boolean enabled;
    private Set<Role> roles;
}
