package com.springboot.blog.springboot_blog_rest_api.service.impl;

import com.springboot.blog.springboot_blog_rest_api.entity.CartItem;
import com.springboot.blog.springboot_blog_rest_api.entity.Product;
import com.springboot.blog.springboot_blog_rest_api.entity.User;
import com.springboot.blog.springboot_blog_rest_api.exception.ResourceNotFoundException;
import com.springboot.blog.springboot_blog_rest_api.payload.CartItemDto;
import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.repository.CartItemRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.ProductRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.UserRepository;
import com.springboot.blog.springboot_blog_rest_api.service.CartItemService;
import com.springboot.blog.springboot_blog_rest_api.utils.Mapper;
import com.springboot.blog.springboot_blog_rest_api.utils.PaginationUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    public CartItemServiceImpl(CartItemRepository cartItemRepository,
                               ProductRepository productRepository,
                               UserRepository userRepository,
                               Mapper mapper) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public CartItemDto addCartItem(CartItemDto cartItemDto) {
        CartItem cartItem = mapper.mapToEntity(cartItemDto, CartItem.class);

        Product product = productRepository.findById(cartItemDto.getProductId()).orElseThrow(()
                -> new ResourceNotFoundException("Product", "id", String.valueOf(cartItemDto.getProductId())));

        User user = userRepository.findById(cartItemDto.getUserId()).orElseThrow(()
                -> new ResourceNotFoundException("User", "id", String.valueOf(cartItemDto.getUserId())));

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        return mapper.mapToDto(savedCartItem, CartItemDto.class);
    }

    @Override
    @Transactional
    public CartItemDto updateCartItem(long id, CartItemDto cartItemDto) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Cart item", "id", String.valueOf(id)));

        Product product = productRepository.findById(cartItemDto.getProductId()).orElseThrow(()
                -> new ResourceNotFoundException("Product", "id", String.valueOf(cartItemDto.getProductId())));

        User user = userRepository.findById(cartItemDto.getUserId()).orElseThrow(()
                -> new ResourceNotFoundException("User", "id", String.valueOf(cartItemDto.getUserId())));

        cartItem.setQuantity(cartItemDto.getQuantity());
        cartItem.setProduct(product);

        return mapper.mapToDto(cartItem, CartItemDto.class);
    }

    @Override
    public CartItemDto getCartItem(long id) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Cart item", "id", String.valueOf(id)));

        return mapper.mapToDto(cartItem, CartItemDto.class);
    }

    @Override
    public ListResponse<CartItemDto> getAllCartItems(int pageNo, int pageSize, String sortBy, String sortDir) {
        PageRequest pageRequest = PaginationUtils.createPageRequest(pageNo, pageSize, sortBy, sortDir);
        Page<CartItem> cartItemPage = cartItemRepository.findAll(pageRequest);

        return PaginationUtils.toListResponse(cartItemPage,
                cartItem -> mapper.mapToDto(cartItem, CartItemDto.class));
    }

    @Override
    public void deleteCartItem(long id) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Cart item", "id", String.valueOf(id)));

        cartItemRepository.delete(cartItem);
    }
}
