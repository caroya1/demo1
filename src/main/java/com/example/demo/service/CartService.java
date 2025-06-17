package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.mapper.CartItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车服务类
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CartService extends ServiceImpl<CartItemMapper, CartItem> {

  private final UserService userService;
  private final ProductService productService;

  /**
   * 根据用户名获取购物车
   */
  public List<CartItem> getCartByUsername(String username) {
    log.debug("获取用户购物车: username={}", username);
    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    // 获取购物车商品，关联商品信息
    return baseMapper.selectCartItemsWithProduct(user.getId());
  }

  /**
   * 添加商品到购物车
   */
  @Transactional
  public void addToCart(String username, Long productId, Integer quantity) {
    log.debug("添加商品到购物车: username={}, productId={}, quantity={}", username, productId, quantity);

    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    Product product = productService.getById(productId);
    if (product == null) {
      throw new RuntimeException("商品不存在");
    }

    if (product.getStock() < quantity) {
      throw new RuntimeException("库存不足");
    }

    // 检查购物车中是否已有该商品
    QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId()).eq("product_id", productId);
    CartItem existingItem = getOne(queryWrapper);

    if (existingItem != null) {
      // 如果已存在，更新数量
      existingItem.setQuantity(existingItem.getQuantity() + quantity);
      updateById(existingItem);
      log.debug("更新购物车商品数量: cartItemId={}, newQuantity={}", existingItem.getId(), existingItem.getQuantity());
    } else {
      // 如果不存在，创建新的购物车项
      CartItem cartItem = new CartItem();
      cartItem.setUserId(user.getId());
      cartItem.setProductId(productId);
      cartItem.setQuantity(quantity);
      save(cartItem);
      log.debug("新增购物车商品: userId={}, productId={}, quantity={}", user.getId(), productId, quantity);
    }
  }

  /**
   * 更新购物车商品数量
   */
  @Transactional
  public void updateCartItem(String username, Long productId, Integer quantity) {
    log.debug("更新购物车商品数量: username={}, productId={}, quantity={}", username, productId, quantity);

    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    if (quantity <= 0) {
      throw new RuntimeException("数量必须大于0");
    }

    QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId()).eq("product_id", productId);
    CartItem cartItem = getOne(queryWrapper);

    if (cartItem == null) {
      throw new RuntimeException("购物车中没有该商品");
    }

    // 检查库存
    Product product = productService.getById(productId);
    if (product != null && product.getStock() < quantity) {
      throw new RuntimeException("库存不足");
    }

    cartItem.setQuantity(quantity);
    updateById(cartItem);
    log.debug("购物车商品数量更新成功: cartItemId={}, newQuantity={}", cartItem.getId(), quantity);
  }

  /**
   * 从购物车移除商品
   */
  @Transactional
  public void removeFromCart(String username, Long productId) {
    log.debug("从购物车移除商品: username={}, productId={}", username, productId);

    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId()).eq("product_id", productId);
    CartItem cartItem = getOne(queryWrapper);

    if (cartItem == null) {
      throw new RuntimeException("购物车中没有该商品");
    }

    removeById(cartItem.getId());
    log.debug("从购物车移除商品成功: cartItemId={}", cartItem.getId());
  }

  /**
   * 清空购物车
   */
  @Transactional
  public void clearCart(String username) {
    log.debug("清空购物车: username={}", username);

    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId());
    remove(queryWrapper);
    log.debug("清空购物车成功: userId={}", user.getId());
  }
}