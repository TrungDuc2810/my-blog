package com.springboot.blog.springboot_blog_rest_api.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private long id;
    private int quantity;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private long productId;
    private long orderId;
}
