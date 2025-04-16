package com.springboot.blog.springboot_blog_rest_api.service;

import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.ProductDto;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(long id, ProductDto productDto);
    ProductDto getProduct(long id);
    ListResponse<ProductDto> getProducts(int pageNo, int pageSize, String sortBy, String sortDir);
    void deleteProduct(long id);
}
