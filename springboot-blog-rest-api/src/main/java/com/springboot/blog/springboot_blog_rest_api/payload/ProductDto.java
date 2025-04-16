package com.springboot.blog.springboot_blog_rest_api.payload;

import com.springboot.blog.springboot_blog_rest_api.entity.ProductThumbnail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private long id;
    private String name;
    private String description;
    private double price;
    private double discountPrice;
    private String language;
    private String createdAt;
    private String updatedAt;
    private ProductThumbnailDto productThumbnail;
}
