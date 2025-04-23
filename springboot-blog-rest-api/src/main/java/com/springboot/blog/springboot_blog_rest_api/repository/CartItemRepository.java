package com.springboot.blog.springboot_blog_rest_api.repository;

import com.springboot.blog.springboot_blog_rest_api.entity.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Page<CartItem> findByUserId(Long userId, Pageable pageable);
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
}
