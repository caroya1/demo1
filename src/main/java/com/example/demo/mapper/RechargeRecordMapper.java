package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.RechargeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 充值记录Mapper接口
 */
@Mapper
public interface RechargeRecordMapper extends BaseMapper<RechargeRecord> {

  @Select("SELECT rr.*, u.username, u.nickname " +
      "FROM recharge_records rr " +
      "LEFT JOIN users u ON rr.user_id = u.id " +
      "WHERE rr.user_id = #{userId} " +
      "ORDER BY rr.create_time DESC")
  List<Map<String, Object>> selectUserRechargeHistory(@Param("userId") Long userId);
}