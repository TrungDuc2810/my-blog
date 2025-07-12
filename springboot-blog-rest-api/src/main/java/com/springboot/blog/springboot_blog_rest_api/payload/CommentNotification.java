package com.springboot.blog.springboot_blog_rest_api.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentNotification{
    private String type;
    private Long postId;
    private CommentDto comment;
}