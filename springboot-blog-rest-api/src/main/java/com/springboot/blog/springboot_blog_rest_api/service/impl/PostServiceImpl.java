package com.springboot.blog.springboot_blog_rest_api.service.impl;

import com.springboot.blog.springboot_blog_rest_api.entity.Category;
import com.springboot.blog.springboot_blog_rest_api.entity.Media;
import com.springboot.blog.springboot_blog_rest_api.entity.Post;
import com.springboot.blog.springboot_blog_rest_api.exception.ResourceNotFoundException;
import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.MediaDto;
import com.springboot.blog.springboot_blog_rest_api.payload.PostDto;
import com.springboot.blog.springboot_blog_rest_api.repository.CategoryRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.MediaRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.PostRepository;
import com.springboot.blog.springboot_blog_rest_api.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final MediaRepository mediaRepository;
    private ModelMapper mapper;
    private CategoryRepository categoryRepository;

    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper, CategoryRepository categoryRepository, MediaRepository mediaRepository) {
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.categoryRepository = categoryRepository;
        this.mediaRepository = mediaRepository;
    }

    private PostDto mapToDTO(Post post) {
        return mapper.map(post, PostDto.class);
    }

    private Post mapToEntity(PostDto postDTO) {
        return mapper.map(postDTO, Post.class);
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        Category category = categoryRepository.findById(postDto.getCategoryId()).orElseThrow(()
                -> new ResourceNotFoundException("Category", " id", String.valueOf(postDto.getCategoryId())));

        // convert DTO to entity
        Post post = mapToEntity(postDto);
        post.setCategory(category);

        // ánh xạ MediaDTO tới MediaEntity
        if (postDto.getMedia() != null) {
            MediaDto mediaDto = postDto.getMedia();
            Media media = new Media();
            media.setName(mediaDto.getName());
            media.setType(mediaDto.getType());
            media.setFilePath(mediaDto.getFilePath());
            media.setPost(post);
            post.setMedia(media);
        }

        Post newPost = postRepository.save(post);

        return mapToDTO(newPost);
    }

    @Override
    public ListResponse<PostDto> getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        // create pageable instance
        PageRequest pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> posts = postRepository.findAll(pageable);

        // get content for page object
        List<Post> listOfPosts = posts.getContent();

        List<PostDto> content = listOfPosts.stream().map(this::mapToDTO).collect(Collectors.toList());

        ListResponse<PostDto> postResponse = new ListResponse<>();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements((int) posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));
        return mapToDTO(post);
    }

    @Override
    public PostDto updatePost(PostDto postDTO, Long id) {
        Post post = postRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));

        Category category = categoryRepository.findById(postDTO.getCategoryId()).orElseThrow(()
                -> new ResourceNotFoundException("Category", "id", String.valueOf(postDTO.getCategoryId())));

        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        post.setContent(postDTO.getContent());
        post.setCategory(category);

        if (postDTO.getMedia() != null) {
            MediaDto mediaDto = postDTO.getMedia();

            Media media = mediaRepository.findByPostId(post.getId()).orElse(new Media());

            media.setName(mediaDto.getName());
            media.setType(mediaDto.getType());
            media.setFilePath(mediaDto.getFilePath());
            media.setPost(post);

            mediaRepository.save(media);
        }

        postRepository.save(post);

        return mapToDTO(post);
    }


    @Override
    public void deletePostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));
        postRepository.delete(post);
    }

    @Override
    public int getTotalPosts() {
        return (int) postRepository.count();
    }

    @Override
    public ListResponse<PostDto> getPostsByCategoryId(Long categoryId, int pageNo, int pageSize, String sortBy, String sortDir) {
        categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("Category", "id", String.valueOf(categoryId))
        );

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        PageRequest pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> posts = postRepository.findByCategoryId(categoryId, pageable);

        List<PostDto> content = posts.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        ListResponse<PostDto> postResponse = new ListResponse<>();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements((int) posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }

    @Override
    public ListResponse<PostDto> searchPostsByTitle(String title, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        PageRequest pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> posts = postRepository.searchByTitle(title, pageable);

        List<PostDto> content = posts.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        ListResponse<PostDto> postResponse = new ListResponse<>();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements((int) posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }
}
