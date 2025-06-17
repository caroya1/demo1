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

  @Select("SELECT c.*, p.name, p.price, p.image_url, (c.quantity * p.price) as total_price " +
      "FROM cart_items c " +
      "LEFT JOIN products p ON c.product_id = p.id " +
      "WHERE c.user_id = #{userId} ORDER BY c.create_time DESC")
  List<CartItem> selectUserCartItemsWithDetails(@Param("userId") Long userId);
}