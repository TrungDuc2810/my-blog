package com.springboot.blog.springboot_blog_rest_api.controller;

import com.springboot.blog.springboot_blog_rest_api.payload.CartItemDto;
import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.service.CartItemService;
import com.springboot.blog.springboot_blog_rest_api.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping
    public ResponseEntity<CartItemDto> createCartItem(@RequestBody CartItemDto cartItemDto) {
        return new ResponseEntity<>(cartItemService.addCartItem(cartItemDto), HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<CartItemDto>> addBulkCartItems(@RequestBody List<CartItemDto> cartItems) {
        List<CartItemDto> savedItems = cartItemService.addBulkCartItems(cartItems);
        return new ResponseEntity<>(savedItems, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemDto> updateCartItem(@PathVariable(name = "id") long id,
                                                      @RequestBody CartItemDto cartItemDto) {
        return new ResponseEntity<>(cartItemService.updateCartItem(id, cartItemDto), HttpStatus.OK);
    }

    @GetMapping
    public ListResponse<CartItemDto> getCartItems(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {

        return cartItemService.getAllCartItems(pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/users/{id}")
    public ListResponse<CartItemDto> getCartItemsByUserId(
            @PathVariable("id") long userId,
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {

        return cartItemService.getCartItemsByUserId(userId, pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartItemDto> getCartItem(@PathVariable(name = "id") long id) {
        return new ResponseEntity<>(cartItemService.getCartItem(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable(name = "id") long id) {
        cartItemService.deleteCartItem(id);
        return new ResponseEntity<>("Deleted successfully!!!", HttpStatus.OK);
    }
}
