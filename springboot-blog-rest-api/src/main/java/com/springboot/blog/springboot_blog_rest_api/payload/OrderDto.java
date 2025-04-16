package com.springboot.blog.springboot_blog_rest_api.payload;

import com.springboot.blog.springboot_blog_rest_api.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDto {
    private long id;
    private BigDecimal amountMoney;
    private LocalDateTime createdAt;
    private long userId;
    private OrderStatus status;
}
