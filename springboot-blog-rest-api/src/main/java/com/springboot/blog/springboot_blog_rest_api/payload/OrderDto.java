package com.springboot.blog.springboot_blog_rest_api.payload;

import com.springboot.blog.springboot_blog_rest_api.entity.OrderStatus;
import com.springboot.blog.springboot_blog_rest_api.payload.common.BaseDto;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto extends BaseDto {
    private BigDecimal amountMoney;
    private long userId;
    private OrderStatus status;
}
