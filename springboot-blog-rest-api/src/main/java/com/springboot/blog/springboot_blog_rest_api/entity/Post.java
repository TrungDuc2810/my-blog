package com.springboot.blog.springboot_blog_rest_api.entity;

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
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    @Column(name="title", nullable=false)
    private String title;
    @Column(name="description", nullable=false)
    private String description;
    @Column(name="content", columnDefinition = "LONGTEXT", nullable=false)
    private String content;
    @Column(name="postedAt")
    private String postedAt;
    @Column(name="lastUpdated")
    private String lastUpdated;
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Media media;
    @OneToMany(mappedBy="post", cascade=CascadeType.ALL, orphanRemoval=true)
    private Set<Comment> comments = new HashSet<>();
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="category_id")
    private Category category;
    @PrePersist
    public void prePersist() {
        this.postedAt = LocalDateTime.now().toString();  // Set the created timestamp
        this.lastUpdated = LocalDateTime.now().toString();  // Set the updated timestamp
    }
    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = LocalDateTime.now().toString();  // Set the updated timestamp
    }
}
