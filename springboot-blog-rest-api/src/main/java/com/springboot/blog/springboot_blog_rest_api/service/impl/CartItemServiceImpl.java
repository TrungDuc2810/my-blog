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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<CartItemDto> addBulkCartItems(List<CartItemDto> cartItemDtos) {
        return cartItemDtos.stream()
                .map(dto -> {
                    CartItem cartItem = mapper.mapToDto(dto, CartItem.class);

                    Product product = productRepository.findById(dto.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", String.valueOf(dto.getProductId())));

                    User user = userRepository.findById(dto.getUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", String.valueOf(dto.getUserId())));

                    // Check if item already exists for this user and product
                    Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(dto.getUserId(), dto.getProductId());

                    if (existingItem.isPresent()) {
                        // Update quantity of existing item
                        CartItem existing = existingItem.get();
                        existing.setQuantity(existing.getQuantity() + dto.getQuantity());
                        return mapper.mapToDto(cartItemRepository.save(existing), CartItemDto.class);
                    } else {
                        // Save new item
                        cartItem.setProduct(product);
                        cartItem.setUser(user);
                        return mapper.mapToDto(cartItemRepository.save(cartItem), CartItemDto.class);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CartItemDto updateCartItem(long id, CartItemDto cartItemDto) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Cart item", "id", String.valueOf(id)));

        cartItem.setQuantity(cartItemDto.getQuantity());

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
    public ListResponse<CartItemDto> getCartItemsByUserId(long userId, int pageNo, int pageSize, String sortBy, String sortDir) {
        PageRequest pageRequest = PaginationUtils.createPageRequest(pageNo, pageSize, sortBy, sortDir);
        Page<CartItem> cartItemPage = cartItemRepository.findByUserId(userId, pageRequest);

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
