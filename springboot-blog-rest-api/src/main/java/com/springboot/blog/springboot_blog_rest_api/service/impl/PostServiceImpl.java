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
import com.springboot.blog.springboot_blog_rest_api.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final MediaRepository mediaRepository;
    private ModelMapper mapper;
    private CategoryRepository categoryRepository;
    private AIService AIService;

    public PostServiceImpl(PostRepository postRepository,
                           ModelMapper mapper,
                           CategoryRepository categoryRepository,
                           MediaRepository mediaRepository,
                           AIService AIService) {
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.categoryRepository = categoryRepository;
        this.mediaRepository = mediaRepository;
        this.AIService = AIService;
    }

    private PostDto mapToDTO(Post post) {
        return mapper.map(post, PostDto.class);
    }

    private Post mapToEntity(PostDto postDTO) {
        return mapper.map(postDTO, Post.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "postsByCategory", allEntries = true),
            @CacheEvict(value = "postsByTitle", allEntries = true)
    })
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
    @Cacheable(value = "posts", key = "'page_'+#pageNo + '_size_'+#pageSize + '_sortBy_'+#sortBy + '_sortDir_'+#sortDir")
    public ListResponse<PostDto> getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        System.out.println("Getting all posts with: " + "pageNo=" + pageNo + " pageSize=" + pageSize + " sortBy=" + sortBy + " sortDir=" + sortDir);

        PageRequest pageRequest = PaginationUtils.createPageRequest(pageNo, pageSize, sortBy, sortDir);

        Page<Post> posts = postRepository.findAll(pageRequest);

        return PaginationUtils.toListResponse(posts, this::mapToDTO);
    }

    @Override
    @Cacheable(value = "posts", key = "#id")
    public PostDto getPostById(Long id) {
        System.out.println("Getting post by id: " + id);

        Post post = postRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));

        return mapToDTO(post);
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = "posts", key = "#id")
            },
            evict = {
                    @CacheEvict(value = "posts", allEntries = true),
                    @CacheEvict(value = "postsByCategory", allEntries = true),
                    @CacheEvict(value = "postsByTitle", allEntries = true)
            }
    )
    public PostDto updatePost(PostDto postDTO, Long id) {
        System.out.println("Updating post by id: " + id);

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
    @Caching(evict = {
            @CacheEvict(value = "posts", key = "#id"),
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "postsByCategory", allEntries = true),
            @CacheEvict(value = "postsByTitle", allEntries = true)
    })
    public void deletePostById(Long id) {
        System.out.println("Deleting post by id: " + id);

        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));
        postRepository.delete(post);
    }

    @Override
    public int getTotalPosts() {
        return (int) postRepository.count();
    }

    @Override
    @Cacheable(value = "postsByCategory", key = "'category_'+#categoryId + '_page_'+#pageNo + '_size_'+#pageSize + '_sortBy_'+#sortBy + '_sortDir_'+#sortDir")
    public ListResponse<PostDto> getPostsByCategoryId(Long categoryId, int pageNo, int pageSize, String sortBy, String sortDir) {
        categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("Category", "id", String.valueOf(categoryId))
        );

        PageRequest pageRequest = PaginationUtils.createPageRequest(pageNo, pageSize, sortBy, sortDir);

        Page<Post> posts = postRepository.findByCategoryId(categoryId, pageRequest);

        return PaginationUtils.toListResponse(posts, this::mapToDTO);
    }

    @Override
    @Cacheable(value = "postsByTitle", key = "'title_'+#title + '_page_'+#pageNo + '_size_'+#pageSize + '_sortBy_'+#sortBy + '_sortDir_'+#sortDir")
    public ListResponse<PostDto> searchPostsByTitle(String title, int pageNo, int pageSize, String sortBy, String sortDir) {
        PageRequest pageRequest = PaginationUtils.createPageRequest(pageNo, pageSize, sortBy, sortDir);

        Page<Post> posts = postRepository.searchByTitle(title, pageRequest);

        return PaginationUtils.toListResponse(posts, this::mapToDTO);
    }

    @Override
    public String generateDescription(String content) {
        String prompt = """
                From this title: %s, write a paragraph with at least 50 characters to start the article with the above title..
                """.formatted(content);

        String genDescription = AIService.chat(prompt);
        String trimDescription = genDescription.replaceFirst("(?i)^.*?(?=\\n\\n|\\n)", "").trim();

        return trimDescription;
    }
}
