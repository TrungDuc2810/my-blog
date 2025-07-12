package com.springboot.blog.springboot_blog_rest_api.payload;

import com.springboot.blog.springboot_blog_rest_api.payload.common.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto extends BaseDto {
    private String name;
    private String username;
    private String email;
    private String password;
}
