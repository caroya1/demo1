package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.config.JwtUtils;
import com.example.demo.entity.LearningActivity;
import com.example.demo.service.LearningActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 学习活动控制器
 */
@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
@Log4j2
public class LearningController {

  private final LearningActivityService learningActivityService;
  private final JwtUtils jwtUtils;

  /**
   * 获取学习活动列表
   */
  @GetMapping("/activities")
  public Result<Page<LearningActivity>> getLearningActivities(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String search) {
    try {
      Page<LearningActivity> activities = learningActivityService.getLearningActivities(page, size, search);
      return Result.success(activities);
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 获取活动详情
   */
  @GetMapping("/activities/{id}")
  public Result<LearningActivity> getActivityDetail(@PathVariable Long id) {
    try {
      LearningActivity activity = learningActivityService.getActivityDetail(id);
      if (activity != null) {
        return Result.success(activity);
      }
      return Result.error("活动不存在");
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 预约活动 - 更新为支持token认证
   */
  @PostMapping("/activities/{id}/reserve")
  public Result<String> reserveActivity(@PathVariable Long id, HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      learningActivityService.reserveActivity(username, id);
      log.info("预约活动成功: username={}, activityId={}", username, id);
      return Result.success("预约成功");
    } catch (Exception e) {
      log.error("预约活动失败: {}", e.getMessage(), e);
      return Result.error("预约失败: " + e.getMessage());
    }
  }

  /**
   * 取消预约 - 更新为支持token认证
   */
  @DeleteMapping("/activities/{id}/reserve")
  public Result<String> cancelReservation(@PathVariable Long id, HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      learningActivityService.cancelReservation(username, id);
      log.info("取消预约成功: username={}, activityId={}", username, id);
      return Result.success("取消预约成功");
    } catch (Exception e) {
      log.error("取消预约失败: {}", e.getMessage(), e);
      return Result.error("取消预约失败: " + e.getMessage());
    }
  }

  /**
   * 添加收藏活动
   */
  @PostMapping("/activities/{id}/favorite")
  public Result<String> addFavorite(@PathVariable Long id, HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      learningActivityService.addFavorite(username, id);
      log.info("添加活动收藏成功: username={}, activityId={}", username, id);
      return Result.success("添加收藏成功");
    } catch (Exception e) {
      log.error("添加活动收藏失败: {}", e.getMessage(), e);
      return Result.error("添加收藏失败: " + e.getMessage());
    }
  }

  /**
   * 移除收藏活动
   */
  @DeleteMapping("/activities/{id}/favorite")
  public Result<String> removeFavorite(@PathVariable Long id, HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      learningActivityService.removeFavorite(username, id);
      log.info("移除活动收藏成功: username={}, activityId={}", username, id);
      return Result.success("移除收藏成功");
    } catch (Exception e) {
      log.error("移除活动收藏失败: {}", e.getMessage(), e);
      return Result.error("移除收藏失败: " + e.getMessage());
    }
  }

  /**
   * 检查活动是否已收藏
   */
  @GetMapping("/activities/{id}/favorite/check")
  public Result<Map<String, Boolean>> checkFavorite(@PathVariable Long id, HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      boolean isFavorite = learningActivityService.isFavorite(username, id);
      log.debug("检查活动收藏状态: username={}, activityId={}, isFavorite={}", username, id, isFavorite);
      return Result.success(Map.of("isFavorite", isFavorite));
    } catch (Exception e) {
      log.error("检查活动收藏状态失败: {}", e.getMessage(), e);
      return Result.error("检查收藏状态失败: " + e.getMessage());
    }
  }

  /**
   * 检查是否已预约
   */
  @GetMapping("/activities/{id}/reserve/check")
  public Result<Map<String, Boolean>> checkReservation(@PathVariable Long id, HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      boolean isReserved = learningActivityService.isReserved(username, id);
      log.debug("检查预约状态: username={}, activityId={}, isReserved={}", username, id, isReserved);
      return Result.success(Map.of("isReserved", isReserved));
    } catch (Exception e) {
      log.error("检查预约状态失败: {}", e.getMessage(), e);
      return Result.error("检查预约状态失败: " + e.getMessage());
    }
  }

  /**
   * 获取用户预约列表
   */
  @GetMapping("/reservations")
  public Result<List<Map<String, Object>>> getUserReservations(HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      List<Map<String, Object>> reservations = learningActivityService.getUserReservations(username);
      log.info("获取用户预约列表成功: username={}, 预约数量={}", username, reservations.size());
      return Result.success(reservations);
    } catch (Exception e) {
      log.error("获取用户预约列表失败: {}", e.getMessage(), e);
      return Result.error("获取预约列表失败: " + e.getMessage());
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