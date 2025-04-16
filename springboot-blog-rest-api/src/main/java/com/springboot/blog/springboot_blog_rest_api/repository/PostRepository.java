package com.springboot.blog.springboot_blog_rest_api.repository;

import com.springboot.blog.springboot_blog_rest_api.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);
    @Query(value = "select * from posts p where lower(p.title) like lower(concat('%', ?1, '%'))", nativeQuery = true)
    Page<Post> searchByTitle(String title, Pageable pageable);
}
