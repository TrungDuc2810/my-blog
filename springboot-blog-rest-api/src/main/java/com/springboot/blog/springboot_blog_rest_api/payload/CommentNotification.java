package com.springboot.blog.springboot_blog_rest_api.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentNotification {
    private String type; // Loại thông báo: CREATE, UPDATE, DELETE
    private Long postId; // ID của bài viết
    private CommentDto comment; // Thông tin comment
}