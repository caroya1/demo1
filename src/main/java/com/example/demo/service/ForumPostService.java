package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.ForumPost;
import com.example.demo.mapper.ForumPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 论坛帖子服务类
 */
@Service
@RequiredArgsConstructor
public class ForumPostService extends ServiceImpl<ForumPostMapper, ForumPost> {

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
}