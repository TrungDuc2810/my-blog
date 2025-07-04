package com.springboot.blog.springboot_blog_rest_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="comments")
public class Comment implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) private long id;
    private String name;
    private String email;
    private String body;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="post_id", nullable=false)
    private Post post;
}
