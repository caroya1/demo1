package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.Reservation;
import com.example.demo.mapper.FavoriteMapper;
import com.example.demo.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收藏和预约服务类
 */
@Service
@RequiredArgsConstructor
public class FavoriteService extends ServiceImpl<FavoriteMapper, Favorite> {

  private final ReservationMapper reservationMapper;

  /**
   * 获取用户收藏列表
   */
  public List<Favorite> getUserFavorites(Long userId) {
    return baseMapper.selectUserFavoritesWithDetails(userId);
  }

  /**
   * 添加收藏
   */
  public void addFavorite(Long userId, Long postId, String postType) {
    // 检查是否已收藏
    QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", userId)
        .eq("post_id", postId)
        .eq("post_type", postType);

    if (count(queryWrapper) > 0) {
      throw new RuntimeException("已收藏过此内容");
    }

    Favorite favorite = new Favorite();
    favorite.setUserId(userId);
    favorite.setPostId(postId);
    favorite.setPostType(postType);
    save(favorite);
  }

  /**
   * 取消收藏
   */
  public void removeFavorite(Long userId, Long postId, String postType) {
    QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", userId)
        .eq("post_id", postId)
        .eq("post_type", postType);

    remove(queryWrapper);
  }

  /**
   * 获取用户预约列表
   */
  public List<Reservation> getUserReservations(Long userId) {
    return reservationMapper.selectUserReservationsWithDetails(userId);
  }
}