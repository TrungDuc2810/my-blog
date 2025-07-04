package com.springboot.blog.springboot_blog_rest_api.service.impl;

import com.springboot.blog.springboot_blog_rest_api.entity.Post;
import com.springboot.blog.springboot_blog_rest_api.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisCountViewsService {
    private static final String POST_VIEW_KEY = "post:views";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private PostRepository postRepository;

    public void incrementPostView(long postId) {
        redisTemplate.opsForValue().increment(POST_VIEW_KEY + ":" + postId, 1);
    }

    public long getPostViews(long postId) {
        Object views = redisTemplate.opsForValue().get(POST_VIEW_KEY + ":" + postId);
        return views == null ? 0L : Long.parseLong(views.toString());
    }

    @Scheduled(fixedRate = 300000)
    public void syncPostViewsToDatabase() {
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            Object views = redisTemplate.opsForValue().get(POST_VIEW_KEY + ":" + post.getId());
            if (views != null) {
                post.setViews(Long.parseLong(views.toString()));
                postRepository.save(post);
            }
        }
    }
}
