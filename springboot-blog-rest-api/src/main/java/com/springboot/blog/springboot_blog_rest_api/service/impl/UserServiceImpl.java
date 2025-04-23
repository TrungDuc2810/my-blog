package com.springboot.blog.springboot_blog_rest_api.service.impl;

import com.springboot.blog.springboot_blog_rest_api.entity.User;
import com.springboot.blog.springboot_blog_rest_api.exception.ResourceNotFoundException;
import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.UserDto;
import com.springboot.blog.springboot_blog_rest_api.repository.UserRepository;
import com.springboot.blog.springboot_blog_rest_api.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private ModelMapper mapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    private UserDto mapToDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    private User mapToEntity(UserDto userDto) {
        return mapper.map(userDto, User.class);
    }

    @Override
    public ListResponse<UserDto> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);

        Page<User> users = userRepository.findAll(pageRequest);

        List<User> listOfUsers = users.getContent();

        List<UserDto> content = listOfUsers.stream().map(this::mapToDto).collect(Collectors.toList());

        ListResponse<UserDto> userResponse = new ListResponse<>();
        userResponse.setContent(content);
        userResponse.setPageNo(users.getNumber());
        userResponse.setPageSize(users.getSize());
        userResponse.setTotalElements((int)users.getTotalElements());
        userResponse.setTotalPages(users.getTotalPages());
        userResponse.setLast(users.isLast());

        return userResponse;
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new ResourceNotFoundException("User", "username", username));
        return mapToDto(user);
    }

    @Override
    public int getTotalUsers() {
        return (int)userRepository.count();
    }

    @Override
    public void deleteUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", String.valueOf(id)));
        userRepository.delete(user);
    }
}
