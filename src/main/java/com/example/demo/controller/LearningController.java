package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.LearningActivity;
import com.example.demo.service.LearningActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学习活动控制器
 */
@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
public class LearningController {

  private final LearningActivityService learningActivityService;

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
   * 预约活动
   */
  @PostMapping("/activities/{id}/reserve")
  public Result<String> reserveActivity(@PathVariable Long id, @RequestBody Map<String, Object> request) {
    try {
      Long userId = Long.valueOf(request.get("userId").toString());
      learningActivityService.reserveActivity(userId, id);
      return Result.success("预约成功");
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 取消预约
   */
  @DeleteMapping("/activities/{id}/reserve")
  public Result<String> cancelReservation(@PathVariable Long id, @RequestParam Long userId) {
    try {
      learningActivityService.cancelReservation(userId, id);
      return Result.success("取消预约成功");
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }
}