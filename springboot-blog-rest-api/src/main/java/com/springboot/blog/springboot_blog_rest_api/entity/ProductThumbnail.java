package com.springboot.blog.springboot_blog_rest_api.entity;

import com.springboot.blog.springboot_blog_rest_api.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin("*")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="product_thumbnails")
public class ProductThumbnail extends BaseEntity {
    private String name;
    private String type;
    private String filePath;
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
