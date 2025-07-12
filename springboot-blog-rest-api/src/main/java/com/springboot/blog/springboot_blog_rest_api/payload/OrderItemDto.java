package com.springboot.blog.springboot_blog_rest_api.payload;

import com.springboot.blog.springboot_blog_rest_api.payload.common.BaseDto;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto extends BaseDto {
    private int quantity;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private long productId;
    private long orderId;
}
