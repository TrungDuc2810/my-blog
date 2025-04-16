package com.springboot.blog.springboot_blog_rest_api.controller;

import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.MediaDto;
import com.springboot.blog.springboot_blog_rest_api.payload.PostDto;
import com.springboot.blog.springboot_blog_rest_api.service.PostService;
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
@RequestMapping("/api/posts")
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Create blog post rest api
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PostDto> createPost(@Valid @RequestPart("post") PostDto postDto,
                                              @RequestPart(value = "mediaFile", required = false) MultipartFile mediaFile) {
        try {
            if (mediaFile != null && !mediaFile.isEmpty()) {
                MediaDto mediaDto = uploadMedia(mediaFile);
                postDto.setMedia(mediaDto);
            }
            return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public MediaDto uploadMedia(MultipartFile file) throws IOException {
        Set<MediaDto> media = new HashSet<>();

        String uploadDir = "D://OneDrive//blog-frontend//my-blog//public//img//"; // temporary
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        String fileName = file.getOriginalFilename();
        String filePath = uploadDir + fileName;

        File destinationFile = new File(filePath);
        file.transferTo(destinationFile); // save file into server

        MediaDto mediaDto = new MediaDto();
        mediaDto.setName(fileName);

        String mimeType = Files.probeContentType(Path.of(filePath));
        if (mimeType != null) {
                mediaDto.setType(mimeType);
        } else {
            mediaDto.setType(file.getContentType());
        }
        mediaDto.setFilePath("/img/" + fileName); // temporary

        return mediaDto;
    }

    @GetMapping("/total")
    public ResponseEntity<Integer> getTotalPosts() {
        return new ResponseEntity<>(postService.getTotalPosts(), HttpStatus.OK);
    }

    // Get all posts rest api
    @GetMapping
    public ListResponse<PostDto> getAllPosts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);
    }

    // Get post by id
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable(name = "id") long id){
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // Get posts by category id
    @GetMapping("/category/{id}")
    public ResponseEntity<ListResponse<PostDto>> getPostsByCategoryId(
            @PathVariable(name="id") Long categoryId,
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(postService.getPostsByCategoryId(categoryId, pageNo, pageSize, sortBy, sortDir));
    }

    // Update post by id rest api
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PostDto> updatePost(@Valid @RequestPart("post") PostDto postDto,
                                              @PathVariable(name = "id") long id,
                                              @RequestPart(value = "mediaFile", required = false) MultipartFile mediaFile) {
        try {
            if (mediaFile != null && !mediaFile.isEmpty()) {
                MediaDto mediaDto = uploadMedia(mediaFile);
                postDto.setMedia(mediaDto);
            }
            PostDto updatedPost = postService.updatePost(postDto, id);
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete post rest api
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") long id){
        postService.deletePostById(id);
        return new ResponseEntity<>("Post entity deleted successfully.", HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ListResponse<PostDto>> searchPosts(
            @RequestParam String title,
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
            )
    {
        return ResponseEntity.ok(postService.searchPostsByTitle(title, pageNo, pageSize, sortBy, sortDir));
    }
}
