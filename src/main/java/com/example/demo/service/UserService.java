package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.config.JwtUtils;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务类 - 重新设计token处理逻辑
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UserService extends ServiceImpl<UserMapper, User> {

  private final JwtUtils jwtUtils;

  /**
   * 用户注册 - 重新设计
   */
  public Map<String, Object> register(String username, String password, String nickname, String email) {
    log.info("开始用户注册流程: username={}, email={}", username, email);

    // 检查用户名是否已存在
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    if (count(queryWrapper) > 0) {
      log.warn("用户注册失败 - 用户名已存在: username={}", username);
      throw new RuntimeException("用户名已存在");
    }

    // 创建新用户
    User user = new User();
    user.setUsername(username);
    user.setPassword(password); // 明文密码
    user.setNickname(nickname);
    user.setEmail(email);
    user.setUserType("user");

    save(user);
    log.info("用户注册成功: username={}, userId={}", username, user.getId());

    // 生成token
    String token = jwtUtils.generateToken(username);

    // 构建返回数据
    Map<String, Object> result = new HashMap<>();
    result.put("token", token);
    result.put("userId", user.getId());
    result.put("username", user.getUsername());
    result.put("nickname", user.getNickname() != null ? user.getNickname() : user.getUsername());
    result.put("email", user.getEmail());
    result.put("userType", user.getUserType());

    log.debug("为注册用户生成token: username={}", username);
    return result;
  }

  /**
   * 验证用户登录 - 新增方法
   */
  public User validateUser(String username, String password) {
    log.debug("验证用户登录: username={}", username);

    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    User user = getOne(queryWrapper);

    if (user == null) {
      log.debug("用户不存在: username={}", username);
      return null;
    }

    // 直接比较明文密码
    if (!password.equals(user.getPassword())) {
      log.debug("密码错误: username={}", username);
      return null;
    }

    log.debug("用户验证成功: username={}, userId={}", username, user.getId());
    return user;
  }

  /**
   * 根据用户名获取用户信息
   */
  public User getUserByUsername(String username) {
    log.debug("查询用户信息: username={}", username);
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    User user = getOne(queryWrapper);

    if (user != null) {
      log.debug("成功找到用户: username={}, userId={}", username, user.getId());
    } else {
      log.debug("未找到用户: username={}", username);
    }

    return user;
  }

  /**
   * 更新用户信息
   */
  public void updateUserInfo(Long userId, String nickname, String email, String phone, String gender) {
    log.info("开始更新用户信息: userId={}, email={}", userId, email);

    User user = getById(userId);
    if (user == null) {
      log.warn("更新用户信息失败 - 用户不存在: userId={}", userId);
      throw new RuntimeException("用户不存在");
    }

    user.setNickname(nickname);
    user.setEmail(email);
    user.setPhone(phone);
    user.setGender(gender);

    updateById(user);
    log.info("用户信息更新成功: userId={}, username={}", userId, user.getUsername());
  }
}