package com.springboot.blog.springboot_blog_rest_api.service;

import com.springboot.blog.springboot_blog_rest_api.payload.ProductThumbnailDto;

public interface ProductThumbnailService {
    ProductThumbnailDto getByProductId(long productId);
}
