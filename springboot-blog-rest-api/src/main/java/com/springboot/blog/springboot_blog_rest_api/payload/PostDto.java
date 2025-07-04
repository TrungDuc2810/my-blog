package com.springboot.blog.springboot_blog_rest_api.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
public class PostDto implements Serializable {
    private long id;
    @NotEmpty
    @Size(min = 2, message = "Post title should have at least 2 characters")
    private String title;
    @NotEmpty
    @Size(min = 10, message = "Post description should have at least 10 characters")
    private String description;
    @NotEmpty
    private String content;
    private String postedAt;
    private String lastUpdated;
    private long views;
    private MediaDto media;
    private Set<CommentDto> comments;
    private Long categoryId;
}
