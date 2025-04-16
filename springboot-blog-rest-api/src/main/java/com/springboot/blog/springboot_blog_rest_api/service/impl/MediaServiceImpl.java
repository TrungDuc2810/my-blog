package com.springboot.blog.springboot_blog_rest_api.service.impl;

import com.springboot.blog.springboot_blog_rest_api.entity.Media;
import com.springboot.blog.springboot_blog_rest_api.entity.Post;
import com.springboot.blog.springboot_blog_rest_api.exception.ResourceNotFoundException;
import com.springboot.blog.springboot_blog_rest_api.payload.MediaDto;
import com.springboot.blog.springboot_blog_rest_api.repository.MediaRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.PostRepository;
import com.springboot.blog.springboot_blog_rest_api.service.MediaService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaServiceImpl implements MediaService {
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository, PostRepository postRepository, ModelMapper modelMapper) {
        this.mediaRepository = mediaRepository;
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Cacheable(value = "mediaCache", key = "'allMedia'", unless = "#result == null or #result.isEmpty()")
    public List<MediaDto> getAllMedia() {
        List<Media> mediaList = mediaRepository.findAll();
        return mediaList.stream()
                .map(media -> new MediaDto(media.getId(), media.getName(), media.getType(), media.getFilePath()))
                .collect(Collectors.toList());
    }

    @Override
    public int getTotalMedia() {
        return (int)mediaRepository.count();
    }

    @Override
    public MediaDto getMediaByPostId(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new ResourceNotFoundException("Post", "id", String.valueOf(postId)));
        Media media = mediaRepository.findByPostId(postId).orElseThrow(()
                -> new ResourceNotFoundException("Media", "id", String.valueOf(postId)));
        return modelMapper.map(media, MediaDto.class);
    }
}
