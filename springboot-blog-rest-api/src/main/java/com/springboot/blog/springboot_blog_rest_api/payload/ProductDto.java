package com.springboot.blog.springboot_blog_rest_api.payload;

import com.springboot.blog.springboot_blog_rest_api.payload.common.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto extends BaseDto {
    private String name;
    private String description;
    private double price;
    private double discountPrice;
    private String language;
    private ProductThumbnailDto productThumbnail;
}
