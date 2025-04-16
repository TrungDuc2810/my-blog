package com.springboot.blog.springboot_blog_rest_api.repository;

import com.springboot.blog.springboot_blog_rest_api.entity.ProductThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductThumbnailRepository extends JpaRepository<ProductThumbnail, Long> {
    Optional<ProductThumbnail> findByProductId(long productId);
}
