package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Product;
import com.example.demo.mapper.ProductMapper;
import org.springframework.stereotype.Service;

/**
 * 商品服务类
 */
@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {

  /**
   * 分页获取商品列表
   */
  public Page<Product> getProducts(int page, int size, String search, String category) {
    Page<Product> pageObj = new Page<>(page, size);
    QueryWrapper<Product> queryWrapper = new QueryWrapper<>();

    if (search != null && !search.trim().isEmpty()) {
      queryWrapper.like("name", search).or().like("description", search);
    }

    if (category != null && !category.trim().isEmpty()) {
      queryWrapper.eq("category", category);
    }

    queryWrapper.orderByDesc("create_time");

    return page(pageObj, queryWrapper);
  }

  /**
   * 获取商品详情
   */
  public Product getProductDetail(Long id) {
    return getById(id);
  }
}