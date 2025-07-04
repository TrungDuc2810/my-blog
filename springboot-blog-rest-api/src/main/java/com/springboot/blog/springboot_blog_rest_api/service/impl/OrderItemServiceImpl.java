package com.springboot.blog.springboot_blog_rest_api.service.impl;

import com.springboot.blog.springboot_blog_rest_api.entity.*;
import com.springboot.blog.springboot_blog_rest_api.exception.ResourceNotFoundException;
import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.OrderItemDto;
import com.springboot.blog.springboot_blog_rest_api.repository.OrderItemRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.OrderRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.ProductRepository;
import com.springboot.blog.springboot_blog_rest_api.service.OrderItemService;
import com.springboot.blog.springboot_blog_rest_api.utils.Mapper;
import com.springboot.blog.springboot_blog_rest_api.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final Mapper mapper;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository,
                                OrderRepository orderRepository,
                                ProductRepository productRepository,
                                Mapper mapper) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    public OrderItemDto createOrderItem(OrderItemDto orderItemDto) {
        OrderItem orderItem = mapper.mapToEntity(orderItemDto, OrderItem.class);

        Product product = productRepository.findById(orderItemDto.getProductId()).orElseThrow(()
                -> new ResourceNotFoundException("Product", "id", String.valueOf(orderItemDto.getProductId())));

        Order order = orderRepository.findById(orderItemDto.getOrderId()).orElseThrow(()
                -> new ResourceNotFoundException("Order", "id", String.valueOf(orderItemDto.getOrderId())));

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        return mapper.mapToDto(savedOrderItem, OrderItemDto.class);
    }

    @Override
    public OrderItemDto updateOrderItem(long id, OrderItemDto orderItemDto) {
        OrderItem orderItem = orderItemRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("OrderItem", "id", String.valueOf(id)));

        Product product = productRepository.findById(orderItemDto.getProductId()).orElseThrow(()
                -> new ResourceNotFoundException("Product", "id", String.valueOf(orderItemDto.getProductId())));

        Order order = orderRepository.findById(orderItemDto.getOrderId()).orElseThrow(()
                -> new ResourceNotFoundException("Order", "id", String.valueOf(orderItemDto.getOrderId())));

        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setPrice(orderItemDto.getPrice());
        orderItem.setDiscountPrice(orderItemDto.getDiscountPrice());
        orderItem.setProduct(product);
        orderItem.setOrder(order);

        return mapper.mapToDto(orderItemRepository.save(orderItem), OrderItemDto.class);
    }

    @Override
    public OrderItemDto getOrderItem(long id) {
        OrderItem orderItem = orderItemRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("OrderItem", "id", String.valueOf(id)));

        return mapper.mapToDto(orderItem, OrderItemDto.class);
    }

    @Override
    public ListResponse<OrderItemDto> getAllOrderItems(int pageNo, int pageSize, String sortBy, String sortDir) {
        PageRequest pageRequest = PaginationUtils.createPageRequest(pageNo, pageSize, sortBy, sortDir);
        Page<OrderItem> orderItemPage = orderItemRepository.findAll(pageRequest);

        return PaginationUtils.toListResponse(orderItemPage,
                orderItem -> mapper.mapToDto(orderItem, OrderItemDto.class));
    }

    @Override
    public List<OrderItemDto> getOrderItemsByOrderId(long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "id", String.valueOf(orderId)));

        List<OrderItem> orderItems = orderItemRepository.findOrderItemsByOrderId(orderId);

        return orderItems.stream()
                .map(orderItem -> mapper.mapToDto(orderItem, OrderItemDto.class))
                .toList();
    }

    @Override
    public void deleteOrderItem(long id) {
        OrderItem orderItem = orderItemRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("OrderItem", "id", String.valueOf(id)));

        orderItemRepository.delete(orderItem);
    }
}
