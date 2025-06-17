package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.ForumPost;
import com.example.demo.entity.User;
import com.example.demo.mapper.FavoriteMapper;
import com.example.demo.mapper.ForumPostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 论坛帖子服务类
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ForumPostService extends ServiceImpl<ForumPostMapper, ForumPost> {

  private final FavoriteMapper favoriteMapper;
  private final UserService userService;

  /**
   * 分页获取论坛帖子列表
   */
  public Page<ForumPost> getForumPosts(int page, int size, String search) {
    Page<ForumPost> pageObj = new Page<>(page, size);
    String keyword = search == null ? "" : search;
    return baseMapper.selectForumPostsWithAuthor(pageObj, keyword);
  }

  /**
   * 获取帖子详情并增加浏览量
   */
  public ForumPost getPostDetail(Long id) {
    ForumPost post = getById(id);
    if (post != null) {
      // 增加浏览量
      post.setViews(post.getViews() + 1);
      updateById(post);

      // 获取作者信息
      Page<ForumPost> pageObj = new Page<>(1, 1);
      Page<ForumPost> result = baseMapper.selectForumPostsWithAuthor(pageObj, "");
      for (ForumPost p : result.getRecords()) {
        if (p.getId().equals(id)) {
          post.setAuthor(p.getAuthor());
          break;
        }
      }
    }
    return post;
  }

  /**
   * 创建新帖子
   */
  public ForumPost createPost(String title, String content, String imageUrl, Long authorId) {
    ForumPost post = new ForumPost();
    post.setTitle(title);
    post.setContent(content);
    post.setImageUrl(imageUrl);
    post.setAuthorId(authorId);
    post.setViews(0);

    save(post);
    return post;
  }

  /**
   * 添加帖子收藏
   */
  @Transactional
  public void addFavorite(String username, Long postId) {
    log.debug("添加帖子收藏: username={}, postId={}", username, postId);

    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    // 检查帖子是否存在
    ForumPost post = getById(postId);
    if (post == null) {
      throw new RuntimeException("帖子不存在");
    }

    // 检查是否已收藏
    QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId())
        .eq("post_id", postId)
        .eq("post_type", "forum");

    if (favoriteMapper.selectCount(queryWrapper) > 0) {
      throw new RuntimeException("已收藏过此帖子");
    }

    // 创建收藏记录
    Favorite favorite = new Favorite();
    favorite.setUserId(user.getId());
    favorite.setPostId(postId);
    favorite.setPostType("forum");

    favoriteMapper.insert(favorite);
    log.debug("帖子收藏添加成功: favoriteId={}", favorite.getId());
  }

  /**
   * 移除帖子收藏
   */
  @Transactional
  public void removeFavorite(String username, Long postId) {
    log.debug("移除帖子收藏: username={}, postId={}", username, postId);

    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId())
        .eq("post_id", postId)
        .eq("post_type", "forum");

    int deleted = favoriteMapper.delete(queryWrapper);
    if (deleted == 0) {
      throw new RuntimeException("未找到收藏记录");
    }

    log.debug("帖子收藏移除成功: userId={}, postId={}", user.getId(), postId);
  }

  /**
   * 检查帖子是否已收藏
   */
  public boolean isFavorite(String username, Long postId) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      return false;
    }

    QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId())
        .eq("post_id", postId)
        .eq("post_type", "forum");

    return favoriteMapper.selectCount(queryWrapper) > 0;
  }

  /**
   * 更新浏览量
   */
  public void updateViews(Long postId) {
    ForumPost post = getById(postId);
    if (post != null) {
      post.setViews(post.getViews() + 1);
      updateById(post);
      log.debug("帖子浏览量更新: postId={}, newViews={}", postId, post.getViews());
    }
  }
}