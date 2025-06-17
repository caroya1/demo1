package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.*;
import com.example.demo.mapper.OrderItemMapper;
import com.example.demo.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 订单服务类
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class OrderService extends ServiceImpl<OrderMapper, Order> {

  private final OrderItemMapper orderItemMapper;
  private final UserService userService;
  private final CartService cartService;
  private final ProductService productService;

  /**
   * 从购物车结算创建订单
   */
  @Transactional
  public Order checkoutFromCart(String username, Map<String, String> orderInfo) {
    log.info("开始购物车结算: username={}", username);

    // 获取用户信息
    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    // 获取购物车商品
    List<CartItem> cartItems = cartService.getCartByUsername(username);
    if (cartItems.isEmpty()) {
      throw new RuntimeException("购物车为空");
    }

    // 计算订单总金额
    BigDecimal totalAmount = BigDecimal.ZERO;
    for (CartItem item : cartItems) {
      if (item.getProductPrice() != null && item.getQuantity() != null) {
        BigDecimal itemTotal = item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        totalAmount = totalAmount.add(itemTotal);
      }
    }

    // 检查用户余额
    if (user.getBalance() == null || user.getBalance().compareTo(totalAmount) < 0) {
      throw new RuntimeException("余额不足，当前余额：" +
          (user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO) +
          "，需要支付：" + totalAmount);
    }

    // 检查商品库存
    for (CartItem item : cartItems) {
      Product product = productService.getById(item.getProductId());
      if (product == null) {
        throw new RuntimeException("商品不存在：" + item.getProductName());
      }
      if (product.getStock() < item.getQuantity()) {
        throw new RuntimeException("商品库存不足：" + product.getName() +
            "，当前库存：" + product.getStock() + "，需要：" + item.getQuantity());
      }
    }

    // 生成订单号
    String orderNumber = generateOrderNumber();

    // 创建订单
    Order order = new Order();
    order.setOrderNumber(orderNumber);
    order.setUserId(user.getId());
    order.setTotalAmount(totalAmount);
    order.setStatus("paid"); // 余额支付直接标记为已支付
    order.setPaymentMethod("balance");
    order.setShippingAddress(orderInfo.get("shippingAddress"));
    order.setRemark(orderInfo.get("remark"));

    save(order);
    log.info("订单创建成功: orderNumber={}, totalAmount={}", orderNumber, totalAmount);

    // 创建订单项
    for (CartItem item : cartItems) {
      OrderItem orderItem = new OrderItem();
      orderItem.setOrderId(order.getId());
      orderItem.setProductId(item.getProductId());
      orderItem.setProductName(item.getProductName());
      orderItem.setProductPrice(item.getProductPrice());
      orderItem.setProductImageUrl(item.getProductImageUrl());
      orderItem.setQuantity(item.getQuantity());
      orderItem.setSubtotal(item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

      orderItemMapper.insert(orderItem);
    }

    // 扣除用户余额
    BigDecimal newBalance = user.getBalance().subtract(totalAmount);
    user.setBalance(newBalance);
    userService.updateById(user);
    log.info("扣除用户余额: username={}, 扣除金额={}, 剩余余额={}", username, totalAmount, newBalance);

    // 减少商品库存
    for (CartItem item : cartItems) {
      Product product = productService.getById(item.getProductId());
      product.setStock(product.getStock() - item.getQuantity());
      productService.updateById(product);
      log.debug("减少商品库存: productId={}, 减少数量={}, 剩余库存={}",
          product.getId(), item.getQuantity(), product.getStock());
    }

    // 清空购物车
    cartService.clearCart(username);
    log.info("清空购物车: username={}", username);

    return order;
  }

  /**
   * 获取用户订单列表
   */
  public List<Order> getUserOrders(String username) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }
    return baseMapper.selectOrdersByUserId(user.getId());
  }

  /**
   * 获取用户订单列表（分页）
   */
  public Page<Order> getUserOrdersWithPage(String username, int page, int size) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }
    Page<Order> pageObj = new Page<>(page, size);
    return baseMapper.selectOrdersByUserIdWithPage(pageObj, user.getId());
  }

  /**
   * 获取订单详情（包含订单项）
   */
  public Order getOrderDetail(Long orderId, String username) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    Order order = getById(orderId);
    if (order == null || !order.getUserId().equals(user.getId())) {
      throw new RuntimeException("订单不存在或无权限访问");
    }

    // 获取订单项
    List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
    // 这里可以将订单项设置到订单对象中，需要在Order实体中添加对应字段

    return order;
  }

  /**
   * 取消订单
   */
  @Transactional
  public void cancelOrder(Long orderId, String username) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }

    Order order = getById(orderId);
    if (order == null || !order.getUserId().equals(user.getId())) {
      throw new RuntimeException("订单不存在或无权限访问");
    }

    if (!"pending".equals(order.getStatus()) && !"paid".equals(order.getStatus())) {
      throw new RuntimeException("订单状态不允许取消");
    }

    // 如果已支付，退还余额
    if ("paid".equals(order.getStatus())) {
      BigDecimal newBalance = user.getBalance().add(order.getTotalAmount());
      user.setBalance(newBalance);
      userService.updateById(user);
      log.info("退还用户余额: username={}, 退还金额={}, 新余额={}",
          username, order.getTotalAmount(), newBalance);

      // 恢复商品库存
      List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
      for (OrderItem item : orderItems) {
        Product product = productService.getById(item.getProductId());
        if (product != null) {
          product.setStock(product.getStock() + item.getQuantity());
          productService.updateById(product);
          log.debug("恢复商品库存: productId={}, 恢复数量={}, 新库存={}",
              product.getId(), item.getQuantity(), product.getStock());
        }
      }
    }

    // 更新订单状态
    order.setStatus("cancelled");
    updateById(order);
    log.info("订单取消成功: orderNumber={}", order.getOrderNumber());
  }

  /**
   * 生成订单号
   */
  private String generateOrderNumber() {
    LocalDateTime now = LocalDateTime.now();
    String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    String randomSuffix = String.valueOf((int) (Math.random() * 1000));
    return "ORD" + timestamp + String.format("%03d", Integer.parseInt(randomSuffix));
  }
}