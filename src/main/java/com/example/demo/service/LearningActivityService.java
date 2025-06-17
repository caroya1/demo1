package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.LearningActivity;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.mapper.FavoriteMapper;
import com.example.demo.mapper.LearningActivityMapper;
import com.example.demo.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 学习活动服务类
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class LearningActivityService extends ServiceImpl<LearningActivityMapper, LearningActivity> {

  private final ReservationMapper reservationMapper;
  private final FavoriteMapper favoriteMapper;
  private final UserService userService;

  /**
   * 分页获取学习活动列表
   */
  public Page<LearningActivity> getLearningActivities(int page, int size, String search) {
    Page<LearningActivity> pageObj = new Page<>(page, size);
    String keyword = search == null ? "" : search;
    return baseMapper.selectLearningActivitiesWithAuthor(pageObj, keyword);
  }

  /**
   * 获取活动详情并增加浏览量
   */
  public LearningActivity getActivityDetail(Long id) {
    LearningActivity activity = getById(id);
    if (activity != null) {
      // 增加浏览量
      activity.setViews(activity.getViews() + 1);
      updateById(activity);
    }
    return activity;
  }

  /**
   * 预约活动
   */
  @Transactional
  public void reserveActivity(Long userId, Long activityId) {
    LearningActivity activity = getById(activityId);
    if (activity == null) {
      throw new RuntimeException("活动不存在");
    }

    if (!"active".equals(activity.getStatus())) {
      throw new RuntimeException("活动已关闭");
    }

    if (activity.getReservedCount() >= activity.getMaxCapacity()) {
      throw new RuntimeException("活动已满员");
    }

    // 检查是否已预约
    QueryWrapper<Reservation> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", userId).eq("activity_id", activityId);
    if (reservationMapper.selectCount(queryWrapper) > 0) {
      throw new RuntimeException("您已预约过此活动");
    }

    // 创建预约记录
    Reservation reservation = new Reservation();
    reservation.setUserId(userId);
    reservation.setActivityId(activityId);
    reservationMapper.insert(reservation);

    // 更新预约数量
    activity.setReservedCount(activity.getReservedCount() + 1);
    updateById(activity);
  }

  /**
   * 取消预约
   */
  @Transactional
  public void cancelReservation(Long userId, Long activityId) {
    QueryWrapper<Reservation> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", userId).eq("activity_id", activityId);
    Reservation reservation = reservationMapper.selectOne(queryWrapper);

    if (reservation == null) {
      throw new RuntimeException("您未预约此活动");
    }

    // 删除预约记录
    reservationMapper.deleteById(reservation.getId());

    // 更新预约数量
    LearningActivity activity = getById(activityId);
    if (activity != null) {
      activity.setReservedCount(Math.max(0, activity.getReservedCount() - 1));
      updateById(activity);
    }
  }

  /**
   * 添加活动收藏
   */
  @Transactional
  public void addFavorite(String username, Long activityId) {
    log.debug("添加活动收藏: username={}, activityId={}", username, activityId);

    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    // 检查活动是否存在
    LearningActivity activity = getById(activityId);
    if (activity == null) {
      throw new RuntimeException("活动不存在");
    }

    // 检查是否已收藏
    QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId())
        .eq("post_id", activityId)
        .eq("post_type", "learning");

    if (favoriteMapper.selectCount(queryWrapper) > 0) {
      throw new RuntimeException("已收藏过此活动");
    }

    // 创建收藏记录
    Favorite favorite = new Favorite();
    favorite.setUserId(user.getId());
    favorite.setPostId(activityId);
    favorite.setPostType("learning");

    favoriteMapper.insert(favorite);
    log.debug("活动收藏添加成功: favoriteId={}", favorite.getId());
  }

  /**
   * 移除活动收藏
   */
  @Transactional
  public void removeFavorite(String username, Long activityId) {
    log.debug("移除活动收藏: username={}, activityId={}", username, activityId);

    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId())
        .eq("post_id", activityId)
        .eq("post_type", "learning");

    int deleted = favoriteMapper.delete(queryWrapper);
    if (deleted == 0) {
      throw new RuntimeException("未找到收藏记录");
    }

    log.debug("活动收藏移除成功: userId={}, activityId={}", user.getId(), activityId);
  }

  /**
   * 检查活动是否已收藏
   */
  public boolean isFavorite(String username, Long activityId) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      return false;
    }

    QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId())
        .eq("post_id", activityId)
        .eq("post_type", "learning");

    return favoriteMapper.selectCount(queryWrapper) > 0;
  }

  /**
   * 预约活动 - 支持用户名
   */
  @Transactional
  public void reserveActivity(String username, Long activityId) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }
    reserveActivity(user.getId(), activityId);
  }

  /**
   * 取消预约 - 支持用户名
   */
  @Transactional
  public void cancelReservation(String username, Long activityId) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }
    cancelReservation(user.getId(), activityId);
  }

  /**
   * 检查是否已预约
   */
  public boolean isReserved(String username, Long activityId) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      return false;
    }

    QueryWrapper<Reservation> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId()).eq("activity_id", activityId);

    return reservationMapper.selectCount(queryWrapper) > 0;
  }

  /**
   * 获取用户的预约记录
   */
  public List<Map<String, Object>> getUserReservations(String username) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    return reservationMapper.selectUserReservationsWithDetails(user.getId());
  }
}