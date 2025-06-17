package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.ForumPost;
import com.example.demo.service.ForumPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 论坛控制器
 */
@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
public class ForumController {

  private final ForumPostService forumPostService;

  /**
   * 获取论坛帖子列表
   */
  @GetMapping("/posts")
  public Result<Page<ForumPost>> getForumPosts(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String search) {
    try {
      Page<ForumPost> posts = forumPostService.getForumPosts(page, size, search);
      return Result.success(posts);
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 获取帖子详情
   */
  @GetMapping("/posts/{id}")
  public Result<ForumPost> getPostDetail(@PathVariable Long id) {
    try {
      ForumPost post = forumPostService.getPostDetail(id);
      if (post != null) {
        return Result.success(post);
      }
      return Result.error("帖子不存在");
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 创建新帖子
   */
  @PostMapping("/posts")
  public Result<ForumPost> createPost(@RequestBody Map<String, Object> request) {
    try {
      String title = (String) request.get("title");
      String content = (String) request.get("content");
      String imageUrl = (String) request.get("imageUrl");
      Long authorId = Long.valueOf(request.get("authorId").toString());

      ForumPost post = forumPostService.createPost(title, content, imageUrl, authorId);
      return Result.success("发布成功", post);
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }
}