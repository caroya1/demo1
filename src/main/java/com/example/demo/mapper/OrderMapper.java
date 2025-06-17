package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

  @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC")
  List<Order> selectOrdersByUserId(@Param("userId") Long userId);

  @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC")
  Page<Order> selectOrdersByUserIdWithPage(Page<Order> page, @Param("userId") Long userId);
}