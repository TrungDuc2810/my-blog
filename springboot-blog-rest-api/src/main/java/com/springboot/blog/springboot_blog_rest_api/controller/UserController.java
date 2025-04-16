package com.springboot.blog.springboot_blog_rest_api.controller;

import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.UserDto;
import com.springboot.blog.springboot_blog_rest_api.service.UserService;
import com.springboot.blog.springboot_blog_rest_api.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/total")
    public ResponseEntity<Integer> getTotalUsers() {
        return new ResponseEntity<>(userService.getTotalUsers(), HttpStatus.OK);
    }

    @GetMapping()
    public ListResponse<UserDto> getAllUsers(
            @RequestParam(value="pageNo", defaultValue=AppConstants.DEFAULT_PAGE_NUMBER, required=false) int pageNo,
            @RequestParam(value="pageSize", defaultValue=AppConstants.DEFAULT_PAGE_SIZE, required=false) int pageSize,
            @RequestParam(value="sortBy", defaultValue=AppConstants.DEFAULT_SORT_BY, required=false) String sortBy,
            @RequestParam(value="sortDir", defaultValue=AppConstants.DEFAULT_SORT_DIRECTION, required=false) String sortDir
    ) {
        return userService.getAllUsers(pageNo, pageSize, sortBy, sortDir);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable(name="id") long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>("User entity delete successfully!!!", HttpStatus.OK);
    }

}
