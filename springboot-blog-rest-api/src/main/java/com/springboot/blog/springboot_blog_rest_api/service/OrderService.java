package com.springboot.blog.springboot_blog_rest_api.service;

import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.OrderDto;

public interface OrderService {
    OrderDto addOrder(OrderDto orderDto);
    OrderDto updateOrder(long id, OrderDto orderDto);
    OrderDto getOrderById(long id);
    ListResponse<OrderDto> getAllOrders(int pageNo, int pageSize, String sortBy, String sortDir);
    ListResponse<OrderDto> getOrdersByUserId(long userId, int pageNo, int pageSize, String sortBy, String sortDir);
    void deleteOrder(long id);
}
