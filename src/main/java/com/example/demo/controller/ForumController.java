package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.config.JwtUtils;
import com.example.demo.entity.ForumPost;
import com.example.demo.service.ForumPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 论坛控制器
 */
@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
@Log4j2
public class ForumController {

  private final ForumPostService forumPostService;
  private final JwtUtils jwtUtils;

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
   * 创建新帖子 - 修复支持用户名
   */
  @PostMapping("/posts")
  public Result<ForumPost> createPost(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
    try {
      String title = (String) request.get("title");
      String content = (String) request.get("content");
      String imageUrl = (String) request.get("imageUrl");

      // 从token获取用户名，而不是依赖前端传递的authorId
      String username = getUsernameFromToken(httpRequest);

      log.info("开始创建帖子: title={}, username={}", title, username);

      ForumPost post = forumPostService.createPost(title, content, imageUrl, username);
      log.info("帖子创建成功: postId={}, title={}", post.getId(), post.getTitle());

      return Result.success("发布成功", post);
    } catch (Exception e) {
      log.error("创建帖子失败: {}", e.getMessage(), e);
      return Result.error("发布失败: " + e.getMessage());
    }
  }

  /**
   * 添加收藏帖子
   */
  @PostMapping("/posts/{id}/favorite")
  public Result<String> addFavorite(@PathVariable Long id, HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      forumPostService.addFavorite(username, id);
      log.info("添加帖子收藏成功: username={}, postId={}", username, id);
      return Result.success("添加收藏成功");
    } catch (Exception e) {
      log.error("添加帖子收藏失败: {}", e.getMessage(), e);
      return Result.error("添加收藏失败: " + e.getMessage());
    }
  }

  /**
   * 移除收藏帖子
   */
  @DeleteMapping("/posts/{id}/favorite")
  public Result<String> removeFavorite(@PathVariable Long id, HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      forumPostService.removeFavorite(username, id);
      log.info("移除帖子收藏成功: username={}, postId={}", username, id);
      return Result.success("移除收藏成功");
    } catch (Exception e) {
      log.error("移除帖子收藏失败: {}", e.getMessage(), e);
      return Result.error("移除收藏失败: " + e.getMessage());
    }
  }

  /**
   * 检查帖子是否已收藏
   */
  @GetMapping("/posts/{id}/favorite/check")
  public Result<Map<String, Boolean>> checkFavorite(@PathVariable Long id, HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      boolean isFavorite = forumPostService.isFavorite(username, id);
      log.debug("检查帖子收藏状态: username={}, postId={}, isFavorite={}", username, id, isFavorite);
      return Result.success(Map.of("isFavorite", isFavorite));
    } catch (Exception e) {
      log.error("检查帖子收藏状态失败: {}", e.getMessage(), e);
      return Result.error("检查收藏状态失败: " + e.getMessage());
    }
  }

  /**
   * 更新帖子浏览量
   */
  @PostMapping("/posts/{id}/view")
  public Result<String> updateViews(@PathVariable Long id) {
    try {
      forumPostService.updateViews(id);
      return Result.success("浏览量更新成功");
    } catch (Exception e) {
      log.error("更新浏览量失败: {}", e.getMessage(), e);
      return Result.error("更新浏览量失败");
    }
  }

  /**
   * 从token中获取用户名
   */
  private String getUsernameFromToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new RuntimeException("未提供有效的token");
    }
    String token = authHeader.substring(7).trim();
    return jwtUtils.getUsernameFromToken(token);
  }
}