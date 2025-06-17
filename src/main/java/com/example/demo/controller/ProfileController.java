package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.config.JwtUtils;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.Order;
import com.example.demo.entity.Reservation;
import com.example.demo.service.CartItemService;
import com.example.demo.service.FavoriteService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 个人资料控制器
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Log4j2
public class ProfileController {

  private final FavoriteService favoriteService;
  private final CartItemService cartItemService;
  private final OrderService orderService;
  private final UserService userService;
  private final JwtUtils jwtUtils;

  /**
   * 获取用户收藏列表
   */
  @GetMapping("/favorites")
  public Result<List<Favorite>> getUserFavorites(HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      List<Favorite> favorites = favoriteService.getUserFavorites(username);
      log.info("获取用户收藏列表成功: username={}, 收藏数量={}", username, favorites.size());
      return Result.success(favorites);
    } catch (Exception e) {
      log.error("获取用户收藏列表失败: {}", e.getMessage(), e);
      return Result.error("获取收藏列表失败: " + e.getMessage());
    }
  }

  /**
   * 添加收藏
   */
  @PostMapping("/favorites")
  public Result<String> addFavorite(@RequestBody Map<String, Object> request) {
    try {
      Long userId = Long.valueOf(request.get("userId").toString());
      Long postId = Long.valueOf(request.get("postId").toString());
      String postType = (String) request.get("postType");

      favoriteService.addFavorite(userId, postId, postType);
      return Result.success("收藏成功");
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 取消收藏
   */
  @DeleteMapping("/favorites")
  public Result<String> removeFavorite(@RequestParam Long userId,
      @RequestParam Long postId,
      @RequestParam String postType) {
    try {
      favoriteService.removeFavorite(userId, postId, postType);
      return Result.success("取消收藏成功");
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 获取用户订单列表
   */
  @GetMapping("/orders")
  public Result<List<Order>> getUserOrders(HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      List<Order> orders = orderService.getUserOrders(username);
      log.info("获取用户订单列表成功: username={}, 订单数量={}", username, orders.size());
      return Result.success(orders);
    } catch (Exception e) {
      log.error("获取用户订单列表失败: {}", e.getMessage(), e);
      return Result.error("获取订单列表失败: " + e.getMessage());
    }
  }

  /**
   * 获取用户发布的论坛帖子
   */
  @GetMapping("/posts")
  public Result<List<Map<String, Object>>> getUserPosts(HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      List<Map<String, Object>> posts = userService.getUserPosts(username);
      log.info("获取用户帖子列表成功: username={}, 帖子数量={}", username, posts.size());
      return Result.success(posts);
    } catch (Exception e) {
      log.error("获取用户帖子列表失败: {}", e.getMessage(), e);
      return Result.error("获取帖子列表失败: " + e.getMessage());
    }
  }

  /**
   * 获取用户预约列表
   */
  @GetMapping("/reservations")
  public Result<List<Map<String, Object>>> getUserReservations(HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      List<Map<String, Object>> reservations = userService.getUserReservations(username);
      log.info("获取用户预约列表成功: username={}, 预约数量={}", username, reservations.size());
      return Result.success(reservations);
    } catch (Exception e) {
      log.error("获取用户预约列表失败: {}", e.getMessage(), e);
      return Result.error("获取预约列表失败: " + e.getMessage());
    }
  }

  /**
   * 获取用户购物车
   */
  @GetMapping("/cart")
  public Result<List<CartItem>> getUserCart(@RequestParam Long userId) {
    try {
      List<CartItem> cartItems = cartItemService.getUserCart(userId);
      return Result.success(cartItems);
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 添加商品到购物车
   */
  @PostMapping("/cart")
  public Result<String> addToCart(@RequestBody Map<String, Object> request) {
    try {
      Long userId = Long.valueOf(request.get("userId").toString());
      Long productId = Long.valueOf(request.get("productId").toString());
      Integer quantity = Integer.valueOf(request.get("quantity").toString());

      cartItemService.addToCart(userId, productId, quantity);
      return Result.success("添加到购物车成功");
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 更新购物车商品数量
   */
  @PutMapping("/cart/{id}")
  public Result<String> updateCartItemQuantity(@PathVariable Long id, @RequestBody Map<String, Object> request) {
    try {
      Long userId = Long.valueOf(request.get("userId").toString());
      Integer quantity = Integer.valueOf(request.get("quantity").toString());

      cartItemService.updateCartItemQuantity(userId, id, quantity);
      return Result.success("更新成功");
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 删除购物车商品
   */
  @DeleteMapping("/cart/{id}")
  public Result<String> removeFromCart(@PathVariable Long id, @RequestParam Long userId) {
    try {
      cartItemService.removeFromCart(userId, id);
      return Result.success("删除成功");
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 清空购物车
   */
  @DeleteMapping("/cart")
  public Result<String> clearCart(@RequestParam Long userId) {
    try {
      cartItemService.clearCart(userId);
      return Result.success("清空购物车成功");
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 充值余额
   */
  @PostMapping("/recharge")
  public Result<String> recharge(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
    try {
      String username = getUsernameFromToken(httpRequest);
      BigDecimal amount = new BigDecimal(request.get("amount").toString());
      String paymentMethod = (String) request.get("paymentMethod");

      userService.recharge(username, amount, paymentMethod);
      log.info("用户充值成功: username={}, amount={}, paymentMethod={}", username, amount, paymentMethod);
      return Result.success("充值成功");
    } catch (Exception e) {
      log.error("充值失败: {}", e.getMessage(), e);
      return Result.error("充值失败: " + e.getMessage());
    }
  }

  /**
   * 获取充值记录
   */
  @GetMapping("/recharge-history")
  public Result<List<Map<String, Object>>> getRechargeHistory(HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      List<Map<String, Object>> history = userService.getRechargeHistory(username);
      log.info("获取充值记录成功: username={}, 记录数量={}", username, history.size());
      return Result.success(history);
    } catch (Exception e) {
      log.error("获取充值记录失败: {}", e.getMessage(), e);
      return Result.error("获取充值记录失败: " + e.getMessage());
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