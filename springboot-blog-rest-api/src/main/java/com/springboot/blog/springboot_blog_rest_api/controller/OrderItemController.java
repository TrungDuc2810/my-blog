package com.springboot.blog.springboot_blog_rest_api.controller;

import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.OrderItemDto;
import com.springboot.blog.springboot_blog_rest_api.service.OrderItemService;
import com.springboot.blog.springboot_blog_rest_api.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {
    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public ResponseEntity<OrderItemDto> createOrderItem(@RequestBody OrderItemDto orderItemDto) {
        return new ResponseEntity<>(orderItemService.createOrderItem(orderItemDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDto> updateOrder(@PathVariable(name = "id") long id,
                                                @RequestBody OrderItemDto orderItemDto) {
        return new ResponseEntity<>(orderItemService.updateOrderItem(id, orderItemDto), HttpStatus.OK);
    }

    @GetMapping
    public ListResponse<OrderItemDto> getAllOrderItems(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {

        return orderItemService.getAllOrderItems(pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> getOrderItem(@PathVariable(name = "id") long id) {
        return new ResponseEntity<>(orderItemService.getOrderItem(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderItem(@PathVariable(name = "id") long id) {
        orderItemService.deleteOrderItem(id);
        return new ResponseEntity<>("Deleted successfully!!!", HttpStatus.OK);
    }
}
