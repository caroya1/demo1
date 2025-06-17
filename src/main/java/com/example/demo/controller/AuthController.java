package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.config.JwtUtils;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器 - 重新设计token处理逻辑
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

  private final UserService userService;
  private final JwtUtils jwtUtils;

  /**
   * 用户注册
   */
  @PostMapping("/register")
  public Result<Map<String, Object>> register(@RequestBody Map<String, String> request) {
    try {
      String username = request.get("username");
      String password = request.get("password");
      String nickname = request.get("nickname");
      String email = request.get("email");

      log.info("用户注册请求: username={}, email={}", username, email);
      Map<String, Object> registerResult = userService.register(username, password, nickname, email);
      log.info("用户注册成功: username={}", username);
      return Result.success("注册成功", registerResult);
    } catch (Exception e) {
      log.error("用户注册失败: {}", e.getMessage(), e);
      return Result.error(e.getMessage());
    }
  }

  /**
   * 用户登录 - 重新设计
   */
  @PostMapping("/login")
  public Result<Map<String, Object>> login(@RequestBody Map<String, String> request) {
    try {
      String username = request.get("username");
      String password = request.get("password");

      log.info("用户登录请求: username={}", username);

      // 验证用户名密码
      User user = userService.validateUser(username, password);
      if (user == null) {
        log.warn("用户登录失败 - 用户名或密码错误: username={}", username);
        return Result.error("用户名或密码错误");
      }

      // 生成token
      String token = jwtUtils.generateToken(username);

      // 构建返回数据
      Map<String, Object> loginResult = new HashMap<>();
      loginResult.put("token", token);
      loginResult.put("userId", user.getId());
      loginResult.put("username", user.getUsername());
      loginResult.put("nickname", user.getNickname() != null ? user.getNickname() : user.getUsername());
      loginResult.put("email", user.getEmail());
      loginResult.put("userType", user.getUserType());

      log.info("用户登录成功: username={}, userId={}", username, user.getId());
      return Result.success("登录成功", loginResult);
    } catch (Exception e) {
      log.error("用户登录失败: username={}, error={}", request.get("username"), e.getMessage(), e);
      return Result.error("登录失败: " + e.getMessage());
    }
  }

  /**
   * 获取用户信息 - 重新设计token验证
   */
  @GetMapping("/userinfo")
  public Result<Map<String, Object>> getUserInfo(HttpServletRequest request) {
    try {
      // 获取Authorization header
      String authHeader = request.getHeader("Authorization");
      log.debug("收到getUserInfo请求, Authorization header: {}",
          authHeader != null ? authHeader.substring(0, Math.min(authHeader.length(), 30)) + "..." : "null");

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        log.warn("Authorization header格式错误或缺失");
        return Result.error("请提供有效的认证token");
      }

      // 提取token
      String token = authHeader.substring(7).trim();
      if (token.isEmpty()) {
        log.warn("token为空");
        return Result.error("token不能为空");
      }

      // 验证token
      if (!jwtUtils.validateToken(token)) {
        log.warn("token验证失败");
        return Result.error("token无效或已过期，请重新登录");
      }

      // 从token获取用户名
      String username = jwtUtils.getUsernameFromToken(token);
      log.debug("从token解析出用户名: {}", username);

      // 获取用户信息
      User user = userService.getUserByUsername(username);
      if (user == null) {
        log.warn("用户不存在: username={}", username);
        return Result.error("用户不存在");
      }

      // 构建返回数据
      Map<String, Object> userInfo = new HashMap<>();
      userInfo.put("userId", user.getId());
      userInfo.put("username", user.getUsername());
      userInfo.put("nickname", user.getNickname() != null ? user.getNickname() : user.getUsername());
      userInfo.put("email", user.getEmail());
      userInfo.put("phone", user.getPhone());
      userInfo.put("gender", user.getGender());
      userInfo.put("userType", user.getUserType());

      log.info("成功获取用户信息: username={}", username);
      return Result.success("获取用户信息成功", userInfo);
    } catch (Exception e) {
      log.error("获取用户信息失败: {}", e.getMessage(), e);
      return Result.error("获取用户信息失败: " + e.getMessage());
    }
  }

  /**
   * 更新用户信息
   */
  @PutMapping("/userinfo")
  public Result<String> updateUserInfo(@RequestBody Map<String, Object> request) {
    try {
      Long userId = Long.valueOf(request.get("userId").toString());
      String nickname = (String) request.get("nickname");
      String email = (String) request.get("email");
      String phone = (String) request.get("phone");
      String gender = (String) request.get("gender");

      log.info("更新用户信息请求: userId={}, email={}", userId, email);
      userService.updateUserInfo(userId, nickname, email, phone, gender);
      log.info("用户信息更新成功: userId={}", userId);
      return Result.success("更新成功");
    } catch (Exception e) {
      log.error("用户信息更新失败: {}", e.getMessage(), e);
      return Result.error(e.getMessage());
    }
  }

  /**
   * 退出登录
   */
  @PostMapping("/logout")
  public Result<String> logout(HttpServletRequest request) {
    try {
      String authHeader = request.getHeader("Authorization");
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7).trim();
        String username = jwtUtils.getUsernameFromToken(token);
        log.info("用户退出登录: username={}", username);
      }
      return Result.success("退出登录成功");
    } catch (Exception e) {
      log.error("退出登录失败: {}", e.getMessage(), e);
      return Result.success("退出登录成功"); // 即使有错误也返回成功
    }
  }
}