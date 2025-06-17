package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 预约Mapper接口
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

  @Select("SELECT r.*, la.title, u.nickname as author, la.reserved_count as reservedCount, " +
      "la.status, la.create_time as createTime, r.create_time as reservationTime " +
      "FROM reservations r " +
      "LEFT JOIN learning_activities la ON r.activity_id = la.id " +
      "LEFT JOIN users u ON la.author_id = u.id " +
      "WHERE r.user_id = #{userId} ORDER BY r.create_time DESC")
  List<Map<String, Object>> selectUserReservationsWithDetails(@Param("userId") Long userId);
}