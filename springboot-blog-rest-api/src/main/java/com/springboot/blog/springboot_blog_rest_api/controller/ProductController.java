package com.springboot.blog.springboot_blog_rest_api.controller;

import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.ProductDto;
import com.springboot.blog.springboot_blog_rest_api.payload.ProductThumbnailDto;
import com.springboot.blog.springboot_blog_rest_api.service.ProductService;
import com.springboot.blog.springboot_blog_rest_api.utils.AppConstants;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestPart("product") ProductDto productDto,
                                                    @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
        try {
            if (thumbnail != null && !thumbnail.isEmpty()) {
                ProductThumbnailDto productThumbnailDto = uploadMedia(thumbnail);
                productDto.setProductThumbnail(productThumbnailDto);
            }
            return new ResponseEntity<>(productService.createProduct(productDto), HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ProductThumbnailDto uploadMedia(MultipartFile file) throws IOException {
        Set<ProductThumbnailDto> productThumbnailDtos = new HashSet<>();

        String uploadDir = "D://OneDrive//blog-frontend//my-blog//public//productThumbnails//"; // temporary
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        String fileName = file.getOriginalFilename();
        String filePath = uploadDir + fileName;

        File destinationFile = new File(filePath);
        file.transferTo(destinationFile); // save file into server

        ProductThumbnailDto productThumbnailDto = new ProductThumbnailDto();
        productThumbnailDto.setName(fileName);

        String mimeType = Files.probeContentType(Path.of(filePath));
        if (mimeType != null) {
            productThumbnailDto.setType(mimeType);
        } else {
            productThumbnailDto.setType(file.getContentType());
        }
        productThumbnailDto.setFilePath("/productThumbnails/" + fileName); // temporary

        return productThumbnailDto;
    }

    @GetMapping
    public ListResponse<ProductDto> getProducts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return productService.getProducts(pageNo, pageSize, sortBy, sortDir);
    }

    // Get post by id
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable(name = "id") long id){
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(name = "id") long id) {
        productService.deleteProduct(id);

        return new ResponseEntity<>("Delete successfully!!!", HttpStatus.OK);
    }
}
