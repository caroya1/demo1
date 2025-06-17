package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 购物车Mapper接口
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

  @Select("SELECT ci.*, p.name as productName, p.price as productPrice, p.image_url as productImageUrl, " +
      "p.stock as productStock " +
      "FROM cart_items ci " +
      "LEFT JOIN products p ON ci.product_id = p.id " +
      "WHERE ci.user_id = #{userId} " +
      "ORDER BY ci.create_time DESC")
  List<CartItem> selectCartItemsWithProduct(@Param("userId") Long userId);
}