package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.config.JwtUtils;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

  private final OrderService orderService;
  private final JwtUtils jwtUtils;

  /**
   * 购物车结算下单
   */
  @PostMapping("/checkout")
  public Result<Order> checkout(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
    try {
      String username = getUsernameFromToken(httpRequest);
      Order order = orderService.checkoutFromCart(username, request);
      log.info("购物车结算成功: username={}, orderNumber={}", username, order.getOrderNumber());
      return Result.success("下单成功", order);
    } catch (Exception e) {
      log.error("购物车结算失败: {}", e.getMessage(), e);
      return Result.error(e.getMessage());
    }
  }

  /**
   * 获取用户订单列表
   */
  @GetMapping
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
   * 获取订单详情
   */
  @GetMapping("/{orderId}")
  public Result<Order> getOrderDetail(@PathVariable Long orderId, HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      Order order = orderService.getOrderDetail(orderId, username);
      log.info("获取订单详情成功: username={}, orderId={}", username, orderId);
      return Result.success(order);
    } catch (Exception e) {
      log.error("获取订单详情失败: {}", e.getMessage(), e);
      return Result.error("获取订单详情失败: " + e.getMessage());
    }
  }

  /**
   * 取消订单
   */
  @PutMapping("/{orderId}/cancel")
  public Result<String> cancelOrder(@PathVariable Long orderId, HttpServletRequest request) {
    try {
      String username = getUsernameFromToken(request);
      orderService.cancelOrder(orderId, username);
      log.info("取消订单成功: username={}, orderId={}", username, orderId);
      return Result.success("订单取消成功");
    } catch (Exception e) {
      log.error("取消订单失败: {}", e.getMessage(), e);
      return Result.error("取消订单失败: " + e.getMessage());
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