package com.springboot.blog.springboot_blog_rest_api.service;

import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.OrderItemDto;

public interface OrderItemService {
    OrderItemDto createOrderItem(OrderItemDto orderItemDto);
    OrderItemDto updateOrderItem(long id, OrderItemDto orderItemDto);
    OrderItemDto getOrderItem(long id);
    ListResponse<OrderItemDto> getAllOrderItems(int pageNo, int pageSize, String sortBy, String sortDir);
    void deleteOrderItem(long id);
}
