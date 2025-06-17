package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.mapper.CartItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车服务类
 */
@Service
@RequiredArgsConstructor
public class CartItemService extends ServiceImpl<CartItemMapper, CartItem> {

  private final ProductService productService;

  /**
   * 获取用户购物车
   */
  public List<CartItem> getUserCart(Long userId) {
    return baseMapper.selectCartItemsWithProduct(userId);
  }

  /**
   * 添加商品到购物车
   */
  @Transactional
  public void addToCart(Long userId, Long productId, Integer quantity) {
    Product product = productService.getById(productId);
    if (product == null) {
      throw new RuntimeException("商品不存在");
    }

    if (product.getStock() < quantity) {
      throw new RuntimeException("库存不足");
    }

    // 检查购物车中是否已有该商品
    QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", userId).eq("product_id", productId);
    CartItem existingItem = getOne(queryWrapper);

    if (existingItem != null) {
      // 更新数量
      existingItem.setQuantity(existingItem.getQuantity() + quantity);
      updateById(existingItem);
    } else {
      // 新增购物车项
      CartItem cartItem = new CartItem();
      cartItem.setUserId(userId);
      cartItem.setProductId(productId);
      cartItem.setQuantity(quantity);
      save(cartItem);
    }
  }

  /**
   * 更新购物车商品数量
   */
  public void updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity) {
    CartItem cartItem = getById(cartItemId);
    if (cartItem == null || !cartItem.getUserId().equals(userId)) {
      throw new RuntimeException("购物车项不存在");
    }

    Product product = productService.getById(cartItem.getProductId());
    if (product.getStock() < quantity) {
      throw new RuntimeException("库存不足");
    }

    cartItem.setQuantity(quantity);
    updateById(cartItem);
  }

  /**
   * 删除购物车商品
   */
  public void removeFromCart(Long userId, Long cartItemId) {
    CartItem cartItem = getById(cartItemId);
    if (cartItem == null || !cartItem.getUserId().equals(userId)) {
      throw new RuntimeException("购物车项不存在");
    }

    removeById(cartItemId);
  }

  /**
   * 清空购物车
   */
  public void clearCart(Long userId) {
    QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", userId);
    remove(queryWrapper);
  }
}