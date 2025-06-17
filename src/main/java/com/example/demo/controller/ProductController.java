package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  /**
   * 获取商品列表
   */
  @GetMapping
  public Result<Page<Product>> getProducts(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String category) {
    try {
      Page<Product> products = productService.getProducts(page, size, search, category);
      return Result.success(products);
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  /**
   * 获取商品详情
   */
  @GetMapping("/{id}")
  public Result<Product> getProductDetail(@PathVariable Long id) {
    try {
      Product product = productService.getProductDetail(id);
      if (product != null) {
        return Result.success(product);
      }
      return Result.error("商品不存在");
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }
}