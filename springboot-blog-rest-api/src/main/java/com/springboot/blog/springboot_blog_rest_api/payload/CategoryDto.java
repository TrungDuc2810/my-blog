package com.springboot.blog.springboot_blog_rest_api.payload;

import com.springboot.blog.springboot_blog_rest_api.payload.common.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto extends BaseDto {
    private String name;
    private String description;
}
