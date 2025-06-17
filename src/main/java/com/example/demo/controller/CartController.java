package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.config.JwtUtils;
import com.example.demo.entity.CartItem;
import com.example.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Log4j2
public class CartController {

  private final CartService cartService;
  private final JwtUtils jwtUtils;

  /**
   * 获取当前用户的购物车
   */
  @GetMapping
  public Result<List<CartItem>> getCart(HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      List<CartItem> cartItems = cartService.getCartByUsername(username);
      log.info("获取购物车成功: username={}, 商品数量={}", username, cartItems.size());
      return Result.success(cartItems);
    } catch (Exception e) {
      log.error("获取购物车失败: {}", e.getMessage(), e);
      return Result.error("获取购物车失败: " + e.getMessage());
    }
  }

  /**
   * 添加商品到购物车
   */
  @PostMapping("/add")
  public Result<String> addToCart(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
    try {
      String username = getUsernameFromToken(httpRequest);
      Long productId = Long.valueOf(request.get("productId").toString());
      Integer quantity = Integer.valueOf(request.get("quantity").toString());

      cartService.addToCart(username, productId, quantity);
      log.info("添加商品到购物车成功: username={}, productId={}, quantity={}", username, productId, quantity);
      return Result.success("添加到购物车成功");
    } catch (Exception e) {
      log.error("添加商品到购物车失败: {}", e.getMessage(), e);
      return Result.error("添加到购物车失败: " + e.getMessage());
    }
  }

  /**
   * 更新购物车商品数量
   */
  @PutMapping("/update")
  public Result<String> updateCartItem(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
    try {
      String username = getUsernameFromToken(httpRequest);
      Long productId = Long.valueOf(request.get("productId").toString());
      Integer quantity = Integer.valueOf(request.get("quantity").toString());

      cartService.updateCartItem(username, productId, quantity);
      log.info("更新购物车商品数量成功: username={}, productId={}, quantity={}", username, productId, quantity);
      return Result.success("更新成功");
    } catch (Exception e) {
      log.error("更新购物车商品数量失败: {}", e.getMessage(), e);
      return Result.error("更新失败: " + e.getMessage());
    }
  }

  /**
   * 从购物车移除商品
   */
  @DeleteMapping("/remove/{productId}")
  public Result<String> removeFromCart(@PathVariable Long productId, HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      cartService.removeFromCart(username, productId);
      log.info("从购物车移除商品成功: username={}, productId={}", username, productId);
      return Result.success("移除成功");
    } catch (Exception e) {
      log.error("从购物车移除商品失败: {}", e.getMessage(), e);
      return Result.error("移除失败: " + e.getMessage());
    }
  }

  /**
   * 清空购物车
   */
  @DeleteMapping("/clear")
  public Result<String> clearCart(HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      cartService.clearCart(username);
      log.info("清空购物车成功: username={}", username);
      return Result.success("清空购物车成功");
    } catch (Exception e) {
      log.error("清空购物车失败: {}", e.getMessage(), e);
      return Result.error("清空购物车失败: " + e.getMessage());
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