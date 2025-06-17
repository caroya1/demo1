package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.Reservation;
import com.example.demo.service.CartItemService;
import com.example.demo.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 个人中心控制器
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

  private final FavoriteService favoriteService;
  private final CartItemService cartItemService;

  /**
   * 获取用户收藏列表
   */
  @GetMapping("/favorites")
  public Result<List<Favorite>> getUserFavorites(@RequestParam Long userId) {
    try {
      List<Favorite> favorites = favoriteService.getUserFavorites(userId);
      return Result.success(favorites);
    } catch (Exception e) {
      return Result.error(e.getMessage());
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
   * 获取用户预约列表
   */
  @GetMapping("/reservations")
  public Result<List<Reservation>> getUserReservations(@RequestParam Long userId) {
    try {
      List<Reservation> reservations = favoriteService.getUserReservations(userId);
      return Result.success(reservations);
    } catch (Exception e) {
      return Result.error(e.getMessage());
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
}