package com.springboot.blog.springboot_blog_rest_api.entity;

import com.springboot.blog.springboot_blog_rest_api.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    private double price;
    private double discountPrice;
    private String language;
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProductThumbnail productThumbnail;
}
