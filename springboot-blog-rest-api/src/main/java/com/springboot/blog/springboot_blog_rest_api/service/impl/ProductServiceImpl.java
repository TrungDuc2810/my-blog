package com.springboot.blog.springboot_blog_rest_api.service.impl;

import com.springboot.blog.springboot_blog_rest_api.entity.Product;
import com.springboot.blog.springboot_blog_rest_api.entity.ProductThumbnail;
import com.springboot.blog.springboot_blog_rest_api.exception.ResourceNotFoundException;
import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.ProductDto;
import com.springboot.blog.springboot_blog_rest_api.payload.ProductThumbnailDto;
import com.springboot.blog.springboot_blog_rest_api.repository.ProductRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.ProductThumbnailRepository;
import com.springboot.blog.springboot_blog_rest_api.service.ProductService;
import com.springboot.blog.springboot_blog_rest_api.utils.PaginationUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final ProductThumbnailRepository productThumbnailRepository;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper, ProductThumbnailRepository productThumbnailRepository) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.productThumbnailRepository = productThumbnailRepository;
    }

    private ProductDto mapToDto(Product product) {
        return modelMapper.map(product, ProductDto.class);
    }

    private Product mapToEntity(ProductDto productDto) {
        return modelMapper.map(productDto, Product.class);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true)
    })
    public ProductDto createProduct(ProductDto productDto) {
        Product product = mapToEntity(productDto);

        if (productDto.getProductThumbnail() != null) {
            ProductThumbnailDto thumbnailDto = productDto.getProductThumbnail();
            ProductThumbnail thumbnail = new ProductThumbnail();

            thumbnail.setName(thumbnailDto.getName());
            thumbnail.setType(thumbnailDto.getType());
            thumbnail.setFilePath(thumbnailDto.getFilePath());

            thumbnail.setProduct(product);
            product.setProductThumbnail(thumbnail);
        }

        Product savedProduct = productRepository.save(product);
        return mapToDto(savedProduct);
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = "products", key = "#id")
            },
            evict = {
                    @CacheEvict(value = "products", allEntries = true)
            }
    )
    public ProductDto updateProduct(long id, ProductDto productDto) {
        Product product = productRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Product", "id", String.valueOf(id)));

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setDiscountPrice(productDto.getDiscountPrice());
        product.setLanguage(productDto.getLanguage());

        if (productDto.getProductThumbnail() != null) {
            ProductThumbnailDto productThumbnailDto = productDto.getProductThumbnail();

            ProductThumbnail productThumbnail = productThumbnailRepository.findByProductId(product.getId()).orElseThrow(()
                    -> new ResourceNotFoundException("Product thumbnail", "id", String.valueOf(product.getId())));

            productThumbnail.setName(productThumbnailDto.getName());
            productThumbnail.setType(productThumbnailDto.getType());
            productThumbnail.setFilePath(productThumbnailDto.getFilePath());
            productThumbnail.setProduct(product);

            productThumbnailRepository.save(productThumbnail);
        }

        productRepository.save(product);

        return mapToDto(product);
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public ProductDto getProduct(long id) {
        Product product = productRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Product", "id", String.valueOf(id)));

        return mapToDto(product);
    }

    @Override
    @Cacheable(value = "products", key = "'page_'+#pageNo + '_size_'+#pageSize + '_sortBy_'+#sortBy + '_sortDir_'+#sortDir")
    public ListResponse<ProductDto> getProducts(int pageNo, int pageSize, String sortBy, String sortDir) {
        PageRequest pageRequest = PaginationUtils.createPageRequest(pageNo, pageSize, sortBy, sortDir);
        Page<Product> products = productRepository.findAll(pageRequest);

        return PaginationUtils.toListResponse(products, this::mapToDto);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products", allEntries = true)
    })
    public void deleteProduct(long id) {
        Product product = productRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Product", "id", String.valueOf(id)));

        productRepository.delete(product);
    }
}
