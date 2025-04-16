package com.springboot.blog.springboot_blog_rest_api.controller;

import com.springboot.blog.springboot_blog_rest_api.payload.MediaDto;
import com.springboot.blog.springboot_blog_rest_api.service.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping
    public ResponseEntity<List<MediaDto>> getAllMedia() {
        return new ResponseEntity<>(mediaService.getAllMedia(), HttpStatus.OK);
    }

    @GetMapping("/total")
    public ResponseEntity<Integer> getTotalMedia() {
        return new ResponseEntity<>(mediaService.getTotalMedia(), HttpStatus.OK);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<MediaDto> getMediaById(@PathVariable(name = "id") long postId) {
        return new ResponseEntity<>(mediaService.getMediaByPostId(postId), HttpStatus.OK);
    }

}
