package com.springboot.blog.springboot_blog_rest_api.payload;

import lombok.Data;

@Data
public class CartItemDto {
    private long id;
    private int quantity;
    private long productId;
    private long userId;
}
