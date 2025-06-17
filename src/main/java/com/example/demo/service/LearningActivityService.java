package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.LearningActivity;
import com.example.demo.entity.Reservation;
import com.example.demo.mapper.LearningActivityMapper;
import com.example.demo.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学习活动服务类
 */
@Service
@RequiredArgsConstructor
public class LearningActivityService extends ServiceImpl<LearningActivityMapper, LearningActivity> {

  private final ReservationMapper reservationMapper;

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
}