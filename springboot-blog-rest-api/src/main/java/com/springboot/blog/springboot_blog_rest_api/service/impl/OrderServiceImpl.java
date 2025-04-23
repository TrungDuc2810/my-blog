package com.springboot.blog.springboot_blog_rest_api.service.impl;

import com.springboot.blog.springboot_blog_rest_api.entity.Order;
import com.springboot.blog.springboot_blog_rest_api.entity.User;
import com.springboot.blog.springboot_blog_rest_api.exception.ResourceNotFoundException;
import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.OrderDto;
import com.springboot.blog.springboot_blog_rest_api.repository.OrderRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.UserRepository;
import com.springboot.blog.springboot_blog_rest_api.service.OrderService;
import com.springboot.blog.springboot_blog_rest_api.utils.Mapper;
import com.springboot.blog.springboot_blog_rest_api.utils.PaginationUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            Mapper mapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public OrderDto addOrder(OrderDto orderDto) {
        Order order = mapper.mapToEntity(orderDto, Order.class);

        User user = userRepository.findById(orderDto.getUserId()).orElseThrow(()
                -> new ResourceNotFoundException("User", "id", String.valueOf(orderDto.getUserId())));

        Order savedOrder = orderRepository.save(order);

        return mapper.mapToDto(savedOrder, OrderDto.class);
    }

    @Override
    @Transactional
    public OrderDto updateOrder(long id, OrderDto orderDto) {
        Order order = orderRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Order", "id", String.valueOf(id)));

        order.setStatus(orderDto.getStatus());

        return mapper.mapToDto(order, OrderDto.class);
    }

    @Override
    public OrderDto getOrderById(long id) {
        Order order = orderRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Order", "id", String.valueOf(id)));

        return mapper.mapToDto(order, OrderDto.class);
    }

    @Override
    public ListResponse<OrderDto> getAllOrders(int pageNo, int pageSize, String sortBy, String sortDir) {
        PageRequest pageRequest = PaginationUtils.createPageRequest(pageNo, pageSize, sortBy, sortDir);

        Page<Order> orders = orderRepository.findAll(pageRequest);

        return PaginationUtils.toListResponse(orders,
                order -> mapper.mapToDto(order, OrderDto.class));
    }

    @Override
    public ListResponse<OrderDto> getOrdersByUserId(long userId, int pageNo, int pageSize, String sortBy, String sortDir) {
        PageRequest pageRequest = PaginationUtils.createPageRequest(pageNo, pageSize, sortBy, sortDir);

        Page<Order> orders = orderRepository.findByUserId(userId, pageRequest);

        return PaginationUtils.toListResponse(orders,
                order -> mapper.mapToDto(order, OrderDto.class));
    }

    @Override
    public void deleteOrder(long id) {
        Order order = orderRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Order", "id", String.valueOf(id)));

        orderRepository.delete(order);
    }
}
