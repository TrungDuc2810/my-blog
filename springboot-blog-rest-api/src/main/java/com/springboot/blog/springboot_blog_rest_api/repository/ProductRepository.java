package com.springboot.blog.springboot_blog_rest_api.repository;

import com.springboot.blog.springboot_blog_rest_api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
