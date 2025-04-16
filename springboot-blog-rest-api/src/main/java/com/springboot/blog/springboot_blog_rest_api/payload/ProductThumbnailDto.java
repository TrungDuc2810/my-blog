package com.springboot.blog.springboot_blog_rest_api.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductThumbnailDto {
    private long id;
    private String name;
    private String type;
    private String filePath;
}
