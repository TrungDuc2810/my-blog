package com.springboot.blog.springboot_blog_rest_api.service;

import com.springboot.blog.springboot_blog_rest_api.payload.CartItemDto;
import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;

public interface CartItemService {
    CartItemDto addCartItem(CartItemDto cartItemDto);
    CartItemDto updateCartItem(long id, CartItemDto cartItemDto);
    CartItemDto getCartItem(long id);
    ListResponse<CartItemDto> getAllCartItems(int pageNo, int pageSize, String sortBy, String sortDir);
    void deleteCartItem(long id);
}
