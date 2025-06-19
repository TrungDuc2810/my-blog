package com.springboot.blog.springboot_blog_rest_api.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto implements Serializable {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String type;
    private String filePath;
}
