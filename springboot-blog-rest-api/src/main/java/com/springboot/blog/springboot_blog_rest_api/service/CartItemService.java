package com.springboot.blog.springboot_blog_rest_api.service;

import com.springboot.blog.springboot_blog_rest_api.payload.CartItemDto;
import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;

import java.util.List;

public interface CartItemService {
    CartItemDto addCartItem(CartItemDto cartItemDto);
    List<CartItemDto> addBulkCartItems(List<CartItemDto> cartItemDtos);
    CartItemDto updateCartItem(long id, CartItemDto cartItemDto);
    CartItemDto getCartItem(long id);
    ListResponse<CartItemDto> getAllCartItems(int pageNo, int pageSize, String sortBy, String sortDir);
    ListResponse<CartItemDto> getCartItemsByUserId(long userId, int pageNo, int pageSize, String sortBy, String sortDir);
    void deleteCartItem(long id);
}
