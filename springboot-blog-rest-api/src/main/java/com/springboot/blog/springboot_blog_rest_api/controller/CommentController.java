package com.springboot.blog.springboot_blog_rest_api.controller;

import com.springboot.blog.springboot_blog_rest_api.payload.CommentDto;
import com.springboot.blog.springboot_blog_rest_api.payload.CommentNotification;
import com.springboot.blog.springboot_blog_rest_api.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class CommentController {
    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable(value = "postId") long postId,
                                                    @Valid @RequestBody CommentDto commentDto){
        return new ResponseEntity<>(commentService.createComment(postId, commentDto), HttpStatus.CREATED);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> getCommentsByPostId(@PathVariable(value = "postId") Long postId){
        return commentService.getCommentsByPostId(postId);
    }

    @GetMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable(value = "postId") Long postId,
                                                     @PathVariable(value = "id") Long commentId){
        CommentDto commentDto = commentService.getCommentById(postId, commentId);
        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }

    @PutMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable(value = "postId") Long postId,
                                                    @PathVariable(value = "id") Long commentId,
                                                    @Valid @RequestBody CommentDto commentDto){
        CommentDto updatedComment = commentService.updateComment(postId, commentId, commentDto);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable(value = "postId") Long postId,
                                                @PathVariable(value = "id") Long commentId){
        commentService.deleteComment(postId, commentId);
        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
    }

    @MessageMapping("/posts/{postId}/comments/add")
    @SendTo("/topic/posts/{postId}/comments")
    public CommentNotification handleAddComment(@DestinationVariable Long postId, CommentDto commentDto) {
        CommentDto savedComment = commentService.createComment(postId, commentDto);
        return new CommentNotification("CREATE", postId, savedComment );
    }

    @MessageMapping("/posts/{postId}/comments/{commentId}/update")
    @SendTo("/topic/posts/{postId}/comments")
    public CommentNotification handleCommentUpdate(@DestinationVariable Long postId,
                                                   @DestinationVariable Long commentId,
                                                   CommentDto commentDto) {
        CommentDto updatedComment = commentService.updateComment(postId, commentId, commentDto);
        return new CommentNotification("UPDATE", postId, updatedComment);
    }

    @MessageMapping("/posts/{postId}/comments/{commentId}/delete")
    @SendTo("/topic/posts/{postId}/comments")
    public CommentNotification handleCommentDelete(@DestinationVariable Long postId,
                                                   @DestinationVariable Long commentId) {
        CommentDto deletedComment = commentService.getCommentById(postId, commentId);
        commentService.deleteComment(postId, commentId);
        return new CommentNotification("DELETE", postId, deletedComment);
    }

}