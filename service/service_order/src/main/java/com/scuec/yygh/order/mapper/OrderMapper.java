package com.scuec.yygh.order.mapper;

import com.scuec.yygh.model.order.OrderInfo;
import com.scuec.yygh.vo.order.OrderCountQueryVo;
import com.scuec.yygh.vo.order.OrderCountVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface OrderMapper extends BaseMapper<OrderInfo> {
    //查询预约统计的数据
    List<OrderCountVo> selectOrderCount(@Param("vo") OrderCountQueryVo orderCountQueryVo);
}
