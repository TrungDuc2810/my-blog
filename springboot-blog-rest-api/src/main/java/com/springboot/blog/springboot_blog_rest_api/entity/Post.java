package com.springboot.blog.springboot_blog_rest_api.entity;

import com.springboot.blog.springboot_blog_rest_api.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin("*")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="posts", uniqueConstraints={@UniqueConstraint(columnNames={"title"})})
public class Post extends BaseEntity {
    @Column(name="title", nullable=false)
    private String title;
    @Column(name="description", columnDefinition = "TEXT", nullable=false)
    private String description;
    @Column(name="content", columnDefinition = "LONGTEXT", nullable=false)
    private String content;
    private long views;
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Media media;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<Comment> comments = new HashSet<>();
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="category_id")
    private Category category;
}
