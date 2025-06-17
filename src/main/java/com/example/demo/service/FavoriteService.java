package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.User;
import com.example.demo.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收藏服务类
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class FavoriteService extends ServiceImpl<FavoriteMapper, Favorite> {

  private final UserService userService;

  /**
   * 获取用户收藏列表（带详细信息）
   */
  public List<Favorite> getUserFavorites(String username) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    // 使用Mapper的自定义方法获取带详细信息的收藏列表
    return baseMapper.selectUserFavoritesWithDetails(user.getId());
  }

  /**
   * 添加收藏
   */
  @Transactional
  public void addFavorite(String username, Long postId, String postType) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }
    addFavorite(user.getId(), postId, postType);
  }

  /**
   * 添加收藏（通过用户ID）
   */
  @Transactional
  public void addFavorite(Long userId, Long postId, String postType) {
    log.debug("添加收藏: userId={}, postId={}, postType={}", userId, postId, postType);

    // 检查是否已收藏
    QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", userId)
        .eq("post_id", postId)
        .eq("post_type", postType);

    if (count(queryWrapper) > 0) {
      throw new RuntimeException("已收藏过此内容");
    }

    // 创建收藏记录
    Favorite favorite = new Favorite();
    favorite.setUserId(userId);
    favorite.setPostId(postId);
    favorite.setPostType(postType);

    save(favorite);
    log.debug("收藏添加成功: favoriteId={}", favorite.getId());
  }

  /**
   * 移除收藏
   */
  @Transactional
  public void removeFavorite(String username, Long postId, String postType) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }
    removeFavorite(user.getId(), postId, postType);
  }

  /**
   * 移除收藏（通过用户ID）
   */
  @Transactional
  public void removeFavorite(Long userId, Long postId, String postType) {
    log.debug("移除收藏: userId={}, postId={}, postType={}", userId, postId, postType);

    QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", userId)
        .eq("post_id", postId)
        .eq("post_type", postType);

    boolean removed = remove(queryWrapper);
    if (!removed) {
      throw new RuntimeException("未找到收藏记录");
    }

    log.debug("收藏移除成功: userId={}, postId={}, postType={}", userId, postId, postType);
  }

  /**
   * 检查是否已收藏
   */
  public boolean isFavorite(String username, Long postId, String postType) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      return false;
    }

    QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId())
        .eq("post_id", postId)
        .eq("post_type", postType);

    return count(queryWrapper) > 0;
  }
}